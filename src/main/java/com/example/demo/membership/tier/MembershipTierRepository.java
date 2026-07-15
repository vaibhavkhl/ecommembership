package com.example.demo.membership.tier;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipTierRepository extends JpaRepository<MembershipTier, Long> {

    List<MembershipTier> findByActiveTrueOrderByRankAsc();

    Optional<MembershipTier> findByCode(TierCode code);
}
