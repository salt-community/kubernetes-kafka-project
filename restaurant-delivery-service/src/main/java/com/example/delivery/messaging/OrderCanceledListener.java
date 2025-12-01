package com.example.delivery.messaging;

import com.example.delivery.events.OrderCanceledEvent;
import com.example.delivery.service.DeliveryAssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCanceledListener {

    private final DeliveryAssignmentService service;

    @KafkaListener(topics = "${app.topics.orderCanceled}")
    public void handle(OrderCanceledEvent evt) {
        log.info("Received order.canceled for order {}", evt.orderId());
        service.cancelIfActive(evt.orderId());
    }
}
