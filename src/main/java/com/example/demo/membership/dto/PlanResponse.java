package com.example.demo.membership.dto;

public record PlanResponse(
        Long id,
        String code,
        int durationDays
) {
}
