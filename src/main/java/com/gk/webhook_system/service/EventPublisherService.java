package com.gk.webhook_system.service;

import com.gk.webhook_system.entities.WebhookEvent;

import java.util.List;

public interface EventPublisherService {
    void publishEvent(String eventType, String entityId, Object data);
    List<WebhookEvent> getEvents(String entityId, String eventType);
}
