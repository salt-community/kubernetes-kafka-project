package com.example.restaurantorderservice.messaging;

import com.example.restaurantorderservice.domain.enums.OrderStatus;
import com.example.restaurantorderservice.domain.model.Item;
import com.example.restaurantorderservice.domain.model.Order;
import com.example.restaurantorderservice.domain.repository.ItemRepository;
import com.example.restaurantorderservice.domain.repository.OrderRepository;
import com.example.restaurantorderservice.exception.custom.JsonMapperException;
import com.example.restaurantorderservice.messaging.eventDto.KafkaMessageDto;
import com.example.restaurantorderservice.messaging.idempotent.ConsumedEventRepository;
import com.example.restaurantorderservice.messaging.outbox.OutboxEvent;
import com.example.restaurantorderservice.messaging.outbox.OutboxRepository;
import com.example.restaurantorderservice.messaging.producer.OrderEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(
    partitions = 1,
    brokerProperties = {"listeners=PLAINTEXT://localhost:9095", "port=9095"}
)
@ActiveProfiles("test")
public class ProducerTest {
    @Autowired
    private OrderEventPublisher producer;

    @MockitoBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private ItemRepository itemRepository;

    @MockitoBean
    private OutboxRepository outboxRepository;

    @MockitoBean
    private ConsumedEventRepository consumedEventRepository;

    @Value("${app.topic.order.created}")
    private String topic_order_created;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void shouldProduceTopic() {
        // ARRANGE

        UUID eventId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Instant createdAt = Instant.now();
        Instant processedAt = Instant.now();

        UUID itemUUID = UUID.randomUUID();
        int itemId = 0;

        String payload;
        try {
            payload = mapper.writeValueAsString(KafkaMessageDto.fromOrder(
                new Order(orderId, OrderStatus.PLACED, createdAt, 10, List.of(new Item(itemUUID, itemId, 1, 10))), eventId));
        } catch (JsonProcessingException e) {
            throw new JsonMapperException(e.getMessage());
        }

        OutboxEvent outboxEvent = OutboxEvent.builder()
            .eventId(eventId)
            .topic(topic_order_created)
            .orderId(orderId)
            .payload(payload)
            .createdAt(createdAt)
            .processed(true)
            .processedAt(processedAt)
            .build();

        // ACT & ASSERT

        producer.send(outboxEvent);

        ArgumentCaptor<ProducerRecord<String, String>> captor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate, timeout(1000)).send(captor.capture());
        ProducerRecord<String, String> record = captor.getValue();

        assertEquals(topic_order_created, record.topic());
        assertEquals(payload, record.value());
    }
}
