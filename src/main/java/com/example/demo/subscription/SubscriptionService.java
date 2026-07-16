package com.example.demo.subscription;

import com.example.demo.common.exception.ActiveSubscriptionExistsException;
import com.example.demo.common.exception.InvalidRequestException;
import com.example.demo.common.exception.NoActiveSubscriptionException;
import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.membership.CatalogService;
import com.example.demo.membership.plan.MembershipPlan;
import com.example.demo.membership.pricing.PlanTierPricing;
import com.example.demo.membership.tier.MembershipTier;
import com.example.demo.subscription.dto.ChangeSubscriptionRequest;
import com.example.demo.subscription.dto.PlanSummary;
import com.example.demo.subscription.dto.SubscribeRequest;
import com.example.demo.subscription.dto.SubscriptionEventResponse;
import com.example.demo.subscription.dto.SubscriptionResponse;
import com.example.demo.subscription.dto.TierSummary;
import com.example.demo.user.AppUser;
import com.example.demo.user.AppUserRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Owns the subscription lifecycle: subscribe, change (upgrade/downgrade tier and/or switch
 * plan), cancel, and read the current membership. A plan change restarts the validity window
 * from now using the new plan's duration; a tier-only change keeps the existing window. Cancel
 * is "at period end": status flips to CANCELLED immediately but endDate is left untouched, so
 * the member keeps access until it naturally lapses.
 *
 * Every transition also writes a {@link SubscriptionEvent} - an immutable history of what
 * changed and what was charged, independent of the current state on {@link Subscription} itself
 * and independent of later edits to {@link PlanTierPricing}.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionEventRepository subscriptionEventRepository;
    private final AppUserRepository userRepository;
    private final CatalogService catalogService;

    @Transactional(readOnly = true)
    public SubscriptionResponse getCurrent(Long userId) {
        Subscription subscription = subscriptionRepository.findFirstByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new NoActiveSubscriptionException(
                        "User " + userId + " has no subscription"));
        return toResponse(subscription);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionEventResponse> getEvents(Long userId) {
        getUserOrThrow(userId);
        return subscriptionEventRepository.findBySubscription_User_IdOrderByCreatedAtDesc(userId).stream()
                .map(this::toEventResponse)
                .toList();
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
        PlanTierPricing pricing = catalogService.getCurrentPricing(plan.getId(), tier.getId());

        Instant now = Instant.now();
        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .tier(tier)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(now)
                .endDate(now.plus(plan.getDurationDays(), ChronoUnit.DAYS))
                .autoRenew(false)
                .pricePaid(pricing.getPrice())
                .currency(pricing.getCurrency())
                .createdAt(now)
                .updatedAt(now)
                .build();
        subscription = subscriptionRepository.save(subscription);

        recordEvent(SubscriptionEventType.SUBSCRIBED, subscription, null, plan, null, tier,
                pricing.getPrice(), pricing.getCurrency(), now);

        return toResponse(subscription);
    }

    public SubscriptionResponse change(Long userId, ChangeSubscriptionRequest request) {
        if (request.planId() == null && request.tierId() == null) {
            throw new InvalidRequestException("At least one of planId or tierId must be provided");
        }

        Subscription subscription = findActiveOrThrow(userId);
        MembershipPlan oldPlan = subscription.getPlan();
        MembershipTier oldTier = subscription.getTier();

        MembershipPlan newPlan = request.planId() != null ? catalogService.getPlan(request.planId()) : oldPlan;
        MembershipTier newTier = request.tierId() != null ? catalogService.getTier(request.tierId()) : oldTier;

        boolean planChanged = !newPlan.getId().equals(oldPlan.getId());
        boolean tierChanged = !newTier.getId().equals(oldTier.getId());
        if (!planChanged && !tierChanged) {
            throw new InvalidRequestException("Requested plan and tier match the current subscription");
        }

        PlanTierPricing pricing = catalogService.getCurrentPricing(newPlan.getId(), newTier.getId());
        Instant now = Instant.now();

        subscription.setPlan(newPlan);
        subscription.setTier(newTier);
        subscription.setPricePaid(pricing.getPrice());
        subscription.setCurrency(pricing.getCurrency());
        if (planChanged) {
            subscription.setStartDate(now);
            subscription.setEndDate(now.plus(newPlan.getDurationDays(), ChronoUnit.DAYS));
        }
        subscription.setUpdatedAt(now);
        subscription = subscriptionRepository.save(subscription);

        SubscriptionEventType eventType = tierChanged
                ? (newTier.getRank() > oldTier.getRank() ? SubscriptionEventType.UPGRADED : SubscriptionEventType.DOWNGRADED)
                : SubscriptionEventType.PLAN_CHANGED;
        recordEvent(eventType, subscription, oldPlan, newPlan, oldTier, newTier,
                pricing.getPrice(), pricing.getCurrency(), now);

        return toResponse(subscription);
    }

    public SubscriptionResponse cancel(Long userId) {
        Subscription subscription = findActiveOrThrow(userId);
        Instant now = Instant.now();
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setUpdatedAt(now);
        subscription = subscriptionRepository.save(subscription);

        recordEvent(SubscriptionEventType.CANCELLED, subscription,
                subscription.getPlan(), subscription.getPlan(),
                subscription.getTier(), subscription.getTier(),
                null, null, now);

        return toResponse(subscription);
    }

    private void recordEvent(
            SubscriptionEventType eventType,
            Subscription subscription,
            MembershipPlan fromPlan,
            MembershipPlan toPlan,
            MembershipTier fromTier,
            MembershipTier toTier,
            BigDecimal pricePaid,
            String currency,
            Instant now) {
        subscriptionEventRepository.save(SubscriptionEvent.builder()
                .subscription(subscription)
                .eventType(eventType)
                .fromPlan(fromPlan)
                .toPlan(toPlan)
                .fromTier(fromTier)
                .toTier(toTier)
                .pricePaid(pricePaid)
                .currency(currency)
                .createdAt(now)
                .build());
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
                subscription.getEndDate().isBefore(Instant.now()),
                subscription.getPricePaid(),
                subscription.getCurrency());
    }

    private SubscriptionEventResponse toEventResponse(SubscriptionEvent event) {
        return new SubscriptionEventResponse(
                event.getId(),
                event.getEventType().name(),
                event.getFromPlan() != null ? event.getFromPlan().getCode().name() : null,
                event.getToPlan() != null ? event.getToPlan().getCode().name() : null,
                event.getFromTier() != null ? event.getFromTier().getCode().name() : null,
                event.getToTier() != null ? event.getToTier().getCode().name() : null,
                event.getPricePaid(),
                event.getCurrency(),
                event.getCreatedAt());
    }
}
