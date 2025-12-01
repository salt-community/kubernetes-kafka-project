package com.example.restaurantorderservice.http.dto.response;

import com.example.restaurantorderservice.domain.model.Item;

public record ItemResponseDto(int itemId, int quantity, double price) {

    public static ItemResponseDto from(Item item) {
        return new ItemResponseDto(item.getItemId(), item.getQuantity(), item.getPrice());
    }
}
