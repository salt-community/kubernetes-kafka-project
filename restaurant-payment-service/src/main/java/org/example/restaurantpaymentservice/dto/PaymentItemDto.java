package org.example.restaurantpaymentservice.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PaymentItemDto (
        UUID id,
        Integer itemId,
        int quantity,
        double price
) {
}
