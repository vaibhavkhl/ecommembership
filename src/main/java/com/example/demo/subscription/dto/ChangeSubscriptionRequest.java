package com.example.demo.subscription.dto;

/**
 * Both fields are optional, but at least one must be provided - enforced in the service layer
 * since "change nothing" isn't a valid request. Pass the unchanged field's current value or omit it.
 */
public record ChangeSubscriptionRequest(
        Long planId,
        Long tierId
) {
}
