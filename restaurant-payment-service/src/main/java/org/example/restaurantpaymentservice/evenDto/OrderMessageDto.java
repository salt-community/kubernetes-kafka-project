package org.example.restaurantpaymentservice.evenDto;

import lombok.Builder;
import org.example.restaurantpaymentservice.enums.OrderStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderMessageDto (
        UUID eventId,
        UUID orderId,
        OrderStatus orderStatus,
        Instant createdAt,
        double totalPrice,
         List<OrderItemMessageDto> items){
}
