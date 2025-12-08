package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
public class Event {
    @JsonProperty("eventId")
    private UUID id;
    @JsonProperty("orderId")
    private String orderId;
    @JsonProperty("replicaOrigin")
    private String replicaOrigin;
    @JsonProperty("topic")
    private String topic;

    public static Event fromConsumerRecord(ConsumerRecord<?, ?> record) {
        ObjectMapper mapper = new ObjectMapper();
        String key = record.key().toString();
        String value  = record.value().toString();

        Event event =  mapper.readValue(value, Event.class);
        event.setTopic(key);
        return event;
    }
}


