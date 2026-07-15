package com.example.demo.common.exception;

public class ActiveSubscriptionExistsException extends RuntimeException {

    public ActiveSubscriptionExistsException(String message) {
        super(message);
    }
}
