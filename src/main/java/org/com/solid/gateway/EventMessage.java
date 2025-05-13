package org.com.solid.gateway;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class EventMessage<T> {
    // Getters
    private final String eventType;
    private final T payload;
    private final Map<String, String> customAttributes;

    public EventMessage(String eventType, T payload) {
        this(eventType, payload, new HashMap<>());
    }

    public EventMessage(String eventType, T payload, Map<String, String> customAttributes) {
        this.eventType = eventType;
        this.payload = payload;
        this.customAttributes = customAttributes;
    }

}
