package com.gk.webhook_system.dto;

import java.time.LocalDateTime;

public record WebhookPayload(
        String eventId,
        String eventType,
        LocalDateTime timestamp
) {
}
