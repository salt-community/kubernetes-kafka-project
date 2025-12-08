package com.example.demo.service;

import com.example.demo.models.Event;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerifierService {

    private final Map<UUID, Event> lastSeen = new ConcurrentHashMap<>();

    @KafkaListener(topics = "#{'${app.topics}'.split(',')}")
    public void listen(ConsumerRecord<?, ?> record) {
        Event event = Event.fromConsumerRecord(record);
        lastSeen.put(event.getId(), event);
    }

    public List<Event> getEvents() {
        return (List<Event>) lastSeen.values();
    }
}