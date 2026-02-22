package com.gk.webhook_system.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gk.webhook_system.entities.WebhookEvent;
import com.gk.webhook_system.entities.WebhookSubscription;
import com.gk.webhook_system.repositories.WebhookEventRepository;
import com.gk.webhook_system.repositories.WebhookSubscriptionRepository;
import com.gk.webhook_system.service.EventPublisherService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisherServiceImpl implements EventPublisherService {
    private final WebhookSubscriptionRepository subscriptionRepository;
    private final WebhookEventRepository eventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @Override
    public void publishEvent(String eventType, String entityId, Object data) {
        log.info("Publishing event: type={}, entityId={}", eventType, entityId);

        // Step 1: Find all active subscriptions for this event type
        List<WebhookSubscription> subscriptions =
                subscriptionRepository.findByEventTypeAndActiveTrue(eventType);

        if (subscriptions.isEmpty()) {
            log.info("No active subscriptions found for event type: {}", eventType);
            return;
        }

        log.info("Found {} subscriptions for event type: {}",
                subscriptions.size(), eventType);

        // Step 2: Create a webhook event for each subscription
        for (WebhookSubscription subscription : subscriptions) {
            try {
                // Convert data object to JSON string
                String payload = objectMapper.writeValueAsString(data);

                // Create webhook event
                WebhookEvent event = WebhookEvent.builder()
                        .eventType(eventType)
                        .entityId(entityId)
                        .payload(payload)
                        .callbackUrl(subscription.getCallbackUrl())
                        .status(WebhookEvent.WebhookStatus.PENDING)
                        .attemptCount(0)
                        .build();

                // Save to database
                eventRepository.save(event);

                log.info("Created webhook event {} for subscription {}",
                        event.getId(), subscription.getId());

            } catch (Exception e) {
                log.error("Failed to create webhook event for subscription {}: {}",
                        subscription.getId(), e.getMessage());
            }
        }
    }

    @Override
    public List<WebhookEvent> getEvents(String entityId, String eventType) {
        return eventRepository.findByEntityIdAndEventType(entityId, eventType);

    }
}
