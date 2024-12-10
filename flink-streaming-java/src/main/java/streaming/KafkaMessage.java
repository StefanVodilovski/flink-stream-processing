package streaming;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

public class KafkaMessage {
    @JsonProperty("key")
    private String key;

    @JsonProperty("value")
    private int value;

    @JsonProperty("timestamp")
    private long timestamp;

    public KafkaMessage() {

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "KafkaMessage{" +
                "key='" + key + '\'' +
                ", value=" + value +
                ", timestamp=" + timestamp +
                '}';
    }
}
