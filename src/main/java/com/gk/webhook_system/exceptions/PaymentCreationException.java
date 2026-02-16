package com.gk.webhook_system.exceptions;

public class PaymentCreationException extends RuntimeException{
    public PaymentCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
