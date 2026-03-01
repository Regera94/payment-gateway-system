package com.gk.webhook_system.service;

import com.gk.webhook_system.dto.WebhookPayload;
import com.gk.webhook_system.dto.WebhookPayloadResponse;

public interface WebhookReceiverService {

    WebhookPayloadResponse receiveWebhook(WebhookPayload payload, String signature, String eventType, String eventId);
}
