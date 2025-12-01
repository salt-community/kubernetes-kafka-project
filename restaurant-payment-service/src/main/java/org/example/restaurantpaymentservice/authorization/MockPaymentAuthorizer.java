package org.example.restaurantpaymentservice.authorization;

import org.example.restaurantpaymentservice.dto.PaymentCreateRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

/**
 * Deterministic, test-friendly rules:
 * - amount <= 0          -> FAIL "Invalid amount"
 * - amount > 10_000      -> FAIL "Amount exceeds limit"
 * - providerPaymentId contains "FAIL" (case-insensitive) -> FAIL "Provider declined"
 * - unlucky hash (≈10%)  -> FAIL "Risk engine declined" (based on providerPaymentId hash)
 * Otherwise AUTHORIZED.
 */
@Component
public class MockPaymentAuthorizer implements PaymentAuthorizer {

    @Override
    public AuthorizationResult authorize(PaymentCreateRequest req) {
        BigDecimal amount = req.amount();
        String providerId = req.providerPaymentId() == null ? "" : req.providerPaymentId();

        if (amount == null) {
            return AuthorizationResult.failure("Missing amount");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return AuthorizationResult.failure("Invalid amount");
        }
        if (amount.compareTo(new BigDecimal("10000")) > 0) {
            return AuthorizationResult.failure("Amount exceeds limit");
        }
        if (providerId.toUpperCase().contains("FAIL")) {
            return AuthorizationResult.failure("Provider declined");
        }

        // Deterministic "random" decline ~10%
        long seed = Integer.toUnsignedLong(providerId.hashCode());
        RandomGenerator rng = RandomGeneratorFactory.of("L64X128MixRandom").create(seed);
        boolean unlucky = rng.nextInt(10) == 0; // ~10%
        if (unlucky) {
            return AuthorizationResult.failure("Risk engine declined");
        }

        return AuthorizationResult.success();
    }
}

