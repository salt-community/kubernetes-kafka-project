package com.example.delivery.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderCanceledEvent(UUID orderId, LocalDateTime canceledAt, String reason) {}
