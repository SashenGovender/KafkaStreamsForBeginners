package sashengovender.kafka.streams;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.Arrays;
import java.util.Properties;

public class FavouriteFoodApplication {

    //Sets all the required Consumer, Producer, Kafka properties
    private static Properties GetKafkaProperties() {
        Properties config = new Properties();
        config.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "favouritefood-application");
        config.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        config.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        return config;
    }

    //Define the stream processing that will be done on the input topic
    public Topology CreateTopology() {

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> inputFoodAndUsersStream = builder.stream("favouritefood");// stream of username,food

        KStream<String, String> validUserFoodStream = inputFoodAndUsersStream
                .filter((foodKey, foodValue) -> foodValue.contains(","))// check if we have a comma
                .selectKey((foodKey, foodValue) -> foodValue.split(",")[0].toLowerCase())//set the current null key to be the userid
                .mapValues(foodValue -> foodValue.split(",")[1].toLowerCase())// set the users food
                .filterNot((foodKey, foodValue) -> Arrays.asList("chocolate", "biscuit", "icecream").contains(foodValue));//These are not foods!

        validUserFoodStream.to("valid-favouritefood");//publish to new topic, so we can read as a Ktable

        KTable<String, String> validFavouriteFoodTable = builder.table("valid-favouritefood");//read as KTable so that we only do updates

        //Count all the favourite foods
        KTable<String, Long> validFavouriteFoods = validFavouriteFoodTable
                .groupBy((userKey, userFoodValue) -> new KeyValue<>(userFoodValue, userFoodValue))
                .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("CountByFood")
                        .withKeySerde(Serdes.String())
                        .withValueSerde(Serdes.Long()));

        validFavouriteFoods.toStream().to("favouritefood-count", Produced.with(Serdes.String(),Serdes.Long()));

        return builder.build();
    }

    public static void main(String[] args) {

        Properties config = GetKafkaProperties();

        FavouriteFoodApplication favouriteFoodApplication = new FavouriteFoodApplication();

        KafkaStreams streams = new KafkaStreams(favouriteFoodApplication.CreateTopology(), config);
        streams.start();

        streams.localThreadsMetadata().forEach(data -> System.out.println(data.toString()));//print the topology

        // shutdown hook to correctly close the streams application
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
