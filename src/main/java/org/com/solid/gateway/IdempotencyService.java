package org.com.solid.gateway;

import org.springframework.messaging.Message;
import reactor.core.publisher.Mono;


public interface IdempotencyService {
    public Mono<Boolean> isDuplicate(Message<String> message);
    public Mono<Void> markAsProcessed(Message<String> message);
}

