package com.example.demo.controller;

import com.example.demo.service.VerifierService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

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

    @GetMapping("/status")
    public Map<String, String> getStatus() {
        Map<String, String> report = new HashMap<>();
        Map<String, Instant> lastSeenMap = service.getLastSeenMap();
        Instant now = Instant.now();

        for (String topic : configuredTopics.split(",")) {
            if (!lastSeenMap.containsKey(topic)) {
                report.put(topic, "MISSING");
            } else {
                long seconds = Duration.between(lastSeenMap.get(topic), now).getSeconds();
                report.put(topic, seconds > timeoutSeconds ? "STALE" : "UP");
            }
        }
        return report;
    }
}