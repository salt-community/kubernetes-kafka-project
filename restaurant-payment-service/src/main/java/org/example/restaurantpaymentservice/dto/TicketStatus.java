package org.example.restaurantpaymentservice.dto;

import java.util.Optional;

public record TicketStatus(
        OrderStatus orderStatus,
        FoodStatus foodStatus,
        Optional<Reason> cancelReason
) {

    public enum Reason {
        CLOSED, CAPACITY, ORDER_CANCELED, PAYMENT_FAILED, OPERATOR
    }

    public enum FoodStatus {
        QUEUED,
        IN_PROGRESS,
        READY,
        HANDED_OVER
    }

    public enum OrderStatus {
        QUEUED,       // Order accepted, food not started yet
        IN_PROGRESS,  // Food is being prepared
        READY,        // Food ready for pickup
        HANDED_OVER,  // Food delivered to customer
        CANCELED,     // Order was canceled before completion
    }

    public boolean isFinished() {
        return orderStatus != OrderStatus.CANCELED && orderStatus != OrderStatus.HANDED_OVER;
    }

    public TicketStatus setStatus(OrderStatus newOrderStatus,
                                  FoodStatus newFoodStatus,
                                  Optional<Reason> optionalReason) {
        return new TicketStatus(newOrderStatus, newFoodStatus,optionalReason);
    }

    public TicketStatus cancel(Reason reason) {
        // keep current foodStatus when cancelling
        return new TicketStatus(OrderStatus.CANCELED, foodStatus, Optional.ofNullable(reason));
    }

    public TicketStatus handedOver() {
        // keep current foodStatus when completing
        return new TicketStatus(OrderStatus.HANDED_OVER, foodStatus,Optional.empty() );
    }
}
