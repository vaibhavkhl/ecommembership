package com.example.demo.subscription;

import com.example.demo.membership.plan.MembershipPlan;
import com.example.demo.membership.tier.MembershipTier;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * An append-only audit trail entry for a {@link Subscription} lifecycle transition. The
 * subscription row itself only reflects current state; this table is the history.
 */
@Entity
@Table(name = "subscription_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private SubscriptionEventType eventType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_plan_id")
    private MembershipPlan fromPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_plan_id")
    private MembershipPlan toPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_tier_id")
    private MembershipTier fromTier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_tier_id")
    private MembershipTier toTier;

    @Column(name = "price_paid", precision = 12, scale = 2)
    private BigDecimal pricePaid;

    @Column(length = 3)
    private String currency;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
