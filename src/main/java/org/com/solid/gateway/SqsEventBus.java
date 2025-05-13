package org.com.solid.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

import java.util.HashMap;
import java.util.Map;


@RequiredArgsConstructor
@Component
public class SqsEventBus  implements  EventBus {


    private final EventQueueMapping eventQueueMapping;
    private final SqsAsyncClient sqsAsyncClient;

    @Override
    public Mono<Void> publishEvent(EventMessage<?> message, Throwable ex) {
        String queueUrl = eventQueueMapping.getQueueForEvent(message.getEventType());

        return Mono.fromFuture(
                        sqsAsyncClient.sendMessage(builder -> builder
                                .queueUrl(queueUrl)
                                .messageBody(serializePayload(message.getPayload()))
                                .messageAttributes(createCustomAttributes(message, ex))
                        ))
                .then();
    }
    private String serializePayload(Object payload) {
        // Implemente a serialização conforme necessário (JSON, etc.)
        try {
            return new ObjectMapper().writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize message payload", e);
        }
    }

    /**
     * @param message
     * @param ex
     * @return
     */
      protected Map<String, MessageAttributeValue> createCustomAttributes(EventMessage<?> message, Throwable ex){

            Map<String, MessageAttributeValue> attributes = new HashMap<>();
            attributes.put("error_type", MessageAttributeValue.builder()
                    .dataType("String")
                    .stringValue(ex.getClass().getName())
                    .build());

            attributes.put("event_id", MessageAttributeValue.builder()
                    .dataType("String")
                    .stringValue(ex.getClass().getName())
                    .build());

            attributes.put("correlation_id", MessageAttributeValue.builder()
                    .dataType("String")
                    .stringValue(ex.getClass().getName())
                    .build());

            attributes.put("transaction_id", MessageAttributeValue.builder()
                    .dataType("String")
                    .stringValue(ex.getClass().getName())
                    .build());

            attributes.put("request_source", MessageAttributeValue.builder()
                    .dataType("String")
                    .stringValue(ex.getClass().getName())
                    .build());

             return attributes;
      }




}
