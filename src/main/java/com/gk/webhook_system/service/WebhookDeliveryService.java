package com.gk.webhook_system.service;

import com.gk.webhook_system.entities.WebhookEvent;

public interface WebhookDeliveryService {

     void deliverWebhookAsync(Long eventId);
     void deliverWebhook(WebhookEvent event);
     void processPendingWebhooks();
}
