package com.example.demo.membership.tier;

/**
 * Each value corresponds to a future strategy that parses {@link TierCriteria#getConfigValue()}
 * and evaluates it against a user's activity. Adding a new rule is a new enum value plus a new
 * evaluator, no schema change.
 */
public enum CriteriaType {
    MIN_ORDER_COUNT,
    MIN_ORDER_VALUE_MONTHLY,
    COHORT
}
