package org.example.restaurantpaymentservice.dto;

import lombok.Builder;
import org.example.restaurantpaymentservice.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record PaymentResponse(UUID id, String orderId, BigDecimal amount, String providerPaymentId,
                              PaymentStatus status, Instant createdAt, String failureReason) {
}
