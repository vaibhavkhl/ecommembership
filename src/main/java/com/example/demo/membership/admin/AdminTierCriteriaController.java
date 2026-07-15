package com.example.demo.membership.admin;

import com.example.demo.membership.admin.dto.TierCriteriaRequest;
import com.example.demo.membership.admin.dto.TierCriteriaResponse;
import com.example.demo.membership.admin.dto.TierCriteriaUpdateRequest;
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
@RequestMapping("/api/admin/tier-criteria")
@RequiredArgsConstructor
public class AdminTierCriteriaController {

    private final AdminTierCriteriaService adminTierCriteriaService;

    @GetMapping
    public List<TierCriteriaResponse> getTierCriteria() {
        return adminTierCriteriaService.listTierCriteria();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TierCriteriaResponse createTierCriteria(@Valid @RequestBody TierCriteriaRequest request) {
        return adminTierCriteriaService.createTierCriteria(request);
    }

    @PutMapping("/{id}")
    public TierCriteriaResponse updateTierCriteria(
            @PathVariable Long id, @Valid @RequestBody TierCriteriaUpdateRequest request) {
        return adminTierCriteriaService.updateTierCriteria(id, request);
    }
}
