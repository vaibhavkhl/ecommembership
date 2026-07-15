package com.example.demo.subscription;

import com.example.demo.common.exception.ActiveSubscriptionExistsException;
import com.example.demo.common.exception.InvalidRequestException;
import com.example.demo.common.exception.NoActiveSubscriptionException;
import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.membership.CatalogService;
import com.example.demo.membership.plan.MembershipPlan;
import com.example.demo.membership.tier.MembershipTier;
import com.example.demo.subscription.dto.ChangeSubscriptionRequest;
import com.example.demo.subscription.dto.PlanSummary;
import com.example.demo.subscription.dto.SubscribeRequest;
import com.example.demo.subscription.dto.SubscriptionResponse;
import com.example.demo.subscription.dto.TierSummary;
import com.example.demo.user.AppUser;
import com.example.demo.user.AppUserRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Owns the subscription lifecycle: subscribe, change (upgrade/downgrade tier and/or switch
 * plan), cancel, and read the current membership. A plan change restarts the validity window
 * from now using the new plan's duration; a tier-only change keeps the existing window. Cancel
 * is "at period end": status flips to CANCELLED immediately but endDate is left untouched, so
 * the member keeps access until it naturally lapses.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final AppUserRepository userRepository;
    private final CatalogService catalogService;

    @Transactional(readOnly = true)
    public SubscriptionResponse getCurrent(Long userId) {
        Subscription subscription = subscriptionRepository.findFirstByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new NoActiveSubscriptionException(
                        "User " + userId + " has no subscription"));
        return toResponse(subscription);
    }

    public SubscriptionResponse subscribe(Long userId, SubscribeRequest request) {
        AppUser user = getUserOrThrow(userId);

        subscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .ifPresent(existing -> {
                    throw new ActiveSubscriptionExistsException(
                            "User " + userId + " already has an active subscription: " + existing.getId());
                });

        MembershipPlan plan = catalogService.getPlan(request.planId());
        MembershipTier tier = catalogService.getTier(request.tierId());

        Instant now = Instant.now();
        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .tier(tier)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(now)
                .endDate(now.plus(plan.getDurationDays(), ChronoUnit.DAYS))
                .autoRenew(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return toResponse(subscriptionRepository.save(subscription));
    }

    public SubscriptionResponse change(Long userId, ChangeSubscriptionRequest request) {
        if (request.planId() == null && request.tierId() == null) {
            throw new InvalidRequestException("At least one of planId or tierId must be provided");
        }

        Subscription subscription = findActiveOrThrow(userId);
        Instant now = Instant.now();

        if (request.tierId() != null) {
            subscription.setTier(catalogService.getTier(request.tierId()));
        }

        if (request.planId() != null) {
            MembershipPlan newPlan = catalogService.getPlan(request.planId());
            subscription.setPlan(newPlan);
            subscription.setStartDate(now);
            subscription.setEndDate(now.plus(newPlan.getDurationDays(), ChronoUnit.DAYS));
        }

        subscription.setUpdatedAt(now);
        return toResponse(subscriptionRepository.save(subscription));
    }

    public SubscriptionResponse cancel(Long userId) {
        Subscription subscription = findActiveOrThrow(userId);
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setUpdatedAt(Instant.now());
        return toResponse(subscriptionRepository.save(subscription));
    }

    private Subscription findActiveOrThrow(Long userId) {
        return subscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new NoActiveSubscriptionException(
                        "User " + userId + " has no active subscription"));
    }

    private AppUser getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    private SubscriptionResponse toResponse(Subscription subscription) {
        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getUser().getId(),
                new PlanSummary(subscription.getPlan().getId(), subscription.getPlan().getCode().name()),
                new TierSummary(
                        subscription.getTier().getId(),
                        subscription.getTier().getCode().name(),
                        subscription.getTier().getRank()),
                subscription.getStatus().name(),
                subscription.getStartDate(),
                subscription.getEndDate(),
                subscription.getAutoRenew(),
                subscription.getEndDate().isBefore(Instant.now()));
    }
}
