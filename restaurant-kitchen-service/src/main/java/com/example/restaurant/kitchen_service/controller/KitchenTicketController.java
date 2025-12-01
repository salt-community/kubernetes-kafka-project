package com.example.restaurant.kitchen_service.controller;

import com.example.restaurant.kitchen_service.dto.request.TicketDelayRequest;
import com.example.restaurant.kitchen_service.dto.response.TicketStatusResponse;
import com.example.restaurant.kitchen_service.service.KitchenTicketStatusService;
import com.example.restaurant.kitchen_service.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("api/tickets")
public class KitchenTicketController {


    private final KitchenTicketStatusService statusService;
    private final TicketService ticketService;

    public KitchenTicketController(KitchenTicketStatusService statusService, TicketService ticketService) {
        this.statusService = statusService;
        this.ticketService = ticketService;
    }


    @GetMapping("/{orderId}/status")
    public ResponseEntity<TicketStatusResponse> getByOrderId(@PathVariable String orderId) {
        return statusService.findByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    //Changes ETA but if the autopilot is on it will still set to READY/PREPARED and HAND_OVER
    @PostMapping("/{orderId}/delay")
    public ResponseEntity<Void> delay(@PathVariable String orderId,
                                      @Valid @RequestBody TicketDelayRequest body) {

        try {
            ticketService.delayByOrderId(orderId, body.minutes(), body.note());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

}
