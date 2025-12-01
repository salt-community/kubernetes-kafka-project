package com.example.delivery.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryCompletedEvent(UUID orderId, String driverId, LocalDateTime completedAt) {}
