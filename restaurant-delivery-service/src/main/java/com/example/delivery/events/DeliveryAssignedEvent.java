package com.example.delivery.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryAssignedEvent(UUID orderId, String driverId, LocalDateTime assignedAt) {}
