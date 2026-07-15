package com.example.demo.membership.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TierCriteriaRequest(
        @NotNull Long tierId,
        @NotBlank String criteriaType,
        String configValue
) {
}
