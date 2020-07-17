package sashengovender.kafka.streams;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import sashengovender.kafka.streams.config.KafkaConfiguration;
import sashengovender.kafka.streams.processor.WordAdderProcessor;
import sashengovender.kafka.streams.processor.WordRemoverProcessor;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class SpringKafkaProcessorConfiguration {
  private final static Logger logger = LoggerFactory.getLogger(SpringKafkaProcessorConfiguration.class.getName());
  @Autowired
  private KafkaConfiguration kafkaConfig;

  @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
  public KafkaStreamsConfiguration KafkaConfiguration() {
    //Sets all the required Consumer, Producer, Kafka properties
    Map<String, Object> config = new HashMap<>();
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaConfig.getStreams().getApplicationId()); // Specific fore streams application and used for
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers()); // need to connect to kafka
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // when application starts, when should it start consuming data from
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    return new KafkaStreamsConfiguration(config);
  }

  @Bean
  public KStream<String, String> KafkaProcessor(StreamsBuilder kStreamBuilder) {
    StoreBuilder<KeyValueStore<String, String>> kvStoreBuilder = Stores.keyValueStoreBuilder(
            Stores.persistentKeyValueStore(kafkaConfig.getStreams().getStateStoreName()), Serdes.String(), Serdes.String());
    kStreamBuilder.addStateStore(kvStoreBuilder);

    KStream<String, String> wordsInputStream = kStreamBuilder.stream(kafkaConfig.getStreams().getInputTopic(),
            Consumed.with(Serdes.String(), Serdes.String()));

    DisplayInputStreamRecord(wordsInputStream);

    wordsInputStream.process(() -> new WordAdderProcessor(), kafkaConfig.getStreams().getStateStoreName());

    KStream<String, String> wordsOutputStream = wordsInputStream.transform(() -> new WordRemoverProcessor(), kafkaConfig.getStreams().getStateStoreName());
    wordsOutputStream.to(kafkaConfig.getStreams().getOutputTopic());
    return wordsInputStream;
  }

  private void DisplayInputStreamRecord(KStream<String, String> wordsInputStream) {
    //wordsInputStream.peek((key, value) -> System.out.println("Key: " + key + ", Value: " + value));
    wordsInputStream.peek((key, value) -> logger.info("Key: {}, Value: {}", key, value));

  }

}


