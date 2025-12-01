package com.example.restaurantorderservice.messaging.producer;

import com.example.restaurantorderservice.messaging.outbox.OutboxEvent;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Publishes events from the Outbox to Kafka.
 */
@Component
@AllArgsConstructor
public class OrderEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(OutboxEvent event) {

        ProducerRecord<String, String> record =
            new ProducerRecord<>(event.getTopic(), String.valueOf(event.getOrderId()), event.getPayload());

        // Add eventId to headers for idempotency on consumer side
        record.headers().add("eventId", event.getEventId().toString().getBytes(StandardCharsets.UTF_8));

        kafkaTemplate.send(record);
    }
}
