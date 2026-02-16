package com.gk.webhook_system.service.impl;

import com.gk.webhook_system.entities.WebhookEvent;
import com.gk.webhook_system.service.EventPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisherServiceImpl implements EventPublisherService {
    @Override
    public void publishEvent(String eventType, String entityId, Object data) {

    }

    @Override
    public List<WebhookEvent> getEvents(String entityId, String eventType) {
        return List.of();
    }
}
