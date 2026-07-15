package com.example.demo.membership.tier;

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
 * A configurable rule a user must satisfy to auto-qualify for {@link #tier}. criteriaType is a
 * free-form string (not an enum) so an admin can define new rule kinds at runtime - e.g.
 * "MIN_ORDER_COUNT", "COHORT". The evaluation engine itself is out of scope for now; this table
 * only captures the configuration.
 */
@Entity
@Table(name = "tier_criteria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TierCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    @Column(name = "criteria_type", nullable = false)
    private String criteriaType;

    @Column(name = "config_value")
    private String configValue;

    @Column(name = "is_active", nullable = false)
    private Boolean active;
}
