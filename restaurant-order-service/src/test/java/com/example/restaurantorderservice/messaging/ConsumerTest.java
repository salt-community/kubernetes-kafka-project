package com.example.restaurantorderservice.messaging;

import com.example.restaurantorderservice.domain.repository.ItemRepository;
import com.example.restaurantorderservice.domain.repository.OrderRepository;
import com.example.restaurantorderservice.messaging.eventDto.PaymentFailedDto;
import com.example.restaurantorderservice.messaging.idempotent.ConsumedEventRepository;
import com.example.restaurantorderservice.messaging.outbox.OutboxRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(
    partitions = 1,
    brokerProperties = {"listeners=PLAINTEXT://localhost:9095", "port=9095"}
)
@ActiveProfiles("test")
public class ConsumerTest {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    PaymentFailedListenerTestExtender consumer;

    @MockitoBean
    OrderRepository orderRepository;

    @MockitoBean
    ItemRepository itemRepository;

    @MockitoBean
    OutboxRepository outboxRepository;

    @MockitoBean
    ConsumedEventRepository consumedEventRepository;

    @Value("${app.topic.payment.failed}")
    String failed_payment_topic;

    @Test
    void shouldConsumeTopic_whenListeningToIt() throws InterruptedException {
        //Arrange
        String payload = """
            {
            "eventId": "08243e59-a0c1-4af4-9b3b-855e7b5d3431",
            "orderId": "08243e67-a0c1-4af4-9b3b-855e7b5d3431",
            "paymentStatus": "FAILED"
            }
            """;

        //Act
        kafkaTemplate.send(failed_payment_topic, payload);

        //Assert
        boolean messageConsumed = consumer.getLatch().await(10, TimeUnit.SECONDS);
        PaymentFailedDto data = PaymentFailedDto.builder()
            .eventId(UUID.fromString("08243e59-a0c1-4af4-9b3b-855e7b5d3431"))
            .orderId("08243e67-a0c1-4af4-9b3b-855e7b5d3431")
            .paymentStatus("FAILED")
            .build();
        assertTrue(messageConsumed);
        assertEquals(data, consumer.getPayload());

    }
}
