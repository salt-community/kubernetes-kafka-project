package com.example.restaurantorderservice.messaging.eventDto;

import com.example.restaurantorderservice.domain.enums.OrderStatus;
import com.example.restaurantorderservice.domain.model.Order;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record KafkaMessageDto(
    @NotNull UUID eventId,
    @NotNull UUID orderId,
    @NotNull OrderStatus orderStatus,
    @NotNull Instant createdAt,
    @Min(0) double totalPrice,
    @NotNull @NotEmpty List<KafkaMessageItemDto> items
) {
    public static KafkaMessageDto fromOrder(Order order, UUID eventId) {
        return new KafkaMessageDto(
            eventId,
            order.getOrderId(),
            order.getOrderStatus(),
            order.getCreatedAt(),
            order.getTotalPrice(),
            order.getItems().stream().map(KafkaMessageItemDto::fromItem).toList()
        );
    }
}
