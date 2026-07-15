package com.example.demo.membership.pricing;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanTierPricingRepository extends JpaRepository<PlanTierPricing, Long> {

    List<PlanTierPricing> findByEffectiveToIsNull();

    Optional<PlanTierPricing> findByPlanIdAndTierIdAndEffectiveToIsNull(Long planId, Long tierId);
}
