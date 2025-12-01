package com.example.restaurant.kitchen_service.dto.request;

import jakarta.validation.constraints.Min;

public record TicketDelayRequest(
        @Min(1) int minutes,
        String note
) {}
