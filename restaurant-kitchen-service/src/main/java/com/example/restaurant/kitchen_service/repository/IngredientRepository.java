package com.example.restaurant.kitchen_service.repository;

import com.example.restaurant.kitchen_service.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient,Integer> {
}
