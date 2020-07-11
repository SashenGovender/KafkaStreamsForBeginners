package sashengovender.kafka.streams.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaConfiguration {

    private String bootstrapServers;
    private KafkaStreamsConfig streams;

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public KafkaStreamsConfig getStreams() {
        return streams;
    }

    public void setStreams(KafkaStreamsConfig streams) {
        this.streams = streams;
    }
}
