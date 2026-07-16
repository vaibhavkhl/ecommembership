--liquibase formatted sql

--changeset vaibhavkohli:019-add-subscription-price-columns
ALTER TABLE subscription ADD COLUMN price_paid NUMERIC(12, 2);
ALTER TABLE subscription ADD COLUMN currency VARCHAR(3);

--changeset vaibhavkohli:020-create-subscription-event
CREATE TABLE subscription_event
(
    id              BIGSERIAL PRIMARY KEY,
    subscription_id BIGINT         NOT NULL REFERENCES subscription (id),
    event_type      VARCHAR(32)    NOT NULL,
    from_plan_id    BIGINT REFERENCES membership_plan (id),
    to_plan_id      BIGINT REFERENCES membership_plan (id),
    from_tier_id    BIGINT REFERENCES membership_tier (id),
    to_tier_id      BIGINT REFERENCES membership_tier (id),
    price_paid      NUMERIC(12, 2),
    currency        VARCHAR(3),
    created_at      TIMESTAMP      NOT NULL DEFAULT now()
);

--changeset vaibhavkohli:021-create-subscription-event-subscription-id-index
CREATE INDEX idx_subscription_event_subscription_id ON subscription_event (subscription_id);
