package org.example.restaurantpaymentservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.example.restaurantpaymentservice.enums.OrderStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
@Builder
public record OrderEvent(
        @JsonProperty("eventId") UUID eventId,
        @JsonProperty("orderId") UUID orderId,
        @JsonProperty("orderStatus") OrderStatus orderStatus,
        @JsonProperty("createdAt") Instant createdAt,
        @JsonProperty("totalPrice") double totalPrice,
        @JsonProperty("items") List<OrderItem> items
) {
    @Builder
    public record OrderItem(
            @JsonProperty("id") UUID id,
            @JsonProperty("itemId") int itemId,
            @JsonProperty("quantity") int quantity,
            @JsonProperty("price") double price
    ) {}
}
