package org.com.solid.gateway;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.com.solid.config.ConsumerConfig;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ConfigurableEventQueueMapping implements EventQueueMapping {


    private Map<String, String> eventToQueueMap;

    private  ConsumerConfig consumerConfig;




    @Override
    public String getQueueForEvent(String eventType) {
        return Optional.ofNullable(eventToQueueMap.get(eventType))
                .orElseThrow(() -> new IllegalArgumentException("No queue mapped for event type: " + eventType));
    }

    @PostConstruct
    protected  void init(){
        this.eventToQueueMap = consumerConfig.getEventQueueMappings();

    }
}
