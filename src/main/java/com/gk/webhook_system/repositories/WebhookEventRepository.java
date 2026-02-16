package com.gk.webhook_system.repositories;

import com.gk.webhook_system.entities.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebhookEventRepository extends JpaRepository<WebhookEvent, Long> {

    // Find events by status
    List<WebhookEvent> findByStatus(WebhookEvent.WebhookStatus status);

    // Find events for a specific entity
    List<WebhookEvent> findByEntityIdAndEventType(String entityId, String eventType);
}
