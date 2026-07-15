package com.example.demo.membership.admin.dto;

public record TierBenefitResponse(
        Long id,
        Long tierId,
        String tierCode,
        Long benefitId,
        String benefitCode,
        String configValue,
        boolean active
) {
}
