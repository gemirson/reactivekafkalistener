package org.com.solid.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import reactor.core.publisher.Mono;

@Slf4j
public abstract class AbstractMessageHandler<T, R> implements MessageHandler<T, R> {

    @Override
    public Mono<R> handle(Message<T> message) {
        return Mono.fromSupplier(() -> {
            try {
                return processMessage(message);
            } catch (Exception ex) {
                log.error("Processing failed for message: {}", messageId(message), ex);
                throw new MessageProcessingException("Failed to process message", ex);
            }
        });
    }

    protected abstract R processMessage(Message<T> message);
}

class MessageProcessingException extends RuntimeException {
    public MessageProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
