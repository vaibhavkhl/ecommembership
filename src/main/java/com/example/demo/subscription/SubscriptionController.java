package com.example.demo.subscription;

import com.example.demo.subscription.dto.ChangeSubscriptionRequest;
import com.example.demo.subscription.dto.SubscribeRequest;
import com.example.demo.subscription.dto.SubscriptionEventResponse;
import com.example.demo.subscription.dto.SubscriptionResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/{userId}/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping
    public SubscriptionResponse getCurrent(@PathVariable Long userId) {
        return subscriptionService.getCurrent(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionResponse subscribe(@PathVariable Long userId, @Valid @RequestBody SubscribeRequest request) {
        return subscriptionService.subscribe(userId, request);
    }

    @PatchMapping
    public SubscriptionResponse change(@PathVariable Long userId, @RequestBody ChangeSubscriptionRequest request) {
        return subscriptionService.change(userId, request);
    }

    @DeleteMapping
    public SubscriptionResponse cancel(@PathVariable Long userId) {
        return subscriptionService.cancel(userId);
    }

    @GetMapping("/events")
    public List<SubscriptionEventResponse> getEvents(@PathVariable Long userId) {
        return subscriptionService.getEvents(userId);
    }
}
