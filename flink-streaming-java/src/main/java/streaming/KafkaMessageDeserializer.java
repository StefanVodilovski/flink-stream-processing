package streaming;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.functions.MapFunction;

public class KafkaMessageDeserializer  implements MapFunction<String, KafkaMessage> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public KafkaMessage map(String json) throws Exception {
        return objectMapper.readValue(json, KafkaMessage.class);
    }
}