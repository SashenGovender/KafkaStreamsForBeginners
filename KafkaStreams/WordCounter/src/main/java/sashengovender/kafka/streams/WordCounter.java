package sashengovender.kafka.streams;


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

import java.util.Arrays;
import java.util.Properties;

public class WordCounter {

    //Sets all the required Consumer, Producer, Kafka properties
    private static Properties GetKafkaProperties() {
        Properties config = new Properties();
        config.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "wordcounter-application"); // Specific fore streams application and used for
        // consumer group.id == application.id
        // prefix for internal changelog topics
        config.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092"); // need to connect to kafka
        config.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // when application starts, when should it start consuming data from
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        return config;
    }

    //Define the stream processing that will be done on the input topic
    public Topology CreateTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> inputTextStream = builder.stream("words-input");        //Get Kafka Stream

        KTable<String, Long> wordCounts = inputTextStream
                .mapValues(textLine -> textLine.toLowerCase())//map values to lowercase
                .flatMapValues(textLine -> Arrays.asList(textLine.split("\\W+")))//values split by space regex
                .selectKey((key, word) -> word)// change the current null key to use value as the key
                .groupByKey()//group by key before aggregation
                .count(Materialized.as("Counts"));//count occurrences

        //Write the results back to kafka
        wordCounts.toStream().to("words-count", Produced.with(Serdes.String(), Serdes.Long()));

        return builder.build();
    }

    public static void main(String[] args) {

        Properties config = GetKafkaProperties();

        WordCounter wordCountApp = new WordCounter();

        KafkaStreams streams = new KafkaStreams(wordCountApp.CreateTopology(), config);
        streams.start();

        System.out.println(streams.toString());//print the topology

        // shutdown hook to correctly close the streams application
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
