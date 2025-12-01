package com.example.restaurantorderservice.domain.repository;


import com.example.restaurantorderservice.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}
