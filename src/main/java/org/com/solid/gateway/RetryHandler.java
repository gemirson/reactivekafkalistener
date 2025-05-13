package org.com.solid.gateway;

import reactor.core.publisher.Mono; // Import Mono for RetryHandler interface

public interface RetryHandler {
   public  <T> Mono<T> applyRetryLogic(Mono<T> mono, String operationName);
}
