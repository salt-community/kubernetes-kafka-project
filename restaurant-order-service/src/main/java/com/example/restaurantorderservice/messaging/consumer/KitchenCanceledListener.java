package com.example.restaurantorderservice.messaging.consumer;


import com.example.restaurantorderservice.domain.service.OrderService;
import com.example.restaurantorderservice.messaging.eventDto.KitchenCanceledDto;
import com.example.restaurantorderservice.messaging.idempotent.ConsumedEvent;
import com.example.restaurantorderservice.messaging.idempotent.ConsumedEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@AllArgsConstructor
public class KitchenCanceledListener {

    private final ObjectMapper mapper;

    private final OrderService orderService;

    private final ConsumedEventRepository consumedEventRepository;

    @KafkaListener(id = "myId33", topics = "${app.topic.kitchen.canceled}")
    public void onKitchenCanceled(ConsumerRecord<String, String> eventMessage) {

        KitchenCanceledDto dto = readKitchenCanceled(eventMessage.value());
        UUID eventId = UUID.fromString(dto.eventId());

        //Idempotency for consumed event, checking if we already have listened to the event
        if (consumedEventRepository.existsById(eventId)) return;

        UUID orderId = UUID.fromString(dto.orderId());
        orderService.cancelOrder(orderId);

        ConsumedEvent event = new ConsumedEvent(eventId, Instant.now());
        consumedEventRepository.save(event);
    }

    private KitchenCanceledDto readKitchenCanceled(String json) {
        try {
            return mapper.readValue(json, KitchenCanceledDto.class);
        } catch (JsonProcessingException e) {
            logJsonMappingError(e);
            throw new RuntimeException("Failed to parse KitchenCanceledDto", e);
        }
    }

    private void logJsonMappingError(Exception e) {
        System.out.println("EXCEPTION DESERIALIZING MESSAGE!");
        System.out.println("e.getClass() = " + e.getClass());
        System.out.println("e.getMessage() = " + e.getMessage());
        System.out.println("e.getCause() = " + e.getCause());
        e.printStackTrace();
    }
}
