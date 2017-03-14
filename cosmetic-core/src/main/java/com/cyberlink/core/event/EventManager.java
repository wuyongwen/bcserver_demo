package com.cyberlink.core.event;

public interface EventManager {
    void registerListener(EventListener<? extends Event> listener);

}
