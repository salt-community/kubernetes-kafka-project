package com.example.restaurantorderservice.messaging.outbox;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "outbox_event")
@Builder
@ToString
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID outboxId;

    @Column(nullable = false, unique = true)
    private UUID eventId;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private UUID orderId;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private boolean processed;

    private Instant processedAt;

    private String lastError;

}
