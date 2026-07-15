package com.example.demo.membership;

import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.membership.benefit.TierBenefit;
import com.example.demo.membership.benefit.TierBenefitRepository;
import com.example.demo.membership.dto.BenefitResponse;
import com.example.demo.membership.dto.PlanResponse;
import com.example.demo.membership.dto.PriceResponse;
import com.example.demo.membership.dto.TierResponse;
import com.example.demo.membership.plan.MembershipPlan;
import com.example.demo.membership.plan.MembershipPlanRepository;
import com.example.demo.membership.pricing.PlanTierPricing;
import com.example.demo.membership.pricing.PlanTierPricingRepository;
import com.example.demo.membership.tier.MembershipTier;
import com.example.demo.membership.tier.MembershipTierRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Read-only access to the membership catalog: plans, tiers with their resolved benefits, and
 * current pricing. Subscription lifecycle logic lives in the subscription package instead.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogService {

    private final MembershipPlanRepository planRepository;
    private final MembershipTierRepository tierRepository;
    private final TierBenefitRepository tierBenefitRepository;
    private final PlanTierPricingRepository pricingRepository;

    public List<PlanResponse> listActivePlans() {
        return planRepository.findByActiveTrue().stream()
                .map(this::toPlanResponse)
                .toList();
    }

    public List<TierResponse> listActiveTiers() {
        List<MembershipTier> tiers = tierRepository.findByActiveTrueOrderByRankAsc();
        List<Long> tierIds = tiers.stream().map(MembershipTier::getId).toList();

        Map<Long, List<BenefitResponse>> benefitsByTierId = tierBenefitRepository
                .findByTierIdInAndActiveTrue(tierIds).stream()
                .collect(Collectors.groupingBy(
                        tierBenefit -> tierBenefit.getTier().getId(),
                        Collectors.mapping(this::toBenefitResponse, Collectors.toList())));

        return tiers.stream()
                .map(tier -> new TierResponse(
                        tier.getId(),
                        tier.getCode().name(),
                        tier.getRank(),
                        benefitsByTierId.getOrDefault(tier.getId(), List.of())))
                .toList();
    }

    public List<PriceResponse> listCurrentPrices() {
        return pricingRepository.findByEffectiveToIsNull().stream()
                .map(this::toPriceResponse)
                .toList();
    }

    public PlanTierPricing getCurrentPricing(Long planId, Long tierId) {
        return pricingRepository.findByPlanIdAndTierIdAndEffectiveToIsNull(planId, tierId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active pricing for planId=" + planId + " and tierId=" + tierId));
    }

    public MembershipPlan getPlan(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found: " + planId));
    }

    public MembershipTier getTier(Long tierId) {
        return tierRepository.findById(tierId)
                .orElseThrow(() -> new ResourceNotFoundException("Tier not found: " + tierId));
    }

    private PlanResponse toPlanResponse(MembershipPlan plan) {
        return new PlanResponse(plan.getId(), plan.getCode().name(), plan.getDurationDays());
    }

    private BenefitResponse toBenefitResponse(TierBenefit tierBenefit) {
        return new BenefitResponse(
                tierBenefit.getBenefit().getCode().name(),
                tierBenefit.getBenefit().getDescription(),
                tierBenefit.getConfigValue());
    }

    private PriceResponse toPriceResponse(PlanTierPricing pricing) {
        return new PriceResponse(
                pricing.getPlan().getId(),
                pricing.getPlan().getCode().name(),
                pricing.getTier().getId(),
                pricing.getTier().getCode().name(),
                pricing.getPrice(),
                pricing.getCurrency());
    }
}
