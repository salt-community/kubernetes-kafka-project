package org.example.restaurantpaymentservice.idempotent;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "consumed_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsumedEvent {

    @Id
    private UUID eventId;

    private Instant consumedAt;
}