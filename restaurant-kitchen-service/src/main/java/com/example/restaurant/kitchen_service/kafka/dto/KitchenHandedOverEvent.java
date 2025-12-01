package com.example.restaurant.kitchen_service.kafka.dto;

import com.example.restaurant.kitchen_service.enums.TicketStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.UUID;

public record KitchenHandedOverEvent(
        String eventId,
        String ticketId,
        String orderId,
        TicketStatus status,
        Instant occurredAt
) implements KitchenEvent {
    public static KitchenHandedOverEvent of(String ticketId, String orderId) {
        return new KitchenHandedOverEvent(
                UUID.randomUUID().toString(),
                ticketId,
                orderId,
                TicketStatus.HANDED_OVER,
                Instant.now()
        );
    }
}
