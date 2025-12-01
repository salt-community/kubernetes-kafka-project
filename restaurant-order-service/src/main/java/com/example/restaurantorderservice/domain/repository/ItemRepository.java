package com.example.restaurantorderservice.domain.repository;


import com.example.restaurantorderservice.domain.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {
}
