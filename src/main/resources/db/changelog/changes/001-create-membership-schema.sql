--liquibase formatted sql

--changeset vaibhavkohli:001-create-app-user
CREATE TABLE app_user
(
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    email        VARCHAR(255) NOT NULL,
    cohort_code  VARCHAR(64),
    created_at   TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT uq_app_user_email UNIQUE (email)
);

--changeset vaibhavkohli:002-create-membership-plan
CREATE TABLE membership_plan
(
    id            BIGSERIAL PRIMARY KEY,
    code          VARCHAR(32)  NOT NULL,
    duration_days INTEGER      NOT NULL,
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_membership_plan_code UNIQUE (code)
);

--changeset vaibhavkohli:003-create-membership-tier
CREATE TABLE membership_tier
(
    id        BIGSERIAL PRIMARY KEY,
    code      VARCHAR(32) NOT NULL,
    rank      INTEGER     NOT NULL,
    is_active BOOLEAN     NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_membership_tier_code UNIQUE (code),
    CONSTRAINT uq_membership_tier_rank UNIQUE (rank)
);

--changeset vaibhavkohli:004-create-plan-tier-pricing
CREATE TABLE plan_tier_pricing
(
    id              BIGSERIAL PRIMARY KEY,
    plan_id         BIGINT         NOT NULL REFERENCES membership_plan (id),
    tier_id         BIGINT         NOT NULL REFERENCES membership_tier (id),
    price           NUMERIC(12, 2) NOT NULL,
    currency        VARCHAR(3)     NOT NULL DEFAULT 'INR',
    effective_from  TIMESTAMP      NOT NULL DEFAULT now(),
    effective_to    TIMESTAMP
);

--changeset vaibhavkohli:005-create-plan-tier-pricing-current-price-index
CREATE UNIQUE INDEX uq_plan_tier_pricing_current
    ON plan_tier_pricing (plan_id, tier_id)
    WHERE effective_to IS NULL;

--changeset vaibhavkohli:006-create-benefit-definition
CREATE TABLE benefit_definition
(
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(64)  NOT NULL,
    description VARCHAR(255),
    CONSTRAINT uq_benefit_definition_code UNIQUE (code)
);

--changeset vaibhavkohli:007-create-tier-benefit
CREATE TABLE tier_benefit
(
    id           BIGSERIAL PRIMARY KEY,
    tier_id      BIGINT      NOT NULL REFERENCES membership_tier (id),
    benefit_id   BIGINT      NOT NULL REFERENCES benefit_definition (id),
    config_value VARCHAR(255),
    is_active    BOOLEAN     NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_tier_benefit UNIQUE (tier_id, benefit_id)
);

--changeset vaibhavkohli:008-create-tier-criteria
CREATE TABLE tier_criteria
(
    id            BIGSERIAL PRIMARY KEY,
    tier_id       BIGINT      NOT NULL REFERENCES membership_tier (id),
    criteria_type VARCHAR(64) NOT NULL,
    config_value  VARCHAR(255),
    is_active     BOOLEAN     NOT NULL DEFAULT TRUE
);

--changeset vaibhavkohli:009-create-subscription
CREATE TABLE subscription
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT      NOT NULL REFERENCES app_user (id),
    plan_id     BIGINT      NOT NULL REFERENCES membership_plan (id),
    tier_id     BIGINT      NOT NULL REFERENCES membership_tier (id),
    status      VARCHAR(32) NOT NULL,
    start_date  TIMESTAMP   NOT NULL,
    end_date    TIMESTAMP   NOT NULL,
    auto_renew  BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP   NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP   NOT NULL DEFAULT now()
);

--changeset vaibhavkohli:010-create-subscription-active-per-user-index
CREATE UNIQUE INDEX uq_subscription_active_per_user
    ON subscription (user_id)
    WHERE status = 'ACTIVE';