package com.gk.webhook_system.controller;


import com.gk.webhook_system.dto.PaymentRequest;
import com.gk.webhook_system.dto.PaymentResponse;
import com.gk.webhook_system.entities.Payment;
import com.gk.webhook_system.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/payments")
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest request) {


        PaymentResponse payment = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @PostMapping("/{orderId}/complete")
    public ResponseEntity<PaymentResponse> completePayment(@PathVariable String orderId) {
        PaymentResponse payment = paymentService.completePayment(orderId);
        return ResponseEntity.ok(payment);
    }


}
