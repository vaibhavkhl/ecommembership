package com.example.demo.subscription;

/**
 * Fixed, internal set of lifecycle transitions - unlike BenefitCode/CriteriaType, these aren't
 * admin-configurable, so a real enum is appropriate here.
 */
public enum SubscriptionEventType {
    SUBSCRIBED,
    UPGRADED,
    DOWNGRADED,
    PLAN_CHANGED,
    CANCELLED
}
