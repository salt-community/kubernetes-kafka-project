package com.example.restaurantorderservice.messaging.eventDto;

import com.example.restaurantorderservice.domain.model.Item;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record KafkaMessageItemDto(
    @NotNull UUID id,
    @NotNull @Min(0) Integer itemId,
    @Min(1) int quantity,
    @Min(0) double price
) {
    public static KafkaMessageItemDto fromItem(Item item) {
        return new KafkaMessageItemDto(
            item.getId(),
            item.getItemId(),
            item.getQuantity(),
            item.getPrice()
        );
    }
}
