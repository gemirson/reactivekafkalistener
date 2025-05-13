package org.com.solid.gateway;

import org.springframework.messaging.Message;
import reactor.core.publisher.Mono;

public interface EventBus {
    public Mono<Void> publishEvent(EventMessage<?> message, Throwable ex);
}
