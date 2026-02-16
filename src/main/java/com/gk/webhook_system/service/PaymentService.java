package com.gk.webhook_system.service;

import com.gk.webhook_system.dto.PaymentRequest;
import com.gk.webhook_system.dto.PaymentResponse;

public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest paymentRequest);
    PaymentResponse completePayment(String orderId);

}
