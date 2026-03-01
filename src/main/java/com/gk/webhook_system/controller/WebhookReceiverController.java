package com.gk.webhook_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gk.webhook_system.dto.WebhookPayload;
import com.gk.webhook_system.dto.WebhookPayloadResponse;
import com.gk.webhook_system.service.WebhookReceiverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.service.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/api/test/webhook-receiver")
@RequiredArgsConstructor
@Slf4j
public class WebhookReceiverController {
 private final WebhookReceiverService webhookReceiverService;

 @PostMapping
    public ResponseEntity<WebhookPayloadResponse> receiveWebhook(
         @RequestHeader("X-Webhook-Event-Id") String eventId,
         @RequestHeader("X-Webhook-Event-Type") String eventType,
         @RequestHeader("X-Webhook-Signature") String signature,
         @RequestBody  WebhookPayload payload) {

     return ResponseEntity.ok(webhookReceiverService.receiveWebhook(payload,signature,eventType,eventId));
 }


}
