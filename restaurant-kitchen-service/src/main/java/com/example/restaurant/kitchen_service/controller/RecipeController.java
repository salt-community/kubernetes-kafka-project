package com.example.restaurant.kitchen_service.controller;

import com.example.restaurant.kitchen_service.dto.response.PossibleRecipe;
import com.example.restaurant.kitchen_service.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recipe")
@CrossOrigin
public class RecipeController {

    RecipeService service;

    @Autowired
    public RecipeController(RecipeService service) {
        this.service = service;
    }

    @GetMapping("/getAllRecipes")
    public ResponseEntity<List<PossibleRecipe>> getAllRecipes() {
        return ResponseEntity.ok(service.getPossibleRecipes());
    }
}
