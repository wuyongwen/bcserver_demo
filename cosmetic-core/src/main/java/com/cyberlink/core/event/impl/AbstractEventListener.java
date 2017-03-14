package com.cyberlink.core.event.impl;

import java.lang.reflect.ParameterizedType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyberlink.core.event.Event;
import com.cyberlink.core.event.EventListener;

public abstract class AbstractEventListener<T extends Event> implements
        EventListener<T>, Comparable<EventListener<T>> {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private Class<T> event;
    private Integer order = 0;

    @SuppressWarnings("unchecked")
    public AbstractEventListener() {
        event = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public Class<T> getEvent() {
        return event;
    }

    public final void setOrder(Integer order) {
        this.order = order;
    }

    public final Integer getOrder() {
        return order;
    }

    public int compareTo(EventListener<T> o) {
        return order.compareTo(o.getOrder());
    }
}
