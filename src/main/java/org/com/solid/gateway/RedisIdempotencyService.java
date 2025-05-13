package org.com.solid.gateway;

import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.com.solid.config.ConsumerConfig;

@RequiredArgsConstructor
@Component
class RedisIdempotencyService implements IdempotencyService {
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ConsumerConfig config;

    @Override
    public Mono<Boolean> isDuplicate(Message<String> message) {
        String key = "msg:" + message.getHeaders().getId();
        return redisTemplate.hasKey(key);
    }

    @Override
    public Mono<Void> markAsProcessed(Message<String> message) {
        String key = "msg:" + message.getHeaders().getId();
        return redisTemplate.opsForValue()
            .set(key, "processed", config.getIdempotencyTtl())
            .then();
    }
}

