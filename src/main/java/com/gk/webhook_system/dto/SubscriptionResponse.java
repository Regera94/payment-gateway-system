package com.gk.webhook_system.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SubscriptionResponse(
        Long subscriptionId,
        String clientId,
        String callBackUrl,
        String eventType,
        String secretKey,
        Boolean active,
        LocalDateTime createdAt
) {
}
