package com.example.restaurantorderservice.http.dto.response;

import com.example.restaurantorderservice.domain.enums.OrderStatus;
import com.example.restaurantorderservice.domain.model.Order;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponseDto(
    UUID orderId,
    Instant createdAt,
    List<ItemResponseDto> items,
    double totalPrice,
    OrderStatus status
) {
    public static OrderResponseDto fromOrder(
        Order order
    ) {
        return new OrderResponseDto(
            order.getOrderId(),
            order.getCreatedAt(),
            order.getItems().stream()
                .map(ItemResponseDto::from)
                .toList(),
            order.getTotalPrice(),
            order.getOrderStatus());
    }
}
