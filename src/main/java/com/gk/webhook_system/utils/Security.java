package com.gk.webhook_system.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

@Component
@Slf4j
public class Security {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    /**
     * Generate a secure random secret key
     */
    public String generateSecretKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Generate HMAC signature for webhook payload
     */
    public String generateSignature(String payload, String secretKey) {
        try {
            // Create HMAC instance
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);

            // Initialize with secret key
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    secretKey.getBytes(StandardCharsets.UTF_8),
                    HMAC_ALGORITHM
            );
            mac.init(secretKeySpec);

            // Compute the signature
            byte[] signatureBytes = mac.doFinal(
                    payload.getBytes(StandardCharsets.UTF_8)
            );

            // Encode as Base64
            return Base64.getEncoder().encodeToString(signatureBytes);

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Error generating signature", e);
            throw new RuntimeException("Failed to generate webhook signature", e);
        }
    }

    /**
     * Verify webhook signature
     */
    public boolean verifySignature(String payload, String receivedSignature, String secretKey) {
        try {
            String expectedSignature = generateSignature(payload, secretKey);

            // Use constant-time comparison to prevent timing attacks
            return MessageDigest.isEqual(
                    expectedSignature.getBytes(StandardCharsets.UTF_8),
                    receivedSignature.getBytes(StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            log.error("Error verifying signature", e);
            return false;
        }
    }
}
