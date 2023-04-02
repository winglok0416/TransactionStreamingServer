package hk.edu.cuhk;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.sparkproject.guava.util.concurrent.AtomicDouble;

public class TransactionStreamingServer {
  private static final AtomicLong runningTotalVolume = new AtomicLong(0);
  private static final AtomicDouble runningMaxPrice = new AtomicDouble(Double.MIN_VALUE);
  private static final AtomicDouble runningMinPrice = new AtomicDouble(Double.MAX_VALUE);
  private static final String OUTPUT_PATH = "/opt/bitnami/spark/tmp/result.txt";

  public static final String TOPIC = "transaction";
  public static final String BOOTSTRAP_SERVERS = "kafka:9091";
  public static final String GROUP_ID = "group-01";

  public static void main(String[] args)
      throws InterruptedException, FileNotFoundException {
    PrintWriter qualityWriter = new PrintWriter(new FileOutputStream(OUTPUT_PATH, true));
    qualityWriter.println("currentTime , currentPrice , totalVolume, maxPrice, minPrice");
    qualityWriter.close();

    SparkConf sparkConf = new SparkConf().setAppName("TransactionAnalyzer");
    JavaStreamingContext ssc = new JavaStreamingContext(sparkConf, Durations.minutes(1L));

    Map<String, Object> kafkaParams = new HashMap<>();
    kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
    kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
    kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

    Set<String> topicSet = Collections.singleton(TOPIC);

    JavaInputDStream<ConsumerRecord<String, String>> messages = KafkaUtils.createDirectStream(
        ssc,
        LocationStrategies.PreferConsistent(),
        ConsumerStrategies.Subscribe(
            topicSet,
            kafkaParams
        )
    );

    JavaDStream<StreamingInput> streamingInputs = messages
        .map((ConsumerRecord<String, String> msg) -> StreamingInputDeserializer.readValue(msg.value()))
        .cache();


    streamingInputs.foreachRDD((JavaRDD<StreamingInput> rdd) -> {
      String data = rdd.collect().stream().map((StreamingInput input) -> {
        runningTotalVolume.getAndAdd(input.getVolume());
        runningMaxPrice.getAndSet(Math.max(runningMaxPrice.get(), input.getPrice()));
        runningMinPrice.getAndSet(Math.min(runningMinPrice.get(), input.getPrice()));

        return input.getTime() + ", " + input.getPrice()
            + ", " + runningTotalVolume + ", " + runningMaxPrice + ", "
            + runningMinPrice;
      }).collect(Collectors.joining("\n"));

      if (!data.isEmpty()) {
        data = data.concat("\n");
      }

      PrintWriter handleWriter = new PrintWriter(new FileOutputStream(OUTPUT_PATH, true));
      handleWriter.append(data);
      handleWriter.close();
    });

    ssc.start();
    ssc.awaitTermination();
  }
}