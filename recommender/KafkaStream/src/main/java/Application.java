import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.TopologyBuilder;

import java.util.Properties;

public class Application {
    public static void main(String[] args) {
        String brokers = "192.168.74.128:9092";
        String zookeepers = "192.168.74.128:2181";

        String from = "log";        // 输入topic
        String to = "recommender";  // 输出topic

        // Kafka Stream的配置
        Properties settings = new Properties();
        settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "logFilter");
        settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        settings.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, zookeepers);

        // Kafka Stream 配置对象
        StreamsConfig config = new StreamsConfig(settings);

        // 拓扑建构器
        TopologyBuilder builder = new TopologyBuilder();

        // 流处理的拓扑结构
        builder.addSource("SOURCE", from)
                .addProcessor("PROCESSOR", () -> new LogProcessor(), "SOURCE")
                .addSink("SINK", to, "PROCESSOR");

        KafkaStreams streams = new KafkaStreams(builder, config);

        streams.start();

    }
}
