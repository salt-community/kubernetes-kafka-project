package com.example.restaurant.kitchen_service.service;

import com.example.restaurant.kitchen_service.dto.response.TicketStatusResponse;
import com.example.restaurant.kitchen_service.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KitchenTicketStatusService {

    private final TicketRepository repo;


    public KitchenTicketStatusService(TicketRepository repo) {
        this.repo = repo;
    }


    public Optional<TicketStatusResponse> findByOrderId (String orderId) {
        return repo.findByOrderId(orderId).map(t->
                new TicketStatusResponse(
                        t.getOrderId(),
                        t.getStatus(),
                        t.getEstimatedReadyAt(),
                        t.getUpdatedAt()
                )
                );
    }



}
