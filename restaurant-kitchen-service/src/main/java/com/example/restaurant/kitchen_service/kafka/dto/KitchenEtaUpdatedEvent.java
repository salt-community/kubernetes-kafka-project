package com.example.restaurant.kitchen_service.kafka.dto;

import com.example.restaurant.kitchen_service.enums.TicketStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.UUID;

public record KitchenEtaUpdatedEvent(
        String eventId,
        String ticketId,
        String orderId,
        TicketStatus status,
        Instant occurredAt,
        Instant estimatedReadyAt,
        String note
) implements KitchenEvent {
    public static KitchenEtaUpdatedEvent of(String ticketId, String orderId,
                                            TicketStatus currentStatus,
                                            Instant newEta, String note) {
        return new KitchenEtaUpdatedEvent(
                UUID.randomUUID().toString(),
                ticketId,
                orderId,
                currentStatus,
                Instant.now(),
                newEta,
                note
        );
    }
}