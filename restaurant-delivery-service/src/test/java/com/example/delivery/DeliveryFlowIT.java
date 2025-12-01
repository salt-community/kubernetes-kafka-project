package com.example.delivery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import com.example.delivery.domain.Delivery;
import com.example.delivery.domain.DeliveryStatus;
import com.example.delivery.events.DeliveryAssignedEvent;
import com.example.delivery.events.DeliveryCompletedEvent;
import com.example.delivery.events.OrderCanceledEvent;
import com.example.delivery.repository.DeliveryRepository;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.time.LocalDateTime;

/**
 * End-to-end IT:
 *  - uses EmbeddedKafka for topics
 *  - uses Testcontainers Postgres for real DB
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
        "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer",
        "spring.kafka.producer.properties.spring.json.add.type.headers=true",
        "spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
        "spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer",
        "spring.kafka.consumer.properties.spring.json.trusted.packages=com.example.delivery.events",
        "spring.kafka.consumer.properties.spring.json.value.default.type=com.example.delivery.events.OrderCanceledEvent",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
    }
)
@EmbeddedKafka(
    partitions = 1,
    topics = { "kitchen.prepared", "order.canceled", "delivery.assigned", "delivery.completed" }
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
class DeliveryFlowIT {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("deliverydb")
        .withUsername("delivery")
        .withPassword("delivery");

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
        r.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        r.add("app.topics.kitchenPrepared", () -> "kitchen.prepared");
        r.add("app.topics.orderCanceled", () -> "order.canceled");
        r.add("app.topics.deliveryAssigned", () -> "delivery.assigned");
        r.add("app.topics.deliveryCompleted", () -> "delivery.completed");
    }

    @SpyBean
    com.example.delivery.service.DeliveryAssignmentService service;

    @Autowired
    EmbeddedKafkaBroker embeddedKafka;

    @Autowired
    KafkaListenerEndpointRegistry registry;

    @Autowired
    TestRestTemplate rest;

    @Autowired
    DeliveryRepository deliveryRepo;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    void waitForKafkaListeners() {
        for (MessageListenerContainer c : registry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(c, embeddedKafka.getPartitionsPerTopic());
        }
    }

    private <T> Consumer<String, T> typedConsumer(Class<T> valueType, String groupId) {
        Map<String, Object> props = KafkaTestUtils.consumerProps(groupId, "true", embeddedKafka);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.delivery.events");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaConsumerFactory<>(
            props,
            new StringDeserializer(),
            new JsonDeserializer<>(valueType, false)
        ).createConsumer();
    }

    @Test
    void assignEndpoint_persistsAndPublishesAssignedEvent() {
        UUID orderId = UUID.randomUUID();

        Consumer<String, DeliveryAssignedEvent> consumer = typedConsumer(
            DeliveryAssignedEvent.class,
            "test-assigned"
        );
        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, "delivery.assigned");

        ResponseEntity<Void> resp = rest.postForEntity(
            "/api/deliveries/{orderId}/assign",
            null,
            Void.class,
            orderId
        );
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();

        var record = KafkaTestUtils.getSingleRecord(
            consumer,
            "delivery.assigned",
            Duration.ofSeconds(10)
        );
        assertThat(record.value().orderId()).isEqualTo(orderId);

        Optional<Delivery> saved = deliveryRepo.findByOrderId(orderId);
        assertThat(saved).isPresent();
        assertThat(saved.get().getStatus()).isEqualTo(DeliveryStatus.ASSIGNED);
        assertThat(saved.get().getDriverId()).isNotBlank();

        consumer.close();
    }

    @Test
    void completeEndpoint_updatesDbAndPublishesCompletedEvent() {
        UUID orderId = UUID.randomUUID();

        Delivery delivery = Delivery.builder()
            .orderId(orderId)
            .status(DeliveryStatus.ASSIGNED)
            .driverId("driver-42")
            .build();
        deliveryRepo.save(delivery);

        Consumer<String, DeliveryCompletedEvent> consumer = typedConsumer(
            DeliveryCompletedEvent.class,
            "test-completed"
        );
        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, "delivery.completed");

        ResponseEntity<Void> resp = rest.postForEntity(
            "/api/deliveries/{orderId}/complete",
            null,
            Void.class,
            orderId
        );
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();

        var record = KafkaTestUtils.getSingleRecord(
            consumer,
            "delivery.completed",
            Duration.ofSeconds(10)
        );
        assertThat(record.value().orderId()).isEqualTo(orderId);
        assertThat(record.value().driverId()).isEqualTo("driver-42");

        Delivery updated = deliveryRepo.findByOrderId(orderId).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(DeliveryStatus.COMPLETED);
        assertThat(updated.getCompletedAt()).isNotNull();

        consumer.close();
    }

    @Test
    void cancelMessage_marksDeliveryCanceled() {
        UUID orderId = UUID.randomUUID();

        deliveryRepo.save(
            Delivery.builder()
                .orderId(orderId)
                .status(DeliveryStatus.ASSIGNED)
                .driverId("driver-7")
                .build()
        );

        kafkaTemplate.send(
            "order.canceled",
            orderId.toString(),
            new OrderCanceledEvent(orderId, LocalDateTime.now(), "test")
        );

        verify(service, timeout(10000)).cancelIfActive(orderId);

        Delivery canceled = waitFor(
            () ->
                deliveryRepo
                    .findByOrderId(orderId)
                    .filter(d -> d.getStatus() == DeliveryStatus.CANCELED)
                    .orElse(null),
            Duration.ofSeconds(20)
        );
        assertThat(canceled).isNotNull();
    }

    private static <T> T waitFor(java.util.function.Supplier<T> supplier, Duration timeout) {
        long deadline = System.nanoTime() + timeout.toNanos();
        T value;
        do {
            value = supplier.get();
            if (value != null) return value;
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        } while (System.nanoTime() < deadline);
        return null;
    }
}
