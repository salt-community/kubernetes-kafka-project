package org.example.restaurantpaymentservice.authorization;

import org.example.restaurantpaymentservice.dto.PaymentCreateRequest;

public interface PaymentAuthorizer {
    AuthorizationResult authorize(PaymentCreateRequest req);

    record AuthorizationResult(boolean authorized, String failureReason) {
        public static AuthorizationResult success() { return new AuthorizationResult(true, null); }
        public static AuthorizationResult failure(String reason) { return new AuthorizationResult(false, reason); }
    }
}
