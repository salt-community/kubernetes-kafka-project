package com.example.restaurantorderservice.messaging.outbox;

import com.example.restaurantorderservice.messaging.producer.OrderEventPublisher;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@AllArgsConstructor
public class OutboxEventService {

    private final OrderEventPublisher orderEventPublisher;
    private final OutboxRepository outboxRepository;

    @Transactional
    public void publishEvent(OutboxEvent event) {
        try {
            orderEventPublisher.send(event);
            event.setProcessed(true);
            event.setProcessedAt(Instant.now());
            event.setLastError(null);
        } catch (Exception e) {
            event.setLastError(e.getMessage());
        } finally {
            outboxRepository.save(event);
        }
    }
}

