package org.com.solid.gateway;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono; // Import Mono for RetryHandler interface

import org.com.solid.config.ConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.util.retry.Retry;


@Component
class DefaultRetryHandler implements RetryHandler {
    private static final Logger log = LoggerFactory.getLogger(DefaultRetryHandler.class);
    private final ConsumerConfig config;

    public DefaultRetryHandler(ConsumerConfig config) {
        this.config = config;
    }
   
    @Override
    public <T> Mono<T> applyRetryLogic(Mono<T> mono, String operationName) {
        return mono.retryWhen(Retry.backoff(config.getMaxRetries(), config.getRetryBackoff())
            .doBeforeRetry(rs -> log.warn("Retrying {} after failure (attempt {})", 
                operationName, rs.totalRetries() + 1))
            .onRetryExhaustedThrow((spec, rs) -> {
                log.error("Retries exhausted for {}", operationName);
                return rs.failure();
            }));
    }
}

