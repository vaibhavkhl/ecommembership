package com.example.demo.membership.benefit;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TierBenefitRepository extends JpaRepository<TierBenefit, Long> {

    List<TierBenefit> findByTierIdAndActiveTrue(Long tierId);

    List<TierBenefit> findByTierIdInAndActiveTrue(List<Long> tierIds);

    Optional<TierBenefit> findByTierIdAndBenefitId(Long tierId, Long benefitId);
}
