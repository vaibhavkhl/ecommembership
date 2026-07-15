package com.example.demo.membership.admin.dto;

import jakarta.validation.constraints.NotNull;

public record TierCriteriaUpdateRequest(
        String configValue,
        @NotNull Boolean active
) {
}
