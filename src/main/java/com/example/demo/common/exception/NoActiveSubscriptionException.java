package com.example.demo.common.exception;

public class NoActiveSubscriptionException extends RuntimeException {

    public NoActiveSubscriptionException(String message) {
        super(message);
    }
}
