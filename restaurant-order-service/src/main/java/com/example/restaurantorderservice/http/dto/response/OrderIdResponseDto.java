package com.example.restaurantorderservice.http.dto.response;

import java.util.UUID;

public record OrderIdResponseDto(
    UUID orderId,
    double totalPrice
) {
}
