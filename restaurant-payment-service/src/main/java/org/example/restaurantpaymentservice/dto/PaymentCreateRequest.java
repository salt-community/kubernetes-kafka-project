package org.example.restaurantpaymentservice.dto;


import java.math.BigDecimal;


public record PaymentCreateRequest(String orderId, BigDecimal amount, String providerPaymentId) {
}
