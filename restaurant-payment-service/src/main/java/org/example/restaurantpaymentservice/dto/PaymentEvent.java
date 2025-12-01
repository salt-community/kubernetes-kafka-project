package org.example.restaurantpaymentservice.dto;

import lombok.Builder;
import org.example.restaurantpaymentservice.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Builder
public record PaymentEvent (
        UUID eventId, // unique id for the message
        UUID paymentId, // Payment.id
        UUID orderId,
        BigDecimal amount,
        String providerPaymentId,
        PaymentStatus status, // AUTHORIZED | FAILED | REFUNDED
        String failureReason, // null unless FAILED
        Instant createdAt, // original payment timestamp
        Instant occurredAt, // when we produced the event
        List<PaymentItemDto> items
){
}

