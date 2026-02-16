package com.gk.webhook_system.dto;

import java.math.BigDecimal;

public record PaymentRequest(
        String customerId,
        BigDecimal amount,
        String currency
) {
}
