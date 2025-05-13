package org.com.solid.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
public class AberturaCarteiraHandler extends  AbstractMessageHandler<String, Mono<Void>>{

    private EventBus eventBus;

    /**
     * @param message 
     * @return
     */
    @Override
    protected Mono<Void> processMessage(Message<String> message) {
        Map<String, String> headerMap = MessageHeaderConverter.convertHeadersToStringMap(message.getHeaders());
        return eventBus.publishEvent(
                new EventMessage<>("ORDER_CREATED", message.getPayload(), headerMap),
                null // ou exception se for um erro
        );

    }
}
