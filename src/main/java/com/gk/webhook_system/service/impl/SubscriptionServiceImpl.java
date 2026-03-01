package com.gk.webhook_system.service.impl;

import com.gk.webhook_system.dto.SubscriptionRequest;
import com.gk.webhook_system.dto.SubscriptionResponse;
import com.gk.webhook_system.entities.WebhookSubscription;
import com.gk.webhook_system.exceptions.SubscriptionException;
import com.gk.webhook_system.repositories.WebhookSubscriptionRepository;
import com.gk.webhook_system.service.SubscriptionService;
import com.gk.webhook_system.utils.Security;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {
    private final WebhookSubscriptionRepository webhookSubscriptionRepository;
    private final Security security;
    @Transactional
    @Override
    public SubscriptionResponse subscribe(SubscriptionRequest subscriptionRequest) {
        try{
            log.info("Creating subscription for client: {}, event: {}",
                    subscriptionRequest.clientId(), subscriptionRequest.eventType());

            Optional<WebhookSubscription> exists = webhookSubscriptionRepository
                    .findByClientId(subscriptionRequest.clientId());

            if (exists.isPresent()) {
                throw new IllegalArgumentException("Client ID already exists");
            }

            // Generate secret key
            String secretKey = security.generateSecretKey();

            // Create subscription
            WebhookSubscription subscription = WebhookSubscription.builder()
                    .clientId(subscriptionRequest.clientId())
                    .callbackUrl(subscriptionRequest.callBackUrl())
                    .eventType(subscriptionRequest.eventType())
                    .secretKey(secretKey)
                    .active(true)
                    .build();

            // Save to database
            subscription = webhookSubscriptionRepository.save(subscription);

            log.info("Created subscription ID: {}", subscription.getId());

            // Convert to response DTO
            return mapToResponse(subscription);

        }
        catch (SubscriptionException subscriptionException){
            log.error("Error: " + subscriptionException.getMessage());
            return null;
        }
    }

    @Override
    public List<SubscriptionResponse> getSubscriptions(String clientId) {
        return List.of();
    }

    @Override
    public void subscribe(Long subscriptionId) {

    }

    private SubscriptionResponse mapToResponse(WebhookSubscription subscription) {
        return SubscriptionResponse.builder()
                .subscriptionId(subscription.getId())
                .clientId(subscription.getClientId())
                .callBackUrl(subscription.getCallbackUrl())
                .eventType(subscription.getEventType())
                .secretKey(subscription.getSecretKey())
                .active(subscription.getActive())
                .createdAt(subscription.getCreatedAt())
                .build();
    }
}
