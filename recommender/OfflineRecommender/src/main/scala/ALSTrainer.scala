import DataLoader.{MONGODB_RATING_COLLECTION, config}
import breeze.numerics.sqrt
import org.apache.spark.{SparkConf, mllib}
import org.apache.spark.mllib.recommendation.{ALS, MatrixFactorizationModel, Rating}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object ALSTrainer {
  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setMaster(config("spark.cores")).setAppName("OfflineRecommender")
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()

    implicit val mongoConfig = MongoConfig(config("mongo.uri"), config("mongo.db"))

    //注意要放在全局的位置，尤其是方法调用
    import spark.implicits._

    // 加载评分数据
    val ratingRDD = spark.read
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_RATING_COLLECTION)
      .format("com.mongodb.spark.sql")
      .load()
      .as[MovieRating]
      .rdd
      .map(rating => mllib.recommendation.Rating(rating.uid, rating.mid, rating.score)) // 转化成rdd，并且去掉时间戳
      .cache()

    // 随机切分数据集，生成训练集和测试集
    val splits = ratingRDD.randomSplit(Array(0.8, 0.2))
    val trainingRDD = splits(0)
    val testRDD = splits(1)

    // 模型参数选择，输出最优参数
    adjustALSParam(trainingRDD, testRDD)

    spark.close()
  }

  def adjustALSParam(trainData: RDD[mllib.recommendation.Rating], testData: RDD[mllib.recommendation.Rating]): Unit = {
    // 取值范围
    val result = for (rank <- Array(50, 100, 200, 300); lambda <- Array(0.01, 0.1, 1))
      yield {
        val model = ALS.train(trainData, rank, 5, lambda)
        // 计算当前参数对应模型的rmse，返回Double
        val rmse = getRMSE(model, testData)
        (rank, lambda, rmse)
      }
    // 控制台打印输出最优参数
    println(result.minBy(_._3))
  }

  def getRMSE(model: MatrixFactorizationModel, data: RDD[mllib.recommendation.Rating]): Double = {
    // 计算预测评分
    val userProducts = data.map(item => (item.user, item.product))
    val predictRating = model.predict(userProducts)

    // 以uid，mid作为外键，inner join实际观测值和预测值
    val observed = data.map(item => ((item.user, item.product), item.rating))
    val predict = predictRating.map(item => ((item.user, item.product), item.rating))
    // 内连接得到(uid, mid),(actual, predict)
    sqrt(
      observed.join(predict).map {
        case ((uid, mid), (actual, pre)) =>
          val err = actual - pre
          err * err
      }.mean()
    )
  }
}
