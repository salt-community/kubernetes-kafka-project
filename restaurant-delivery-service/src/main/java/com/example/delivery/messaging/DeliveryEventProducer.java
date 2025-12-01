package com.example.delivery.messaging;

import com.example.delivery.events.DeliveryAssignedEvent;
import com.example.delivery.events.DeliveryCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryEventProducer {

    private final KafkaTemplate<String, Object> kafka;

    @Value("${app.topics.deliveryAssigned}")
    private String assignedTopic;

    @Value("${app.topics.deliveryCompleted}")
    private String completedTopic;

    public void publishAssigned(DeliveryAssignedEvent evt) {
        log.info("Publishing delivery.assigned for order {}", evt.orderId());
        kafka.send(assignedTopic, evt.orderId().toString(), evt);
    }

    public void publishCompleted(DeliveryCompletedEvent evt) {
        log.info("Publishing delivery.completed for order {}", evt.orderId());
        kafka.send(completedTopic, evt.orderId().toString(), evt);
    }
}
