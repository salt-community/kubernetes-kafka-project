package com.example.restaurant.kitchen_service.kafka.dto;

import com.example.restaurant.kitchen_service.enums.TicketStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.UUID;

public record KitchenCanceledEvent(
        String eventId,
        String ticketId,
        String orderId,
        TicketStatus status,
        Instant occurredAt,
        TicketStatus previousStatus,
        String cancelReason
) implements KitchenEvent {
    public static KitchenCanceledEvent of(String ticketId, String orderId,
                                          TicketStatus previousStatus,
                                          String reason) {
        return new KitchenCanceledEvent(
                UUID.randomUUID().toString(),
                ticketId,
                orderId,
                TicketStatus.CANCELED,
                Instant.now(),
                previousStatus,
                reason
        );
    }
}
