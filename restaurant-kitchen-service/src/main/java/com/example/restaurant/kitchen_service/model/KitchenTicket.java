package com.example.restaurant.kitchen_service.model;


import com.example.restaurant.kitchen_service.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "kitchen_ticket",
        indexes = {
                @Index(name = "ix_ticket_order", columnList = "order_id"),
                @Index(name = "ix_ticket_status", columnList = "status")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_ticket_order", columnNames = "order_id")
        }
)
public class KitchenTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false, updatable = false)
    private String orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private TicketStatus status = TicketStatus.IN_PROGRESS;

    @Column(name = "estimated_ready_at")
    private Instant estimatedReadyAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Version
    private Long version;

    @ManyToMany
    @JoinTable(name = "ticket_recipe", joinColumns = @JoinColumn(name = "ticket_id"), inverseJoinColumns = @JoinColumn(name = "recipe_id"))
    private List<Recipe> recipes = new ArrayList<>();


    @PrePersist
    void prePersist() {
        var now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = Instant.now();
    }

}
