package com.example.restaurant.kitchen_service.exception;

public class IngredientsMissingException extends RuntimeException {
    public IngredientsMissingException(String message) {
        super(message);
    }
}
