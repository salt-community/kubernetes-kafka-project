package com.example.demo.controller;

import com.example.demo.models.Event;
import com.example.demo.service.VerifierService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class StatusController {

    private final VerifierService service;

    @Value("${app.topics}")
    private String configuredTopics;

    @Value("${app.timeout}")
    private long timeoutSeconds;

    public StatusController(VerifierService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Void> health() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/events")
    public List<Event> getEvents() {
        return service.getEvents();
    }
}