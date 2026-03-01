package com.gk.webhook_system.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gk.webhook_system.dto.WebhookPayload;
import com.gk.webhook_system.dto.WebhookPayloadResponse;
import com.gk.webhook_system.service.WebhookReceiverService;
import com.gk.webhook_system.utils.Security;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.service.SecurityService;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
@Slf4j
public class WebhookReceiverServiceImpl implements WebhookReceiverService {
    private final ObjectMapper objectMapper;
    private final Security security;
    private static final String TEST_SECRET_KEY = "test-secret-key";


    @Override
    public WebhookPayloadResponse receiveWebhook(WebhookPayload payload, String signature, String eventType, String eventId) {

        try {
            log.info("========================================");
            log.info("📨 WEBHOOK RECEIVED!");
            log.info("Event ID: {}", payload.eventId());
            log.info("Event Type: {}", payload.eventType());
            log.info("Payload: {}", payload);
            log.info("========================================");

            String payloadJson = objectMapper.writeValueAsString(payload);

            boolean isValid = security.verifySignature(
                    payloadJson,
                    signature,
                    TEST_SECRET_KEY
            );

            if (!isValid) {
                log.error("❌ INVALID SIGNATURE!");
                return new WebhookPayloadResponse("ERROR", "Invalid signature", payload.eventId());
            }

            log.info("✓ Signature valid!");
            log.info("Event ID: {}", eventId);
            log.info("Event Type: {}", eventType);
            log.info("Payload: {}", payload);
            log.info("========================================");

            return new WebhookPayloadResponse("RECEIVED", "Webhook processed successfully!", payload.eventId());


        }
        catch (Exception e) {
            log.error("Error receiving webhook payload: {}", e.getMessage(), e);
            return new WebhookPayloadResponse("ERROR", "Failed to process webhook payload", payload.eventId());
        }
    }
}
