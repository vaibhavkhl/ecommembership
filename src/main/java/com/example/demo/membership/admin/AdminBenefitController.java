package com.example.demo.membership.admin;

import com.example.demo.membership.admin.dto.BenefitDefinitionRequest;
import com.example.demo.membership.admin.dto.BenefitDefinitionResponse;
import com.example.demo.membership.admin.dto.TierBenefitRequest;
import com.example.demo.membership.admin.dto.TierBenefitResponse;
import com.example.demo.membership.admin.dto.TierBenefitUpdateRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminBenefitController {

    private final AdminBenefitService adminBenefitService;

    @GetMapping("/benefit-definitions")
    public List<BenefitDefinitionResponse> getBenefitDefinitions() {
        return adminBenefitService.listBenefitDefinitions();
    }

    @PostMapping("/benefit-definitions")
    @ResponseStatus(HttpStatus.CREATED)
    public BenefitDefinitionResponse createBenefitDefinition(@Valid @RequestBody BenefitDefinitionRequest request) {
        return adminBenefitService.createBenefitDefinition(request);
    }

    @GetMapping("/tier-benefits")
    public List<TierBenefitResponse> getTierBenefits() {
        return adminBenefitService.listTierBenefits();
    }

    @PostMapping("/tier-benefits")
    @ResponseStatus(HttpStatus.CREATED)
    public TierBenefitResponse createTierBenefit(@Valid @RequestBody TierBenefitRequest request) {
        return adminBenefitService.createTierBenefit(request);
    }

    @PutMapping("/tier-benefits/{id}")
    public TierBenefitResponse updateTierBenefit(
            @PathVariable Long id, @Valid @RequestBody TierBenefitUpdateRequest request) {
        return adminBenefitService.updateTierBenefit(id, request);
    }
}
