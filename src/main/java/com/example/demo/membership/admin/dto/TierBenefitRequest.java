package com.example.demo.membership.admin.dto;

import jakarta.validation.constraints.NotNull;

public record TierBenefitRequest(
        @NotNull Long tierId,
        @NotNull Long benefitId,
        String configValue
) {
}
