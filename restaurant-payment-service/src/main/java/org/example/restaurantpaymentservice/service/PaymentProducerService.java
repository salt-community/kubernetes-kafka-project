package org.example.restaurantpaymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.restaurantpaymentservice.dto.PaymentEvent;
import org.example.restaurantpaymentservice.dto.PaymentItemDto;
import org.example.restaurantpaymentservice.model.Item;
import org.example.restaurantpaymentservice.model.Payment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProducerService {

    public static final String TOPIC_AUTHORIZED = "payment.authorized";
    public static final String TOPIC_FAILED     = "payment.failed";
    public static final String TOPIC_REFUND     = "payment.refund";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Routes the event to the correct topic based on Payment.status.
     * PENDING is not published (no topic). AUTHORIZED/FAILED/REFUNDED are published.
     */
    public void send(Payment payment) {
        if (payment == null || payment.getStatus() == null) {
            log.warn("Skipping send: payment or status is null");
            return;
        }

        String topic = switch (payment.getStatus()) {
            case AUTHORIZED -> TOPIC_AUTHORIZED;
            case FAILED     -> TOPIC_FAILED;
            case REFUNDED   -> TOPIC_REFUND;
            case PENDING    -> null; // decide: do nothing for PENDING
        };

        if (topic == null) {
            log.info("No topic for status={}, skipping publication. paymentId={}", payment.getStatus(), payment.getId());
            return;
        }

        PaymentEvent event = toEvent(payment);

        // Key by paymentId for stable partitioning
        var future = kafkaTemplate.send(topic, payment.getId().toString(), event);

        // Optional logging callback (Spring Kafka 3.x returns CompletableFuture)
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send payment event. topic={}, key={}, paymentId={}",
                        topic, payment.getId(), payment.getId(), ex);
            } else {
                log.info("Sent payment event. topic={}, key={}, partition={}, offset={}, paymentId={}",
                        topic, payment.getId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        payment.getId());
            }
        });
    }

    private PaymentEvent toEvent(Payment p) {
        return PaymentEvent.builder()
                .eventId(UUID.randomUUID())
                .paymentId(p.getId())
                .orderId(p.getOrderId())
                .amount(p.getAmount())
                .providerPaymentId(p.getProviderPaymentId())
                .status(p.getStatus())
                .failureReason(p.getFailureReason())
                .createdAt(p.getCreatedAt())
                .occurredAt(Instant.now())
                .items(
                        (p.getItems() == null) ? List.of()
                                : p.getItems().stream()
                                .map(this::toItemDto)
                                .toList()
                )
                .build();
    }

    private PaymentItemDto toItemDto(Item it) {
        return PaymentItemDto.builder()
                .id(it.getId())
                .itemId(it.getItemId())
                .quantity(it.getQuantity())
                .price(it.getPrice())
                .build();
    }
}

