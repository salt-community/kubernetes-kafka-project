package com.example.delivery.messaging;

import com.example.delivery.events.KitchenPreparedEvent;
import com.example.delivery.service.DeliveryAssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KitchenPreparedListener {

    private final DeliveryAssignmentService service;

    @KafkaListener(topics = "${app.topics.kitchenPrepared}")
    public void handle(KitchenPreparedEvent evt) {
        log.info("Received kitchen.prepared for order {}", evt.orderId());
        service.assignDriverFor(evt.orderId());
    }
}
