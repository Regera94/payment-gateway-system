package com.gk.webhook_system.repositories;

import com.gk.webhook_system.entities.WebhookSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebhookSubscriptionRepository extends JpaRepository<WebhookSubscription, Long> {

    List<WebhookSubscription> findByEventTypeAndActiveTrue(String eventType);

    List<WebhookSubscription> findByClientId(String clientId);
}
