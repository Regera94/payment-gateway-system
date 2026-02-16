package com.gk.webhook_system.utils;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class Security {
    /**
     * Generate a secure random secret key
     */
    public String generateSecretKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
