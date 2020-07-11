package sashengovender.kafka.streams.processor;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sashengovender.kafka.streams.config.KafkaConfiguration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

@Component
@EnableKafka
@EnableKafkaStreams
@EnableScheduling
public class WordCountProcessor {

    private final KafkaConfiguration kafkaConfig;
    private static Logger logger = LoggerFactory.getLogger(WordCountProcessor.class);
    private KafkaStreams wordCountKStream;

    public WordCountProcessor(KafkaConfiguration kafkaConfig) {
        System.out.println("WordCountProcessor Constructor");
        this.kafkaConfig = kafkaConfig;
    }

    @PostConstruct
    public void RunWordProcessor() {
        System.out.println("In Method RunWordProcessor");
        wordCountKStream = new KafkaStreams(this.CreateTopology(), this.GetKafkaProperties());
        wordCountKStream.start();
    }

    @PreDestroy
    public void CloseWordProcessor() {
        System.out.println("In Method CloseWordProcessor");
        wordCountKStream.close();
    }

    @Scheduled(fixedDelayString = "${spring.schedule.delay}")
    @ConditionalOnProperty(name = "schedule.reporter.enabled", matchIfMissing = true)
    public void StreamInformationScheduleReporter() {
        System.out.println("Stream Information ");
        System.out.println("Date: " + new Date() + " - " + String.valueOf(wordCountKStream));
    }

    //Sets all the required Consumer, Producer, Kafka properties
    private Properties GetKafkaProperties() {
        Properties config = new Properties();
        config.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, kafkaConfig.getStreams().getApplicationId()); // Specific fore streams application and used for
        config.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers()); // need to connect to kafka
        config.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // when application starts, when should it start consuming data from
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        return config;
    }

    //Define the stream processing that will be done on the input topic
    private Topology CreateTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> inputTextStream = builder.stream(kafkaConfig.getStreams().getInputTopic());
        inputTextStream.peek((key, value) -> System.out.println("Key: " + key + ", Value: " + value));

        KTable<String, Long> wordCounts = inputTextStream
                .mapValues(textLine -> textLine.toLowerCase())//map values to lowercase
                .flatMapValues(textLine -> Arrays.asList(textLine.split("\\W+")))//values split by space regex
                .selectKey((key, word) -> word)// change the current null key to use value as the key
                .groupByKey()//group by key before aggregation
                .count(Materialized.as("Counts"));//count occurrences

        //Write the results back to kafka
        wordCounts.toStream().to(kafkaConfig.getStreams().getOutputTopic(), Produced.with(Serdes.String(), Serdes.Long()));

        return builder.build();
    }
}
