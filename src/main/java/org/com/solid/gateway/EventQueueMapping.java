package org.com.solid.gateway;

public interface EventQueueMapping {
    String getQueueForEvent(String eventType);
}
