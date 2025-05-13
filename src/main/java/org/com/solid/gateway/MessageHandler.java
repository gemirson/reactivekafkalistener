package org.com.solid.gateway;

import reactor.core.publisher.Mono;
import org.springframework.messaging.Message;

import java.util.Objects;

public interface MessageHandler<T, R> {
    Mono<R> handle(Message<T> message);

    default String messageId(Message<?> message) {
        Objects.requireNonNull(message.getHeaders());
        return String.format("%s:%s:%s:",
                        message.getHeaders().get("transaction_id"),
                        message.getHeaders().get("transaction_id"),
                        message.getHeaders().get("correlation_id"));
    }
}
