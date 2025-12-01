package com.example.restaurant.kitchen_service.kafka.dto;

import com.example.restaurant.kitchen_service.enums.TicketStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.UUID;

public record KitchenInProgressEvent(
        String eventId,
        String ticketId,
        String orderId,
        TicketStatus status,
        Instant occurredAt
) implements KitchenEvent {
    public static KitchenInProgressEvent of(String ticketId, String orderId) {
        return new KitchenInProgressEvent(
                UUID.randomUUID().toString(),
                ticketId,
                orderId,
                TicketStatus.IN_PROGRESS,
                Instant.now()
        );
    }
}
