package com.example.demo.subscription.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record SubscriptionResponse(
        Long id,
        Long userId,
        PlanSummary plan,
        TierSummary tier,
        String status,
        Instant startDate,
        Instant endDate,
        boolean autoRenew,
        boolean expired,
        BigDecimal pricePaid,
        String currency
) {
}
