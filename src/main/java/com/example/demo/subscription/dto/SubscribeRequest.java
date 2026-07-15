package com.example.demo.subscription.dto;

import jakarta.validation.constraints.NotNull;

public record SubscribeRequest(
        @NotNull Long planId,
        @NotNull Long tierId
) {
}
