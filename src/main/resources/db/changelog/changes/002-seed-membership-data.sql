--liquibase formatted sql

--changeset vaibhavkohli:011-seed-membership-plan
INSERT INTO membership_plan (code, duration_days, is_active) VALUES
    ('MONTHLY', 30, TRUE),
    ('QUARTERLY', 90, TRUE),
    ('YEARLY', 365, TRUE);

--changeset vaibhavkohli:012-seed-membership-tier
INSERT INTO membership_tier (code, rank, is_active) VALUES
    ('SILVER', 1, TRUE),
    ('GOLD', 2, TRUE),
    ('PLATINUM', 3, TRUE);

--changeset vaibhavkohli:013-seed-benefit-definition
INSERT INTO benefit_definition (code, description) VALUES
    ('FREE_DELIVERY', 'Free delivery on eligible orders'),
    ('DISCOUNT_PERCENT', 'Extra percentage discount on selected items or categories'),
    ('EARLY_ACCESS', 'Early access to sales and exclusive deals'),
    ('PRIORITY_SUPPORT', 'Priority customer support'),
    ('EXCLUSIVE_COUPON', 'Access to exclusive member-only coupons');

--changeset vaibhavkohli:014-seed-tier-benefit
INSERT INTO tier_benefit (tier_id, benefit_id, config_value, is_active)
SELECT t.id, b.id, v.config_value, TRUE
FROM (VALUES
    ('SILVER', 'FREE_DELIVERY', NULL),
    ('GOLD', 'FREE_DELIVERY', NULL),
    ('GOLD', 'DISCOUNT_PERCENT', '5'),
    ('GOLD', 'EARLY_ACCESS', NULL),
    ('PLATINUM', 'FREE_DELIVERY', NULL),
    ('PLATINUM', 'DISCOUNT_PERCENT', '10'),
    ('PLATINUM', 'EARLY_ACCESS', NULL),
    ('PLATINUM', 'PRIORITY_SUPPORT', NULL),
    ('PLATINUM', 'EXCLUSIVE_COUPON', NULL)
) AS v(tier_code, benefit_code, config_value)
JOIN membership_tier t ON t.code = v.tier_code
JOIN benefit_definition b ON b.code = v.benefit_code;

--changeset vaibhavkohli:015-seed-tier-criteria
INSERT INTO tier_criteria (tier_id, criteria_type, config_value, is_active)
SELECT t.id, v.criteria_type, v.config_value, TRUE
FROM (VALUES
    ('GOLD', 'MIN_ORDER_COUNT', '5'),
    ('GOLD', 'MIN_ORDER_VALUE_MONTHLY', '3000'),
    ('PLATINUM', 'MIN_ORDER_COUNT', '15'),
    ('PLATINUM', 'MIN_ORDER_VALUE_MONTHLY', '10000'),
    ('PLATINUM', 'COHORT', 'VIP')
) AS v(tier_code, criteria_type, config_value)
JOIN membership_tier t ON t.code = v.tier_code;

--changeset vaibhavkohli:016-seed-plan-tier-pricing
INSERT INTO plan_tier_pricing (plan_id, tier_id, price, currency, effective_from, effective_to)
SELECT p.id, t.id, v.price, 'INR', now(), NULL
FROM (VALUES
    ('MONTHLY', 'SILVER', 99.00),
    ('MONTHLY', 'GOLD', 199.00),
    ('MONTHLY', 'PLATINUM', 349.00),
    ('QUARTERLY', 'SILVER', 249.00),
    ('QUARTERLY', 'GOLD', 499.00),
    ('QUARTERLY', 'PLATINUM', 899.00),
    ('YEARLY', 'SILVER', 799.00),
    ('YEARLY', 'GOLD', 1599.00),
    ('YEARLY', 'PLATINUM', 2999.00)
) AS v(plan_code, tier_code, price)
JOIN membership_plan p ON p.code = v.plan_code
JOIN membership_tier t ON t.code = v.tier_code;

--changeset vaibhavkohli:017-seed-demo-user
INSERT INTO app_user (name, email, cohort_code, created_at) VALUES
    ('Demo User', 'demo.user@test.com', 'VIP', now());
