package com.example.demo.membership.dto;

import java.util.List;

public record TierResponse(
        Long id,
        String code,
        int rank,
        List<BenefitResponse> benefits
) {
}
