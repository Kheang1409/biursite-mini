package com.biursite.domain.shared.event;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}
