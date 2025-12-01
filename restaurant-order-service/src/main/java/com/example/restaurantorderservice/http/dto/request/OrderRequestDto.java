package com.example.restaurantorderservice.http.dto.request;

import com.example.restaurantorderservice.domain.enums.OrderStatus;
import com.example.restaurantorderservice.domain.model.Order;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.time.Instant;
import java.util.List;

public record OrderRequestDto(
    @Valid @NotEmpty List<ItemRequestDto> items
) {

    public Order toOrder() {
        Order order = new Order();
        order.setOrderStatus(OrderStatus.PLACED);
        order.setCreatedAt(Instant.now());
        order.setTotalPrice(
            items.stream()
                .mapToDouble(itemRequestDto -> itemRequestDto.price() * itemRequestDto.quantity())
                .sum()
        );
        order.setItems(items.stream().map(ItemRequestDto::toItem).toList());
        return order;
    }

}
