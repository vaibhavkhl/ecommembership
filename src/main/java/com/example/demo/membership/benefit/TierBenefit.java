package com.example.demo.membership.benefit;

import com.example.demo.membership.tier.MembershipTier;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Grants {@link #benefit} to {@link #tier}, configured via a plain string interpreted by a
 * benefit-specific strategy (e.g. "10" for a 10% discount, null for a plain boolean perk like
 * priority support). Keeps per-tier perks a data change rather than a code change.
 */
@Entity
@Table(name = "tier_benefit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TierBenefit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "benefit_id", nullable = false)
    private BenefitDefinition benefit;

    @Column(name = "config_value")
    private String configValue;

    @Column(name = "is_active", nullable = false)
    private Boolean active;
}
