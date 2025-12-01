package com.example.delivery.service;

import com.example.delivery.domain.*;
import com.example.delivery.events.DeliveryAssignedEvent;
import com.example.delivery.events.DeliveryCompletedEvent;
import com.example.delivery.messaging.DeliveryEventProducer;
import com.example.delivery.repository.DeliveryRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryAssignmentService {

    private final DeliveryRepository repo;
    private final DeliveryEventProducer producer;

    public void assignDriverFor(UUID orderId) {
        String driverId = "driver-" + Math.abs(orderId.hashCode() % 100);
        Delivery delivery = repo
            .findByOrderId(orderId)
            .orElseGet(() ->
                Delivery.builder().orderId(orderId).status(DeliveryStatus.NEW).build()
            );
        delivery.setDriverId(driverId);
        delivery.setStatus(DeliveryStatus.ASSIGNED);
        delivery.setAssignedAt(LocalDateTime.now());
        repo.save(delivery);

        producer.publishAssigned(
            new DeliveryAssignedEvent(orderId, driverId, delivery.getAssignedAt())
        );
    }

    public void cancelIfActive(UUID orderId) {
        repo
            .findByOrderId(orderId)
            .ifPresent(d -> {
                if (d.getStatus() == DeliveryStatus.ASSIGNED) {
                    d.setStatus(DeliveryStatus.CANCELED);
                    repo.save(d);
                    log.info("Delivery canceled for order {}", orderId);
                }
            });
    }

    public void complete(UUID orderId) {
        Optional<Delivery> opt = repo.findByOrderId(orderId);
        if (opt.isEmpty()) return;
        Delivery d = opt.get();
        d.setStatus(DeliveryStatus.COMPLETED);
        d.setCompletedAt(LocalDateTime.now());
        repo.save(d);
        producer.publishCompleted(
            new DeliveryCompletedEvent(orderId, d.getDriverId(), d.getCompletedAt())
        );
    }

    public Optional<Delivery> getDeliveryByOrderId(UUID orderId) {
        return repo.findByOrderId(orderId);
    }

}
