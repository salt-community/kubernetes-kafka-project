package com.example.restaurantorderservice.exception;

import java.time.LocalDateTime;

public record ErrorResponseDto(
    String message,
    int status,
    LocalDateTime timestamp
) { }
