import DataLoader.{MONGODB_RATING_COLLECTION, config, storeDFInMongoDB}
import org.apache.spark.{SparkConf, mllib}
import org.apache.spark.mllib.recommendation.{ALS, Rating}
import org.apache.spark.sql.SparkSession
import org.jblas.DoubleMatrix

/*基于隐语义模型的离线推荐模块*/

// 基于评分数据的LFM，只需要rating数据
case class MovieRating(uid: Int, mid: Int, score: Double, timestamp: Int)

//case class MongoConfig(uri:String, db:String)

// 推荐对象
case class Recommendation(mid: Int, score: Double)

// 基于预测评分的推荐列表
case class UserRecs(uid: Int, recs: Seq[Recommendation])

// 基于LFM电影特征向量的电影相似度列表
case class MovieRecs(mid: Int, recs: Seq[Recommendation])

object OfflineRecommender {
  val USER_RECS = "UserRecs"
  val MOVIE_RECS = "MovieRecs"

  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster(config("spark.cores")).setAppName("OfflineRecommender")

    // 创建一个SparkSession
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()

    import spark.implicits._

    implicit val mongoConfig = MongoConfig(config("mongo.uri"), config("mongo.db"))


    // 加载数据
    val ratingRDD = spark.read
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_RATING_COLLECTION)
      .format("com.mongodb.spark.sql")
      .load()
      .as[MovieRating]
      .rdd
      .map(rating => (rating.uid, rating.mid, rating.score)) // 转化成rdd，并且去掉时间戳
      .cache() // 持久化在内存中

    val userRDD = ratingRDD.map(_._1).distinct()
    val movieRDD = ratingRDD.map(_._2).distinct()

    // 训练隐语义模型
    val trainData = ratingRDD.map(x => mllib.recommendation.Rating(x._1, x._2, x._3))

    val (rank, iterations, lambda) = (300, 5, 0.1)
    val model = ALS.train(trainData, rank, iterations, lambda)

    // 基于用户和电影的隐特征，计算预测评分，得到用户的推荐列表
    // 计算user和movie的笛卡尔积，得到一个空评分矩阵
    val userMovies = userRDD.cartesian(movieRDD)

    // 调用model的predict方法预测评分
    val preRatings = model.predict(userMovies)

    val userRecs = preRatings
      .filter(_.rating > 0) // 过滤出评分大于0的项
      .map(rating => (rating.user, (rating.product, rating.rating)))
      .groupByKey() // 分组聚合
      .map {        // 推荐个数20
        case (uid, recs) => UserRecs(uid, recs.toList.sortWith(_._2 > _._2).take(20).map(x => Recommendation(x._1, x._2)))
      }
      .toDF()

    storeDFInMongoDB(userRecs, USER_RECS)

    // 基于电影隐特征，计算相似度矩阵，得到电影的相似度列表
    val movieFeatures = model.productFeatures.map {
      case (mid, features) => (mid, new DoubleMatrix(features))
    }

    // 对所有电影两两做笛卡尔积计算相似度
    val movieRecs = movieFeatures.cartesian(movieFeatures)
      .filter {
        case (a, b) => a._1 != b._1 // 过滤自己配对
      }
      .map {
        case (a, b) => {
          val simScore = this.consinSim(a._2, b._2)
          (a._1, (b._1, simScore))
        }
      }
      .filter(_._2._2 > 0.6) // 过滤出相似度大于0.6的
      .groupByKey()
      .map {
        case (mid, items) => MovieRecs(mid, items.toList.sortWith(_._2 > _._2).map(x => Recommendation(x._1, x._2)))
      }
      .toDF()

    storeDFInMongoDB(movieRecs, MOVIE_RECS)

    spark.stop()
  }

  // 求向量余弦相似度 norm2:向量模长
  def consinSim(movie1: DoubleMatrix, movie2: DoubleMatrix): Double = {
    movie1.dot(movie2) / (movie1.norm2() * movie2.norm2())
  }

}
