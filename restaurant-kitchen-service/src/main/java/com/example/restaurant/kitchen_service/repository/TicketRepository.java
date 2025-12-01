package com.example.restaurant.kitchen_service.repository;

import com.example.restaurant.kitchen_service.model.KitchenTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<KitchenTicket, UUID> {

    //if we later enable multiple tickets per order, we can change this optional to a list
    Optional<KitchenTicket> findByOrderId(String orderId);
}
