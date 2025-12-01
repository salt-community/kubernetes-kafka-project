package com.example.restaurant.kitchen_service.kafka.dto;

import com.example.restaurant.kitchen_service.enums.TicketStatus;
import com.example.restaurant.kitchen_service.model.Recipe;
import com.example.restaurant.kitchen_service.service.RecipeService;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public record KitchenPreparedEvent(
        String eventId,
        String ticketId,
        String orderId,
        TicketStatus status,
        Instant occurredAt,
        HashMap<Long, String> foodPrepared
) implements KitchenEvent {
    public static KitchenPreparedEvent of(String ticketId, String orderId, List<Recipe> craftedRecipes) {
        return new KitchenPreparedEvent(
                UUID.randomUUID().toString(),
                ticketId,
                orderId,
                TicketStatus.READY,
                Instant.now(),
                RecipeService.toFoodPreparedFormat(craftedRecipes)
        );
    }
}
