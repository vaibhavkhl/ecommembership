package com.example.demo.membership.admin;

import com.example.demo.common.exception.DuplicateResourceException;
import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.membership.CatalogService;
import com.example.demo.membership.admin.dto.BenefitDefinitionRequest;
import com.example.demo.membership.admin.dto.BenefitDefinitionResponse;
import com.example.demo.membership.admin.dto.TierBenefitRequest;
import com.example.demo.membership.admin.dto.TierBenefitResponse;
import com.example.demo.membership.admin.dto.TierBenefitUpdateRequest;
import com.example.demo.membership.benefit.BenefitDefinition;
import com.example.demo.membership.benefit.BenefitDefinitionRepository;
import com.example.demo.membership.benefit.TierBenefit;
import com.example.demo.membership.benefit.TierBenefitRepository;
import com.example.demo.membership.tier.MembershipTier;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Admin-facing CRUD over the benefit catalog: benefit definitions themselves, and which tiers
 * grant which benefits (with what config, e.g. a discount percentage). Separate from
 * {@link CatalogService}, which is the read-only surface consumed by the shopper-facing app.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AdminBenefitService {

    private final BenefitDefinitionRepository benefitDefinitionRepository;
    private final TierBenefitRepository tierBenefitRepository;
    private final CatalogService catalogService;

    @Transactional(readOnly = true)
    public List<BenefitDefinitionResponse> listBenefitDefinitions() {
        return benefitDefinitionRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public BenefitDefinitionResponse createBenefitDefinition(BenefitDefinitionRequest request) {
        benefitDefinitionRepository.findByCode(request.code()).ifPresent(existing -> {
            throw new DuplicateResourceException("Benefit code already exists: " + request.code());
        });

        BenefitDefinition benefit = BenefitDefinition.builder()
                .code(request.code())
                .description(request.description())
                .build();

        return toResponse(benefitDefinitionRepository.save(benefit));
    }

    @Transactional(readOnly = true)
    public List<TierBenefitResponse> listTierBenefits() {
        return tierBenefitRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public TierBenefitResponse createTierBenefit(TierBenefitRequest request) {
        MembershipTier tier = catalogService.getTier(request.tierId());
        BenefitDefinition benefit = catalogService.getBenefitDefinition(request.benefitId());

        tierBenefitRepository.findByTierIdAndBenefitId(tier.getId(), benefit.getId()).ifPresent(existing -> {
            throw new DuplicateResourceException(
                    "Tier " + tier.getId() + " already has benefit " + benefit.getId());
        });

        TierBenefit tierBenefit = TierBenefit.builder()
                .tier(tier)
                .benefit(benefit)
                .configValue(request.configValue())
                .active(true)
                .build();

        return toResponse(tierBenefitRepository.save(tierBenefit));
    }

    public TierBenefitResponse updateTierBenefit(Long id, TierBenefitUpdateRequest request) {
        TierBenefit tierBenefit = tierBenefitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tier benefit not found: " + id));

        tierBenefit.setConfigValue(request.configValue());
        tierBenefit.setActive(request.active());

        return toResponse(tierBenefitRepository.save(tierBenefit));
    }

    private BenefitDefinitionResponse toResponse(BenefitDefinition benefit) {
        return new BenefitDefinitionResponse(benefit.getId(), benefit.getCode(), benefit.getDescription());
    }

    private TierBenefitResponse toResponse(TierBenefit tierBenefit) {
        return new TierBenefitResponse(
                tierBenefit.getId(),
                tierBenefit.getTier().getId(),
                tierBenefit.getTier().getCode().name(),
                tierBenefit.getBenefit().getId(),
                tierBenefit.getBenefit().getCode(),
                tierBenefit.getConfigValue(),
                tierBenefit.getActive());
    }
}
