package com.example.restaurant.kitchen_service.dto.response;

import com.example.restaurant.kitchen_service.enums.TicketStatus;

import java.time.Instant;

public record TicketStatusResponse(
        String orderId,
        TicketStatus status,
        Instant estimatedReadyAt,
        Instant updatedAt
) {
}
