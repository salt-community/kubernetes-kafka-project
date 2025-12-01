package org.example.restaurantpaymentservice.evenDto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderItemMessageDto(
        UUID id,
        Integer itemId,
        int quantity,
        double price
) {
}
