package com.gk.webhook_system.dto;

public record WebhookPayloadResponse(
        String status,
        String message,
        String eventId
) {
}
