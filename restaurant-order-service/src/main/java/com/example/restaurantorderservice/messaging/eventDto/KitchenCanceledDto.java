package com.example.restaurantorderservice.messaging.eventDto;

import java.time.Instant;

public record KitchenCanceledDto(
    String eventId, // unique id for the message
    String ticketId, // Payment.id
    String orderId,
    String ticketStatus,
    Instant occurredAt,
    String previousStatus,
    String cancelReason
) {
}
