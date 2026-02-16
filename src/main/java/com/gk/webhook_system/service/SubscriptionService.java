package com.gk.webhook_system.service;

import com.gk.webhook_system.dto.SubscriptionRequest;
import com.gk.webhook_system.dto.SubscriptionResponse;

import java.util.List;

public interface SubscriptionService {
    SubscriptionResponse subscribe(SubscriptionRequest subscriptionRequest);
    List<SubscriptionResponse> getSubscriptions(String clientId);
    void subscribe(Long subscriptionId);

}
