package com.example.demo.membership;

import com.example.demo.membership.dto.PlanResponse;
import com.example.demo.membership.dto.PriceResponse;
import com.example.demo.membership.dto.TierResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    @GetMapping("/plans")
    public List<PlanResponse> getPlans() {
        return catalogService.listActivePlans();
    }

    @GetMapping("/tiers")
    public List<TierResponse> getTiers() {
        return catalogService.listActiveTiers();
    }

    @GetMapping("/pricing")
    public List<PriceResponse> getPricing() {
        return catalogService.listCurrentPrices();
    }
}
