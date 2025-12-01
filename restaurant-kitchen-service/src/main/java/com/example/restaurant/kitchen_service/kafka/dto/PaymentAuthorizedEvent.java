package com.example.restaurant.kitchen_service.kafka.dto;

import java.time.Instant;
import java.util.List;

public record PaymentAuthorizedEvent(
        String orderId,
        String paymentId,
        Instant occurredAt,
        List<ReceivedRecipeDto> items
) {
    public record ReceivedRecipeDto(Integer itemId, Integer quantity) {
    }
}
