package com.example.demo.membership.admin.dto;

public record TierCriteriaResponse(
        Long id,
        Long tierId,
        String tierCode,
        String criteriaType,
        String configValue,
        boolean active
) {
}
