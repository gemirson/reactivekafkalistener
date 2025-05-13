package org.com.solid.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "consumer")
public class ConsumerConfig {
    private final Backpressure backpressure = new Backpressure();
    private final Retry retry = new Retry();
    private final Idempotency idempotency = new Idempotency();
    private final SqsQueuesConfig sqs =new SqsQueuesConfig();

    // Getters and Setters
    @Setter
    @Getter
    public static class Backpressure {
        private int limit = 100;

    }

    @Setter
    @Getter
    public static class Retry {
        private int maxAttempts = 3;
        private Duration backoff = Duration.ofSeconds(1);

    }

    @Setter
    @Getter
    public static class Idempotency {
        private boolean enabled = true;
        private Duration ttl = Duration.ofHours(24);

    }
    @Setter
    @Getter
    public static class SqsQueuesConfig {
        private Map<String, String> eventQueueMappings = new HashMap<>();

    }

    // Helper method to get queue URL by event type
    public String getQueueForEventType(String eventType) {
        if (sqs == null || sqs.getEventQueueMappings() == null) {
            throw new IllegalStateException("SQS queue mappings not configured");
        }
        return sqs.getEventQueueMappings().get(eventType);
    }


    // Delegated getters
    public int getBackpressureLimit() {
        return backpressure.getLimit();
    }

    public int getMaxRetries() {
        return retry.getMaxAttempts();
    }

    public Duration getRetryBackoff() {
        return retry.getBackoff();
    }

    public boolean isIdempotencyEnabled() {
        return idempotency.isEnabled();
    }

    public Duration getIdempotencyTtl() {
        return idempotency.getTtl();
    }

    public  Map<String, String> getEventQueueMappings(){return  sqs.getEventQueueMappings();}


}
