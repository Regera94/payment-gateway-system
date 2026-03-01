package com.gk.webhook_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SubscriptionRequest (
        @NotBlank(message = "Client ID is required")
        String clientId,
        @NotBlank(message = "Callback URL is required")
        @Pattern(regexp = "^https?://.*", message = "Callback URL must be a valid HTTP(S) URL")
        String callBackUrl,
        @NotBlank(message = "Event type is required")
        String eventType
){
}
