package com.gk.webhook_system.service.impl;

import com.gk.webhook_system.dto.PaymentRequest;
import com.gk.webhook_system.dto.PaymentResponse;
import com.gk.webhook_system.dto.SubscriptionResponse;
import com.gk.webhook_system.entities.Payment;
import com.gk.webhook_system.entities.WebhookSubscription;
import com.gk.webhook_system.exceptions.PaymentCreationException;
import com.gk.webhook_system.repositories.PaymentRepository;
import com.gk.webhook_system.service.EventPublisherService;
import com.gk.webhook_system.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServIceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final EventPublisherService eventPublisher;

    @Transactional
    @Override
    public PaymentResponse createPayment(PaymentRequest paymentRequest) {
        String orderId = UUID.randomUUID().toString();
        try {

            Payment payment = Payment.builder()
                    .orderId(orderId)
                    .customerId(paymentRequest.customerId())
                    .amount(paymentRequest.amount())
                    .currency(paymentRequest.currency())
                    .status(Payment.PaymentStatus.PENDING)
                    .build();

            payment = paymentRepository.save(payment);
            log.info("Created payment: {}", orderId);

            // Publish event
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("orderId", payment.getOrderId());
            eventData.put("customerId", payment.getCustomerId());
            eventData.put("amount", payment.getAmount());
            eventData.put("currency", payment.getCurrency());
            eventData.put("status", payment.getStatus());

            eventPublisher.publishEvent("payment.created", orderId, eventData);

            return mapToResponse(payment);

        } catch (PaymentCreationException e) {
            log.error("Error creating a payment...{}...", e.getMessage(), e);
            throw new PaymentCreationException("Failed to create payment. orderId=" + orderId, e);

        }
    }

    @Override
    public PaymentResponse completePayment(String orderId) {
        return null;
    }


    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .orderId(payment.getOrderId())
                .customerId(payment.getCustomerId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .build();
    }
}
