package com.example.restaurant.kitchen_service.kafka.consumer;

import com.example.restaurant.kitchen_service.exception.IngredientsMissingException;
import com.example.restaurant.kitchen_service.kafka.dto.PaymentAuthorizedEvent;
import com.example.restaurant.kitchen_service.service.TicketService;
import com.example.restaurant.kitchen_service.service.TicketServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.stereotype.Component;

//no group id on methods, ATM it's sorted out in application.yml

@Component
public class PaymentEventsConsumer {

    private final TicketService ticketService;
    private static final Logger log = LoggerFactory.getLogger(PaymentEventsConsumer.class);

    public PaymentEventsConsumer(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @KafkaListener(topics = "payment.authorized")
    public void onAuthorized(PaymentAuthorizedEvent event) {
        try {
            ticketService.onPaymentAuthorized(event);
        } catch (Exception e) {
            log.warn("Ordered '" + event.orderId() + "' canceled due to missing ingredients");
        }
    }

}
