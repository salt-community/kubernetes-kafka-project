package org.example.restaurantpaymentservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.restaurantpaymentservice.dto.PaymentCreateRequest;
import org.example.restaurantpaymentservice.dto.PaymentResponse;
import org.example.restaurantpaymentservice.enums.PaymentStatus;
import org.example.restaurantpaymentservice.model.Payment;
import org.example.restaurantpaymentservice.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@RequestBody PaymentCreateRequest request) {
        Payment saved = paymentService.authorizePayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> list(
            @RequestParam(value = "status", required = false) PaymentStatus status
    ) {
        List<PaymentResponse> result = paymentService.getAll(status).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getById(@PathVariable("id") UUID id) {
        Payment payment = paymentService.getById(id);
        return ResponseEntity.ok(toResponse(payment));
    }

    // --- helpers ---

    private PaymentResponse toResponse(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .orderId(String.valueOf(p.getOrderId()))
                .amount(p.getAmount())
                .providerPaymentId(p.getProviderPaymentId())
                .status(p.getStatus())
                .createdAt(p.getCreatedAt())
                .failureReason(p.getFailureReason())
                .build();
    }
}

