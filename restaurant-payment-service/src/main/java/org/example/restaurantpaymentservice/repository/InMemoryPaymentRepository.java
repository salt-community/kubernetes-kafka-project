package org.example.restaurantpaymentservice.repository;

import org.example.restaurantpaymentservice.enums.PaymentStatus;
import org.example.restaurantpaymentservice.model.Payment;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryPaymentRepository  {

    private final Map<UUID, Payment> store = new ConcurrentHashMap<>();


    public Payment save(Payment payment) {
        store.put(payment.getId(), payment);
        return payment;
    }

    public Optional<Payment> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Payment> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<Payment> findByStatus(PaymentStatus status) {
        return store.values().stream()
                .filter(p -> p.getStatus() == status)
                .toList();
    }
}

