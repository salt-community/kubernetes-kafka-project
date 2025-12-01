package org.example.restaurantpaymentservice.service;

import lombok.RequiredArgsConstructor;
import org.example.restaurantpaymentservice.authorization.PaymentAuthorizer;
import org.example.restaurantpaymentservice.dto.PaymentCreateRequest;
import org.example.restaurantpaymentservice.enums.PaymentStatus;
import org.example.restaurantpaymentservice.evenDto.OrderMessageDto;
import org.example.restaurantpaymentservice.model.Item;
import org.example.restaurantpaymentservice.model.Payment;
import org.example.restaurantpaymentservice.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentAuthorizer authorizer;
    private final PaymentProducerService producerService;
    private final PaymentRepository repository;

    @Transactional
    public Payment authorizePayment(PaymentCreateRequest req) {
        UUID orderId = UUID.fromString(req.orderId());

        Payment payment = repository.findByOrderIdAndStatus(orderId, PaymentStatus.PENDING)
                .orElseThrow(() -> new IllegalStateException("No PENDING payment found for orderId=" + orderId));

        BigDecimal requested = req.amount();
        if (requested == null) {
            throw new IllegalArgumentException("Amount is required when authorizing a payment.");
        }
        if (payment.getAmount().compareTo(requested) != 0) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Amount mismatch. Expected " + payment.getAmount() + " but got " + requested);
            payment.setProviderPaymentId(req.providerPaymentId());
            Payment savedMismatch = repository.save(payment);
            producerService.send(savedMismatch);
            return savedMismatch;
        }

        var decision = authorizer.authorize(req);

        if (decision.authorized() && (req.providerPaymentId() == null || req.providerPaymentId().isBlank())) {
            throw new IllegalArgumentException("providerPaymentId is required for AUTHORIZED payments.");
        }

        payment.setProviderPaymentId(req.providerPaymentId());
        payment.setStatus(decision.authorized() ? PaymentStatus.AUTHORIZED : PaymentStatus.FAILED);
        payment.setFailureReason(decision.authorized() ? null : decision.failureReason());

        Payment saved = repository.save(payment);
        producerService.send(saved);
        return saved;
    }

    @Transactional
    public void refund(UUID paymentId, BigDecimal amount) {
        // 1. Fetch payment by its ID
        Payment payment = repository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        // 2. Ensure it hasn’t already been refunded
        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new IllegalStateException("Cannot refund an already refunded payment");
        }

        // 3. Ensure only authorized payments can be refunded
        if (payment.getStatus() != PaymentStatus.AUTHORIZED) {
            throw new IllegalStateException("Only authorized payments can be refunded");
        }

        if (amount.compareTo(payment.getAmount()) > 0) {
            throw new IllegalArgumentException("Refund amount exceeds original payment");
        }
        if (amount.compareTo(payment.getAmount()) < 0) {
            throw new IllegalArgumentException("Refund amount is less than original payment");
        }

        // 4. Update state
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setFailureReason(null); // just in case

        // 5. Persist changes
        Payment updated = repository.save(payment);

        // 6. Send event
        producerService.send(updated);

    }

    @Transactional(readOnly = true)
    public Payment getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Payment> getAll(PaymentStatus status) {
        return (status != null) ? repository.findByStatus(status) : repository.findAll();
    }

    public static class PaymentNotFoundException extends RuntimeException {
        public PaymentNotFoundException(UUID id) { super("Payment not found: " + id); }
    }
    @Transactional
    public Payment createPendingFromOrder(OrderMessageDto order) {
        var items = order.items().stream()
                .map(i -> Item.builder()
                        .id(UUID.randomUUID())
                        .itemId(i.itemId())
                        .quantity(i.quantity())
                        .price(i.price())
                        .build()
                )
                .toList();

        BigDecimal amount = items.stream()
                .map(it -> BigDecimal.valueOf(it.getPrice())
                        .multiply(BigDecimal.valueOf(it.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Payment payment = Payment.builder()
                .id(UUID.randomUUID())
                .orderId(order.orderId())
                .items(items)
                .amount(amount)
                .providerPaymentId(null)                // not known yet
                .status(PaymentStatus.PENDING)
                .failureReason(null)
                .createdAt(Instant.now())
                .build();

        return repository.save(payment);
    }
}

