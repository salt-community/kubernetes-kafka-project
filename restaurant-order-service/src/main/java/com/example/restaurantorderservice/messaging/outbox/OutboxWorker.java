package com.example.restaurantorderservice.messaging.outbox;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Handle and publishes outbox events to Kafka.
 * <p>
 * - Poll the database for unprocessed outbox events (max 100 at a time).
 * - Delegate publishing of each event to OutboxEventService.
 * - Ensure each event is handled in isolation (transaction per event).
 * - Log errors if individual events fail, without stopping the batch.
 * <p>
 * Runs every 500 ms via
 */
@Component
@AllArgsConstructor
@Slf4j
public class OutboxWorker {

    private final OutboxRepository outboxRepository;

    private final OutboxEventService outboxEventService;

    @Scheduled(fixedDelay = 500)
    public void publishOutboxEvents() {

        List<OutboxEvent> outboxEvents = outboxRepository.findFirst100ByProcessedFalseOrderByCreatedAtAsc();

        for (OutboxEvent event : outboxEvents) {
            try {
                outboxEventService.publishEvent(event);

            } catch (Exception e) {
                log.error("Failed to publish outbox event {}", event.getEventId(), e);
            }
        }

    }

}
