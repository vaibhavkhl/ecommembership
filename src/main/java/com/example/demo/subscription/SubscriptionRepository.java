package com.example.demo.subscription;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByUserIdAndStatus(Long userId, SubscriptionStatus status);

    Optional<Subscription> findFirstByUserIdOrderByCreatedAtDesc(Long userId);
}
