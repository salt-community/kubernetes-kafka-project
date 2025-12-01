package com.example.restaurant.kitchen_service.service;

import com.example.restaurant.kitchen_service.enums.TicketStatus;
import com.example.restaurant.kitchen_service.exception.IngredientsMissingException;
import com.example.restaurant.kitchen_service.kafka.dto.*;
import com.example.restaurant.kitchen_service.kafka.producer.KitchenEventProducer;
import com.example.restaurant.kitchen_service.model.KitchenTicket;
import com.example.restaurant.kitchen_service.model.Recipe;
import com.example.restaurant.kitchen_service.repository.TicketRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@Primary
@Transactional
public class TicketServiceImpl implements TicketService {

    private static final Logger log = LoggerFactory.getLogger(TicketServiceImpl.class);

    private final TicketRepository repo;
    private final KitchenEventProducer producer;
    private final TaskScheduler scheduler;
    private final RecipeService recipeService;

    //proxy use for scheduleAutopilot
    private final TicketService self;

    @Value("${kitchen.autopilot.enabled:false}")
    private boolean autopilotEnabled;

    @Value("${kitchen.autopilot.cook-seconds:0}")
    private int cookSeconds;

    @Value("${kitchen.autopilot.handover-seconds:0}")
    private int handoverSeconds;



    /*
    // simple policy parameters for now - adjust later in its own config file
    // atm we're not using any opening hours or capacity - and opening hours should be moved to another service

    private final LocalTime open = LocalTime.of(10, 0);
    private final LocalTime close = LocalTime.of(21, 0);
    private final int maxConcurrentInProgress = 5;
    private final int baseMinutes = 10;
    private final int perTicketQueueMinutes = 2;
     */

    public TicketServiceImpl(TicketRepository repo, KitchenEventProducer producer, TaskScheduler scheduler, @org.springframework.context.annotation.Lazy TicketService self, RecipeService recipeService) {
        this.repo = repo;
        this.producer = producer;
        this.scheduler = scheduler;
        this.self = self;
        this.recipeService = recipeService;
    }

    // consumers

    @Override
    public void onPaymentAuthorized(PaymentAuthorizedEvent event) {
        KitchenTicket existing = repo.findByOrderId(event.orderId()).orElse(null);
        if (existing != null) {
            switch (existing.getStatus()) {
                case IN_PROGRESS, READY, HANDED_OVER -> {
                    log.info("Ignoring payment.authorized: ticket already {} (ticketId={}, orderId={})",
                            existing.getStatus(), existing.getId(), existing.getOrderId());
                    return;
                }
                case CANCELED -> {
                    log.warn("Ignoring payment.authorized: ticket is already CANCELED (ticketId={}, orderId={})",
                            existing.getId(), existing.getOrderId());
                    return;
                }
            }
            return;
        }

        //this creates a new ticket and directly sets it to IN_PROGRESS
        KitchenTicket t = new KitchenTicket();
        t.setOrderId(event.orderId());
        t.setStatus(TicketStatus.IN_PROGRESS);


        //set initial ETA, at the moment based on autopilots cookSeconds
        if (cookSeconds > 0) {
            Instant eta = Instant.now().plusSeconds(cookSeconds);
            t.setEstimatedReadyAt(eta);
        }

        t = repo.save(t);

        // publishes IN_PROGRESS to kafka and log
        producer.publishInProgress(KitchenInProgressEvent.of(
                t.getId().toString(),
                t.getOrderId()
        ));
        log.info("Started ticket (IN_PROGRESS) ticketId={} orderId={}", t.getId(), t.getOrderId());

        //publish the eta updated if eta was set
        if (t.getEstimatedReadyAt() != null) {
            producer.publishEtaUpdated(KitchenEtaUpdatedEvent.of(
                    t.getId().toString(),
                    t.getOrderId(),
                    t.getStatus(),
                    t.getEstimatedReadyAt(),
                    "auto-init"
            ));
        }

        try {
            List<Recipe> readyFood = recipeService.craftSeveralFoods(event.items());
            t.setRecipes(readyFood);
            repo.save(t);

        } catch (IngredientsMissingException e) {
            cancel(t.getId(), "OPERATOR");
            return;
        }

        // starts autopilot and timers for ready and handover
        if (autopilotEnabled && cookSeconds > 0) {
            scheduleAutopilot(t.getId());
        }

    }


    @Override
    public void onOrderCanceled(OrderCanceledEvent event) {
        repo.findByOrderId(event.orderId()).ifPresentOrElse(ticket -> {
            if (ticket.getStatus() == TicketStatus.HANDED_OVER || ticket.getStatus() == TicketStatus.CANCELED) {
                log.info("Ignore order.canceled; ticket already {}", ticket.getStatus());
                return;
            }
            TicketStatus previous = ticket.getStatus();
            ticket.setStatus(TicketStatus.CANCELED);
            repo.save(ticket);
            producer.publishCanceled(KitchenCanceledEvent.of(
                    ticket.getId().toString(), ticket.getOrderId(), previous, "ORDER_CANCELED"
            ));
        }, () -> log.info("order.canceled ignored: no ticket for orderId={}", event.orderId()));
    }

    // operator/api actions, right now it's called on by the autopilot

    @Override
    public void ready(UUID ticketId) {
        try {
            KitchenTicket t = mustGet(ticketId);
            ensureTransition(t.getStatus(), TicketStatus.READY);
            t.setStatus(TicketStatus.READY);
            repo.save(t);
            List<Recipe> craftedRecipes = t.getRecipes();
            HashMap<Integer, String> recipes = new HashMap<>();
            producer.publishPrepared(KitchenPreparedEvent.of(t.getId().toString(), t.getOrderId(), craftedRecipes));
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }
    }

    @Override
    public void handOver(UUID ticketId) {
        try {
            KitchenTicket t = mustGet(ticketId);
            ensureTransition(t.getStatus(), TicketStatus.HANDED_OVER);
            t.setStatus(TicketStatus.HANDED_OVER);
            repo.save(t);

            producer.publishHandedOver(KitchenHandedOverEvent.of(t.getId().toString(), t.getOrderId()));
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }
    }

    @Override
    public void cancel(UUID ticketId, String reason) {
        KitchenTicket t = mustGet(ticketId);
        if (t.getStatus() == TicketStatus.HANDED_OVER || t.getStatus() == TicketStatus.CANCELED) {
            log.info("Ignore manual cancel; ticket already {}", t.getStatus());
            return;
        }

        String r = ("ORDER_CANCELED".equals(reason) || "OPERATOR".equals(reason)) ? reason : "OPERATOR";

        TicketStatus previous = t.getStatus();
        t.setStatus(TicketStatus.CANCELED);
        repo.save(t);

        producer.publishCanceled(KitchenCanceledEvent.of(t.getId().toString(), t.getOrderId(), previous, r));
    }

    @Override
    public void delay(UUID ticketId, int minutes, String note) {
        KitchenTicket t = mustGet(ticketId);
        Instant nowEta = (t.getEstimatedReadyAt() != null) ? t.getEstimatedReadyAt() : Instant.now();
        Instant newEta = nowEta.plusSeconds(minutes * 60L);
        t.setEstimatedReadyAt(newEta);
        repo.save(t);

        producer.publishEtaUpdated(KitchenEtaUpdatedEvent.of(t.getId().toString(), t.getOrderId(), t.getStatus(), newEta, note));
    }

    @Override
    public void delayByOrderId(String orderId, int minutes, String note) {
        KitchenTicket t = repo.findByOrderId(orderId).orElseThrow(() -> new IllegalArgumentException("Ticket not found for order=" + orderId));
        delay(t.getId(), minutes, note);
    }

    // helpers

    private KitchenTicket mustGet(UUID id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + id));
    }


    private void ensureTransition(TicketStatus from, TicketStatus to) {
        switch (to) {
            case IN_PROGRESS -> {
                if (from == TicketStatus.READY || from == TicketStatus.HANDED_OVER) {
                    throw new IllegalStateException("Cannot move to IN_PROGRESS from " + from);
                }
            }
            case READY -> {
                if (from != TicketStatus.IN_PROGRESS) {
                    throw new IllegalStateException("Must be IN_PROGRESS to mark READY");
                }
            }
            case HANDED_OVER -> {
                if (from != TicketStatus.READY) {
                    throw new IllegalStateException("Must be READY to hand over");
                }
            }
            case CANCELED -> {
                if (from == TicketStatus.HANDED_OVER) {
                    throw new IllegalStateException("Cannot cancel after HANDED_OVER");
                }
            }
            default -> {
            }
        }
    }

    private void scheduleAutopilot(UUID ticketId) {
        try {
            //sets READY after cookSeconds
            scheduler.schedule(() -> {
                try {
                    self.ready(ticketId);
                } catch (Exception e) {
                    log.error("Autopilot READY failed for ticketId={}", ticketId, e);
                }
            }, Instant.now().plusSeconds(cookSeconds));

            // sets HANDOVER after cookSeconds + handoverSeconds
            if (handoverSeconds > 0) {
                scheduler.schedule(() -> {
                    try {
                        handOver(ticketId);
                    } catch (Exception e) {
                        log.error("Autopilot HANDOVER failed for ticketId={}", ticketId, e);
                    }
                }, Instant.now().plusSeconds(cookSeconds + handoverSeconds));
            }


        } catch (Exception ex) {
            log.error("Failed to schedule autopilot for {}", ticketId, ex);
        }
    }
}
