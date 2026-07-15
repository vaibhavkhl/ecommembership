package com.example.demo.membership.tier;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TierCriteriaRepository extends JpaRepository<TierCriteria, Long> {

    List<TierCriteria> findByTierIdAndActiveTrue(Long tierId);
}
