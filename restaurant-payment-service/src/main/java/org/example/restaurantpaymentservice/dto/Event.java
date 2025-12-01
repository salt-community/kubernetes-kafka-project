package org.example.restaurantpaymentservice.dto;

import org.example.restaurantpaymentservice.enums.OrderStatus;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

public interface Event {
    UUID getOrderId();
    BigDecimal getPaymentAmount();
    OrderStatus getOrderStatus();
}
