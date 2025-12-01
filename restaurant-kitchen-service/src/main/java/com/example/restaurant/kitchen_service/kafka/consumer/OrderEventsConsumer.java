package com.example.restaurant.kitchen_service.kafka.consumer;

import com.example.restaurant.kitchen_service.kafka.dto.OrderCanceledEvent;
import com.example.restaurant.kitchen_service.service.TicketService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

//no group id on methods, ATM it's sorted out in application.yml

@Component
public class OrderEventsConsumer {

    private final TicketService ticketService;

    public OrderEventsConsumer(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @KafkaListener(topics = "order.canceled.v1")
    public void onOrderCanceled(OrderCanceledEvent event){
        ticketService.onOrderCanceled(event);
    }


}
