package com.example.restaurant.kitchen_service.repository;

import com.example.restaurant.kitchen_service.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
    @Query("SELECT r FROM Recipe r JOIN FETCH r.ingredients WHERE r.id = :id")
    Recipe findByIdWithIngredients(@Param("id") Long id);
}
