package com.example.demo.membership.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record BenefitDefinitionRequest(
        @NotBlank String code,
        String description
) {
}
