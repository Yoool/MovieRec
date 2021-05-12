import java.net.InetAddress
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{MongoClient, MongoClientURI}
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest
import org.elasticsearch.common.settings.Settings
// 新版本elasticsearch不支持InetSocketTransportAddress，改用TransportAddress
import org.elasticsearch.common.transport.InetSocketTransportAddress
//import org.elasticsearch.common.transport.TransportAddress

import org.elasticsearch.transport.client.PreBuiltTransportClient
import org.apache.spark.sql.functions._

/**
  * @param mid      电影id
  * @param name     电影名
  * @param describe 描述
  * @param duration 时长
  * @param issueD   发行日期
  * @param shootY   拍摄年份
  * @param language 语言
  * @param genres   类型
  * @param actors   演员表
  * @param director 导演
  */
case class Movie(mid: Int, name: String, describe: String, duration: String, issueD: String,
                 shootY: String, language: String, genres: String, actors: String, director: String)

/**
  * @param uid       用户id
  * @param mid       电影id
  * @param score     评分
  * @param timestamp 时间戳
  */
case class Rating(uid: Int, mid: Int, score: Double, timestamp: Int)

/**
  * @param uid       用户id
  * @param mid       电影id
  * @param tag       标签
  * @param timestamp 时间戳
  */
case class Tag(uid: Int, mid: Int, tag: String, timestamp: Int)

/**
  * @param uri MongoDB连接
  * @param db  MongoDB数据库
  */
case class MongoConfig(uri: String, db: String)

/**
  * @param httpHosts      http主机列表
  * @param transportHosts transport主机列表
  * @param index          需要操作的索引
  * @param clustername    集群名称
  */
case class ESConfig(httpHosts: String, transportHosts: String, index: String, clustername: String)

object DataLoader {
  val MOVIE_DATA_PATH = "..\\MovieRecommendSystem\\recommender\\DataLoader\\src\\main\\resources\\movies.csv"
  val RATING_DATA_PATH = "..\\MovieRecommendSystem\\recommender\\DataLoader\\src\\main\\resources\\ratings.csv"
  val TAG_DATA_PATH = "..\\MovieRecommendSystem\\recommender\\DataLoader\\src\\main\\resources\\tags.csv"
  val MONGODB_MOVIE_COLLECTION = "Movie"
  val MONGODB_RATING_COLLECTION = "Rating"
  val MONGODB_TAG_COLLECTION = "Tag"
  val ES_MOVIE_INDEX = "Movie"
  val config = Map(
    "spark.cores" -> "local[*]",
    "mongo.uri" -> "mongodb://reco:123456@192.168.74.128:27017/recommender",
    "mongo.db" -> "recommender",

    "es.httpHosts" -> "192.168.74.128:9200",
    "es.transportHosts" -> "192.168.74.128:9300",
    "es.index" -> "recommender",
    "es.cluster.name" -> "docker-cluster",

    "kafka.topic" -> "recommender"
  )

  def main(args: Array[String]): Unit = {


    // 创建一个sparkConf
    val sparkConf = new SparkConf().setMaster(config("spark.cores")).setAppName("DataLoader")

    // 如果运行的spark程序和elasitcsearch不在一个网段则加此参数，否则spark会连接失败
    sparkConf.set("es.nodes.wan.only", "true")

    // 创建一个SparkSession
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()

    import spark.implicits._

    // 加载数据
    val movieRDD = spark.sparkContext.textFile(MOVIE_DATA_PATH)
    val movieDF = movieRDD.map(
      item => {
        val attr = item.split("\\^")
        Movie(attr(0).toInt, attr(1).trim, attr(2).trim, attr(3).trim, attr(4).trim, attr(5).trim, attr(6).trim, attr(7).trim, attr(8).trim, attr(9).trim)
      }
    ).toDF()

    val ratingRDD = spark.sparkContext.textFile(RATING_DATA_PATH)
    val ratingDF = ratingRDD.map(item => {
      val attr = item.split(",")
      Rating(attr(0).toInt, attr(1).toInt, attr(2).toDouble, attr(3).toInt)
    }).toDF()

    val tagRDD = spark.sparkContext.textFile(TAG_DATA_PATH)
    val tagDF = tagRDD.map(item => {
      val attr = item.split(",")
      Tag(attr(0).toInt, attr(1).toInt, attr(2).trim, attr(3).toInt)
    }).toDF()

    implicit val mongoConfig = MongoConfig(config("mongo.uri"), config("mongo.db"))

    // 将数据保存到MongoDB
    storeDataInMongoDB(movieDF, ratingDF, tagDF)

    //左连接一列标签
    val newTag = tagDF.groupBy($"mid")
      .agg(concat_ws("|", collect_set($"tag")).as("tags"))
      .select("mid", "tags")
    val movieWithTagsDF = movieDF.join(newTag, Seq("mid"), "left")

    implicit val esConfig = ESConfig(config("es.httpHosts"), config("es.transportHosts"), config("es.index"), config("es.cluster.name"))

    // 保存数据到ES
    storeDataInES(movieWithTagsDF)

    spark.stop()
  }

  // 将数据保存到MongoDB
  def storeDataInMongoDB(movieDF: DataFrame, ratingDF: DataFrame, tagDF: DataFrame)(implicit mongoConfig: MongoConfig): Unit = {
    // 新建连接
    val mongoClient = MongoClient(MongoClientURI(mongoConfig.uri))

    // 删除重复数据库
    mongoClient(mongoConfig.db)(MONGODB_MOVIE_COLLECTION).dropCollection()
    mongoClient(mongoConfig.db)(MONGODB_RATING_COLLECTION).dropCollection()
    mongoClient(mongoConfig.db)(MONGODB_TAG_COLLECTION).dropCollection()

    // 将数据写入表
    movieDF.write
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_MOVIE_COLLECTION)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    ratingDF.write
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_RATING_COLLECTION)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    tagDF.write
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_TAG_COLLECTION)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    //对数据表建索引
    mongoClient(mongoConfig.db)(MONGODB_MOVIE_COLLECTION).createIndex(MongoDBObject("mid" -> 1))
    mongoClient(mongoConfig.db)(MONGODB_RATING_COLLECTION).createIndex(MongoDBObject("uid" -> 1))
    mongoClient(mongoConfig.db)(MONGODB_RATING_COLLECTION).createIndex(MongoDBObject("mid" -> 1))
    mongoClient(mongoConfig.db)(MONGODB_TAG_COLLECTION).createIndex(MongoDBObject("uid" -> 1))
    mongoClient(mongoConfig.db)(MONGODB_TAG_COLLECTION).createIndex(MongoDBObject("mid" -> 1))

    mongoClient.close()

  }

  // 保存DataFrame到mongo
  def storeDFInMongoDB(df: DataFrame, collection_name: String)(implicit mongoConfig: MongoConfig): Unit = {
    df.write
      .option("uri", mongoConfig.uri)
      .option("collection", collection_name)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()
  }

  // 将数据保存到ES
  def storeDataInES(movieDF: DataFrame)(implicit eSConfig: ESConfig): Unit = {
    val settings: Settings = Settings.builder().put("cluster.name", eSConfig.clustername).build()
    val esClient = new PreBuiltTransportClient(settings)
    // 正则 主机名：端口号
    val REGEX_HOST_PORT = "(.+):(\\d+)".r
    eSConfig.transportHosts.split(",").foreach {
      case REGEX_HOST_PORT(host: String, port: String) => {
         esClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port.toInt))
        // 新版本elasticsearch不支持InetSocketTransportAddress，改用TransportAddress
//        esClient.addTransportAddress(new TransportAddress(InetAddress.getByName(host), port.toInt))
      }
    }

    // 清理现有数据
    if (esClient.admin().indices().exists(new IndicesExistsRequest(eSConfig.index)).actionGet().isExists) {
      esClient.admin().indices().delete(new DeleteIndexRequest(eSConfig.index))
    }
    esClient.admin().indices().create(new CreateIndexRequest(eSConfig.index))

    movieDF.write
      .option("es.nodes", eSConfig.httpHosts)
      .option("es.http.timeout", "120m") // 超时时间
      .option("es.mapping.id", "mid") // 主键
      .mode("overwrite")
      .format("org.elasticsearch.spark.sql")
      .save(eSConfig.index + "/" + ES_MOVIE_INDEX)
  }

}
