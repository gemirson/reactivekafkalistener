package org.com.solid.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.com.solid.config.ConsumerConfig; // Ensure this import matches the actual package of ConsumerConfig
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Component
public class ReactiveKafkaListener {
    private static final Logger log = LoggerFactory.getLogger(ReactiveKafkaListener.class);
    private final Sinks.Many<Message<String>> receivedEvents = 
        Sinks.many().unicast().onBackpressureBuffer();
    
    private final RetryHandler retryHandler;
    private final IdempotencyService idempotencyService;
    // Removed duplicate declaration of ConsumerConfig

    private final ConsumerConfig config;

    @Bean
    public Consumer<Flux<Message<String>>> receive() {
        return flux -> flux
            .doOnNext(message -> {
                if (receivedEvents.tryEmitNext(message) != Sinks.EmitResult.OK) {
                    log.warn("Failed to emit message due to backpressure: {}", message.getPayload());
                }
            })
            .doOnError(ex -> log.error("Error in main flux", ex))
            .subscribe();
    }

    @PostConstruct
    public void init() {
        receivedEvents.asFlux()
            .limitRate(config.getBackpressureLimit())
            .flatMap(message -> 
                idempotencyService.isDuplicate(message)
                    .flatMap(isDuplicate -> {
                        if (isDuplicate) {
                            log.warn("Duplicate message detected, skipping: {}", messageId(message));
                            return Mono.empty();
                        }
                        return retryHandler.applyRetryLogic(processMessage(message), messageId(message))
                            .doOnSuccess(__ -> markAsProcessed(message))
                            .onErrorResume(ex -> handleFailure(message, ex).then(Mono.empty()));
                    })
            )
            .subscribe();
    }

    private Mono<String> processMessage(Message<String> message) {
        return Mono.fromSupplier(() -> {
            try {
                log.info("Processing message: {}", messageId(message));
                // Business logic here
                // Return some meaningful result
                String result = "Processed: " + messageId(message);
                log.info(result);
                return result;
            } catch (Exception ex) {
                log.error("Processing failed for message: {}", messageId(message), ex);
                throw ex;
            }
        });
    }

    private Mono<Void> handleFailure(Message<String> message, Throwable ex) {
        return sendToDlq(message, ex)
            .doOnSuccess(__ -> log.error("Message sent to DLQ after failures: {}", messageId(message), ex));
    }

    private Mono<Void> sendToDlq(Message<String> message, Throwable ex) {
        Map<String, MessageAttributeValue> attributes = new HashMap<>();
        attributes.put("error_type", MessageAttributeValue.builder()
            .dataType("String")
            .stringValue(ex.getClass().getName())
            .build());
        
        return Mono.fromFuture(
            sqsAsyncClient.sendMessage(builder -> builder
                .queueUrl(config.getDlqUrl())
                .messageBody(message.getPayload())
                .messageAttributes(attributes))
            ).then();
    }

    private void markAsProcessed(Message<String> message) {
        idempotencyService.markAsProcessed(message)
            .doOnError(e -> log.error("Failed to mark message as processed: {}", messageId(message), e))
            .subscribe();
    }

    private String messageId(Message<String> message) {
        return Optional.ofNullable(message.getHeaders().getId())
                       .map(Object::toString)
                       .orElse("unknown-id");
    }
}
