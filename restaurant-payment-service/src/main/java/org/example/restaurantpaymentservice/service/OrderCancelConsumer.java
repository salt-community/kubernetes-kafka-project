package org.example.restaurantpaymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.restaurantpaymentservice.dto.OrderEvent;
import org.example.restaurantpaymentservice.idempotent.ConsumedEvent;
import org.example.restaurantpaymentservice.idempotent.ConsumedEventRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

@Component
@Slf4j
public class OrderCancelConsumer {
    private final ObjectMapper mapper;
    private final PaymentService paymentService;
    private final ConsumedEventRepository consumedEventRepository;


    public OrderCancelConsumer(ObjectMapper mapper, PaymentService paymentService, ConsumedEventRepository consumedEventRepository) {
        this.mapper = mapper;
        this.paymentService = paymentService;
        this.consumedEventRepository = consumedEventRepository;
    }

    @KafkaListener(topics = "order.canceled.v1")
    public void listen(ConsumerRecord<String, String> record) throws JsonProcessingException {
        System.out.printf(
                "Topic: %s, Partition: %d, Offset: %d, Message: %s%n",
                record.topic(),
                record.partition(),
                record.offset(),
                record.value()
        );

        String json = record.value();
        ObjectReader reader = mapper
            .readerFor(OrderEvent.class)
            .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        OrderEvent event = reader.readValue(json);

        if (consumedEventRepository.existsById(event.eventId())) {
            log.info("Duplicate event detected, skipping.");
            return;
        }
        ConsumedEvent consumedEvent = new ConsumedEvent(event.eventId(), Instant.now());
        consumedEventRepository.save(consumedEvent);


        handleOrderCanceled(event);
    }

    private void handleOrderCanceled(OrderEvent event) {
        System.out.println("Processing Order.canceled: " + event);
        BigDecimal payment = BigDecimal.valueOf(event.totalPrice());
        paymentService.refund(event.eventId(),payment);
    }
}
