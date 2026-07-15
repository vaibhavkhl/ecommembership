package com.example.demo.membership.dto;

import java.math.BigDecimal;

public record PriceResponse(
        Long planId,
        String planCode,
        Long tierId,
        String tierCode,
        BigDecimal price,
        String currency
) {
}
