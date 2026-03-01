package com.gk.webhook_system.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gk.webhook_system.entities.WebhookEvent;
import com.gk.webhook_system.entities.WebhookSubscription;
import com.gk.webhook_system.exceptions.WebhookEventException;
import com.gk.webhook_system.repositories.WebhookEventRepository;
import com.gk.webhook_system.repositories.WebhookSubscriptionRepository;
import com.gk.webhook_system.service.WebhookDeliveryService;
import com.gk.webhook_system.utils.Security;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookDeliveryServiceImpl implements WebhookDeliveryService {

    private final WebhookEventRepository eventRepository;
    private final WebhookSubscriptionRepository subscriptionRepository;
    private final Security security;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Async
    @Override
    public void deliverWebhookAsync(Long eventId) {
        WebhookEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new WebhookEventException("Event not found"));

        deliverWebhook(event);

    }

    @Override
    public void deliverWebhook(WebhookEvent event) {
        log.info("Delivering webhook event {} to {}",
                event.getId(), event.getCallbackUrl());

        try {

            // Update status to IN_PROGRESS
            event.setStatus(WebhookEvent.WebhookStatus.IN_PROGRESS);
            event.setAttemptCount(event.getAttemptCount() + 1);
            event.setLastAttemptAt(LocalDateTime.now());
            eventRepository.save(event);

            // Find the subscription to get the secret key
            WebhookSubscription subscription = subscriptionRepository
                    .findByEventTypeAndActiveTrue(event.getEventType())
                    .stream()
                    .filter(s -> s.getCallbackUrl().equals(event.getCallbackUrl()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Subscription not found"));

            // Prepare the webhook payload
            Map<String, Object> webhookPayload = new HashMap<>();
            webhookPayload.put("eventId", event.getId().toString());
            webhookPayload.put("eventType", event.getEventType());
            webhookPayload.put("timestamp", LocalDateTime.now().toString());

            // Parse the stored payload and add it
            Object data = objectMapper.readValue(event.getPayload(), Object.class);
            webhookPayload.put("data", data);

            // Convert payload to JSON string
            String payloadJson = objectMapper.writeValueAsString(webhookPayload);

            // Generate signature
            String signature = security.generateSignature(
                    payloadJson,
                    subscription.getSecretKey()
            );

            webhookPayload.put("signature", signature);


            // Prepare HTTP request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Webhook-Event-Id", event.getId().toString());
            headers.set("X-Webhook-Event-Type", event.getEventType());
            headers.set("X-Webhook-Signature", signature);

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(webhookPayload, headers);

            // Send the webhook!
            ResponseEntity<String> response = restTemplate.exchange(
                    event.getCallbackUrl(),
                    HttpMethod.POST,
                    request,
                    String.class
            );

            // Check if successful (2xx response)
            if (response.getStatusCode().is2xxSuccessful()) {
                event.setStatus(WebhookEvent.WebhookStatus.SUCCESS);
                event.setCompletedAt(LocalDateTime.now());
                event.setLastError(null);
                log.info("✓ Webhook {} delivered successfully", event.getId());
            } else {
                throw new WebhookEventException("Unexpected status: " + response.getStatusCode());
            }
        }
        catch (WebhookEventException e) {
            log.error("Error delivering webhook event {} to {}: {}",
                    event.getId(), event.getCallbackUrl(), e.getMessage());

            event.setLastError(e.getMessage());
            event.setStatus(WebhookEvent.WebhookStatus.FAILED);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        eventRepository.save(event);

    }

    @Override
    public void processPendingWebhooks() {
        var pendingEvents = eventRepository.findByStatus(
                WebhookEvent.WebhookStatus.PENDING
        );

        log.info("Found {} pending webhooks to deliver", pendingEvents.size());

        for (WebhookEvent event : pendingEvents) {
            deliverWebhookAsync(event.getId());
        }
    }
}
