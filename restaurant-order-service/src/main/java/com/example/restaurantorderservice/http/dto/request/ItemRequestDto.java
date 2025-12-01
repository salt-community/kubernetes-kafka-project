package com.example.restaurantorderservice.http.dto.request;

import com.example.restaurantorderservice.domain.model.Item;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemRequestDto(
    @NotNull @Min(0) Integer itemId,
    @Min(1) int quantity,
    @Min(0) double price
) {
    public Item toItem() {
        Item item = new Item();
        item.setItemId(itemId);
        item.setQuantity(quantity);
        item.setPrice(price);
        return item;
    }
}
