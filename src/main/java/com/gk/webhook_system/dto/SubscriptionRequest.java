package com.gk.webhook_system.dto;

public record SubscriptionRequest (
   String clientId,
   String callBackUrl,
   String eventType
){
}
