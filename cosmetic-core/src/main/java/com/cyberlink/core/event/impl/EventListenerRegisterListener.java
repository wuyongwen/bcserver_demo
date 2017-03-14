package com.cyberlink.core.event.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.StringUtils;

import com.cyberlink.core.event.EventListener;
import com.cyberlink.core.event.EventManager;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class EventListenerRegisterListener implements
        ApplicationListener<ContextRefreshedEvent> {
    private EventManager eventManager;

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void onApplicationEvent(ContextRefreshedEvent event) {
        final Map beans = event.getApplicationContext().getBeansOfType(
                EventListener.class);

        final List<EventListener> listeners = new ArrayList<EventListener>();
        for (Object key : beans.keySet()) {
            final String name = (String) key;
            // ignore advised proxy of background job
            if (StringUtils.startsWithIgnoreCase(name, "backgroundjob.")) {
                continue;
            }
            final Object bean = beans.get(key);
            listeners.add((EventListener) bean);
        }

        Collections.sort(listeners, new Comparator<EventListener>() {
            public int compare(EventListener o1, EventListener o2) {
                return o1.getOrder().compareTo(o2.getOrder());
            }
        });

        for (final EventListener el : listeners) {
            eventManager.registerListener(el);
        }
    }
}
