package org.example.restaurantpaymentservice.repository;

import org.example.restaurantpaymentservice.enums.PaymentStatus;
import org.example.restaurantpaymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByStatus(PaymentStatus status);
    Optional<Payment> findByOrderIdAndStatus(UUID orderId, PaymentStatus status);
}
