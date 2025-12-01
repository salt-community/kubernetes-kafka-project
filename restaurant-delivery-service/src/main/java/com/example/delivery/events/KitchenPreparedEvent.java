package com.example.delivery.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record KitchenPreparedEvent(UUID orderId, LocalDateTime preparedAt) {}
