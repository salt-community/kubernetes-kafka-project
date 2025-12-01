package com.example.restaurantorderservice.messaging.eventDto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PaymentFailedDto(
    UUID eventId, // unique id for the message
    String orderId,
    String paymentStatus // AUTHORIZED | FAILED | REFUNDED
) {
}
