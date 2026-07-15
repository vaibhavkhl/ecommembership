package com.example.demo.membership.benefit;

/**
 * Catalog of benefit types. Adding a perk is a new value here plus a strategy that interprets
 * {@link TierBenefit#getConfigValue()} for it - no schema change required.
 */
public enum BenefitCode {
    FREE_DELIVERY,
    DISCOUNT_PERCENT,
    EARLY_ACCESS,
    PRIORITY_SUPPORT,
    EXCLUSIVE_COUPON
}
