package com.example.demo.subscription;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionEventRepository extends JpaRepository<SubscriptionEvent, Long> {

    List<SubscriptionEvent> findBySubscription_User_IdOrderByCreatedAtDesc(Long userId);
}
