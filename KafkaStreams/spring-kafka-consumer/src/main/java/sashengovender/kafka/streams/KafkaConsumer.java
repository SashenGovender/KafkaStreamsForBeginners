package sashengovender.kafka.streams;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import sashengovender.kafka.streams.models.Player;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "newPlayer", groupId = "newPlayerGroup",containerFactory = "newPlayerKafkaListenerFactory")
    public void ConsumeNewPlayer(Player newPlayer) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("Message from Topic newPlayer: " + mapper.writeValueAsString(newPlayer));
        System.out.println();
    }

    @KafkaListener(topics = "newPlayerJson", groupId = "newPlayerJsonGroup",containerFactory = "newPlayerJsonKafkaListenerFactory")
    public void ConsumeNewPlayerJson(String newPlayerJson) {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("Message from Topic newPlayerJson: " + newPlayerJson);
        System.out.println();
    }
}