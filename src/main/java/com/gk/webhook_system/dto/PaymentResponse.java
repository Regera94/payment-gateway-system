package com.gk.webhook_system.dto;

import com.gk.webhook_system.entities.Payment;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PaymentResponse(
        Long id,
        String orderId,
        String customerId,
        BigDecimal amount,
        String currency,
        Payment.PaymentStatus status,
        LocalDateTime createdAt
) {
}
