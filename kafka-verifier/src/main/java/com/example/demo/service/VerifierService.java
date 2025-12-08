package com.example.demo.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerifierService {

    private final Map<String, Instant> lastSeen = new ConcurrentHashMap<>();

    @KafkaListener(topics = "#{'${app.topics}'.split(',')}")
    public void listen(ConsumerRecord<?, ?> record) {
        lastSeen.put(record.topic(), Instant.now());
        System.out.println("order created");

    }

    public Map<String, Instant> getLastSeenMap() {
        return lastSeen;
    }
}