package com.cyberlink.core.event;

public interface EventListener<T extends Event> {
    void onEvent(T event);

    Class<T> getEvent();

    void setOrder(Integer order);

    Integer getOrder();
}
