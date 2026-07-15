package com.example.demo.membership.admin;

import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.membership.CatalogService;
import com.example.demo.membership.admin.dto.TierCriteriaRequest;
import com.example.demo.membership.admin.dto.TierCriteriaResponse;
import com.example.demo.membership.admin.dto.TierCriteriaUpdateRequest;
import com.example.demo.membership.tier.MembershipTier;
import com.example.demo.membership.tier.TierCriteria;
import com.example.demo.membership.tier.TierCriteriaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Admin-facing CRUD over tier auto-qualification rules. Evaluation of these rules against a
 * user's activity is out of scope for now; this only manages the configuration.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AdminTierCriteriaService {

    private final TierCriteriaRepository tierCriteriaRepository;
    private final CatalogService catalogService;

    @Transactional(readOnly = true)
    public List<TierCriteriaResponse> listTierCriteria() {
        return tierCriteriaRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public TierCriteriaResponse createTierCriteria(TierCriteriaRequest request) {
        MembershipTier tier = catalogService.getTier(request.tierId());

        TierCriteria criteria = TierCriteria.builder()
                .tier(tier)
                .criteriaType(request.criteriaType())
                .configValue(request.configValue())
                .active(true)
                .build();

        return toResponse(tierCriteriaRepository.save(criteria));
    }

    public TierCriteriaResponse updateTierCriteria(Long id, TierCriteriaUpdateRequest request) {
        TierCriteria criteria = tierCriteriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tier criteria not found: " + id));

        criteria.setConfigValue(request.configValue());
        criteria.setActive(request.active());

        return toResponse(tierCriteriaRepository.save(criteria));
    }

    private TierCriteriaResponse toResponse(TierCriteria criteria) {
        return new TierCriteriaResponse(
                criteria.getId(),
                criteria.getTier().getId(),
                criteria.getTier().getCode().name(),
                criteria.getCriteriaType(),
                criteria.getConfigValue(),
                criteria.getActive());
    }
}
