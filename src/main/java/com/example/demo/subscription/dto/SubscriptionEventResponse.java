package com.example.demo.subscription.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record SubscriptionEventResponse(
        Long id,
        String eventType,
        String fromPlanCode,
        String toPlanCode,
        String fromTierCode,
        String toTierCode,
        BigDecimal pricePaid,
        String currency,
        Instant createdAt
) {
}
