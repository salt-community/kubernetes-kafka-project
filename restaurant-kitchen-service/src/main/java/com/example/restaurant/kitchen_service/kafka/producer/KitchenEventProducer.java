package com.example.restaurant.kitchen_service.kafka.producer;

import com.example.restaurant.kitchen_service.kafka.dto.*;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KitchenEventProducer {

    private static final Logger log = LoggerFactory.getLogger(KitchenEventProducer.class);

    private final KafkaTemplate<String, Object> template;

    public KitchenEventProducer(KafkaTemplate<String, Object> template) {
        this.template = template;
    }

    private <T> void send(String topic, String key, T payload) {
        template.send(topic, key, payload).whenComplete((res, ex) -> {
            if (ex != null) {
                log.error("Failed to publish {} key={}", topic, key, ex);
            } else {
                RecordMetadata m = res.getRecordMetadata();
                log.info("Published {} key={} partition={} offset={}", topic, key, m.partition(), m.offset());
            }
        });
    }

    public void publishInProgress(KitchenInProgressEvent evt) {
        send("kitchen.in.progress", evt.orderId(), evt);
    }

    public void publishPrepared(KitchenPreparedEvent evt) {
        send("kitchen.prepared", evt.orderId(), evt);
    }

    public void publishHandedOver(KitchenHandedOverEvent evt) {
        send("kitchen.handed.over", evt.orderId(), evt);
    }

    public void publishCanceled(KitchenCanceledEvent evt) {
        send("kitchen.canceled", evt.orderId(), evt);
    }

    public void publishEtaUpdated(KitchenEtaUpdatedEvent evt) {
        send("kitchen.eta.updated", evt.orderId(), evt);
    }
}
