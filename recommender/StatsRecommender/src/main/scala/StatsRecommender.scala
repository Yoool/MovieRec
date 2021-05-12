import DataLoader.{config, storeDFInMongoDB}
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

import java.text.SimpleDateFormat
import java.util.Date

//case class Movie(mid: Int, name: String, descri: String, timelong: String, issue: String,
//                 shoot: String, language: String, genres: String, actors: String, directors: String)
//
//case class Rating(uid: Int, mid: Int, score: Double, timestamp: Int)
//
//case class MongoConfig(uri: String, db: String)

// 推荐对象
case class Recommendation(mid: Int, score: Double)

// top10推荐列表对象
case class GenresRecommendation(genres: String, recs: Seq[Recommendation])

object StatisticsRecommender {
  //统计表 表名
  val HOT_MOVIES = "HotMovies" // 热门电影
  val RECENTLY_HOT_MOVIES = "RecentlyHotMovies" // 近期热门电影
  val AVERAGE_SCORE_MOVIES = "AverageScoreMovies" // 高平均评分电影
  val GENRES_TOP_MOVIES = "GenresTopMovies" // 按类别热门电影

  def main(args: Array[String]): Unit = {
    //    val config = Map(
    //      "spark.cores" -> "local[*]",
    //      "mongo.uri" -> "mongodb://192.168.74.128:27017/recommender",
    //      "mongo.db" -> "recommender"
    //    )

    // 创建一个sparkConf
    val sparkConf = new SparkConf().setMaster(config("spark.cores")).setAppName("StatisticsRecommeder")

    // 创建一个SparkSession
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()

    import spark.implicits._

    implicit val mongoConfig = MongoConfig(config("mongo.uri"), config("mongo.db"))

    // 从mongodb加载数据
    val movieDF = spark.read
      .option("uri", mongoConfig.uri)
      .option("collection", "Movie")
      .format("com.mongodb.spark.sql")
      .load()
      .as[Movie]
      .toDF()

    val ratingDF = spark.read
      .option("uri", mongoConfig.uri)
      .option("collection", "Rating")
      .format("com.mongodb.spark.sql")
      .load()
      .as[Rating]
      .toDF()

    // 创建名为ratings的临时表
    ratingDF.createOrReplaceTempView("ratings")

    // 1. 历史热门统计
    val hotMoviesDF = spark.sql("select mid, count(mid) as count from ratings group by mid")
    storeDFInMongoDB(hotMoviesDF, HOT_MOVIES)

    // 2. 近期热门统计
    val simpleDateFormat = new SimpleDateFormat("yyyyMM")

    // 把时间戳转换成年月格式
    spark.udf.register("changeDate", (x: Int) => simpleDateFormat.format(new Date(x * 1000L)).toInt)
    val ratingOfYearMonth = spark.sql("select mid, score, changeDate(timestamp) as yearmonth from ratings")
    ratingOfYearMonth.createOrReplaceTempView("ratingOfMonth")

    // 从ratingOfMonth中查找电影在各个月份的评分，mid，count，yearmonth
    val recentlyHotMoviesDF = spark.sql("select mid, count(mid) as count, yearmonth from ratingOfMonth group by yearmonth, mid order by yearmonth desc, count desc")

    storeDFInMongoDB(recentlyHotMoviesDF, RECENTLY_HOT_MOVIES)

    // 3. 高平均评分电影
    val averageMoviesDF = spark.sql("select mid, avg(score) as avg from ratings group by mid")
    storeDFInMongoDB(averageMoviesDF, AVERAGE_SCORE_MOVIES)

    // 4. 按类别热门电影
    val genres = List("Action", "Adventure", "Animation", "Comedy", "Crime", "Documentary", "Drama", "Family", "Fantasy", "Foreign", "History", "Horror", "Music", "Mystery"
      , "Romance", "Science", "Tv", "Thriller", "War", "Western")

    // 把平均评分加入movie表
    val movieWithScore = movieDF.join(averageMoviesDF, "mid")

    // 为做笛卡尔积，把genres转成rdd
    val genresRDD = spark.sparkContext.makeRDD(genres)

    // 类别和电影做笛卡尔积
    val genresTopMoviesDF = genresRDD.cartesian(movieWithScore.rdd)
      .filter { // 筛选包含当前类别的电影
        case (genre, movieRow) => movieRow.getAs[String]("genres").toLowerCase.contains(genre.toLowerCase)
      }
      .map { // 转换格式
        case (genre, movieRow) => (genre, (movieRow.getAs[Int]("mid"), movieRow.getAs[Double]("avg")))
      }
      .groupByKey()
      .map { // 推荐列表聚合，按第二个元素（score）降序排序
        case (genre, items) => GenresRecommendation(genre, items.toList.sortWith(_._2 > _._2).take(10)
            .map(item => Recommendation(item._1, item._2)))
      }
      .toDF()

    storeDFInMongoDB(genresTopMoviesDF, GENRES_TOP_MOVIES)

    spark.stop()
  }
}
