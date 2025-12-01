package com.example.restaurant.kitchen_service.kafka.dto;

import com.example.restaurant.kitchen_service.enums.TicketStatus;

import java.time.Instant;

public interface KitchenEvent {
    String eventId();
    String ticketId();
    String orderId();
    TicketStatus status();
    Instant occurredAt();
}
