package com.cyberlink.core.event.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import com.cyberlink.core.event.Event;
import com.cyberlink.core.event.EventListener;
import com.cyberlink.core.event.EventManager;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DefaultEventManager implements EventManager, ApplicationListener {
    private Logger logger = LoggerFactory.getLogger(getClass());
    protected final ExecutorService executorService;

    protected final ConcurrentMap<Class<? extends Event>, List<EventListener>> listenersByClass;

    public DefaultEventManager() {
        listenersByClass = new ConcurrentHashMap<Class<? extends Event>, List<EventListener>>();
        executorService = Executors.newSingleThreadExecutor();
    }

    public DefaultEventManager(ExecutorService executorService) {
        listenersByClass = new ConcurrentHashMap<Class<? extends Event>, List<EventListener>>();
        this.executorService = executorService;
    }

    private void addToListenerList(Class clazz, EventListener listener) {
        if (!listenersByClass.containsKey(clazz)) {
            listenersByClass.put(clazz, new ArrayList<EventListener>());
        }
        listenersByClass.get(clazz).add(listener);
    }

    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof Event) {
            Event e = (Event) event;
            triggerEvent(e, getListeners(e.getClass()));
        }
    }

    private List<? extends EventListener> getListeners(
            Class<? extends Event> clazz) {
        final List<? extends EventListener> results = listenersByClass
                .get(clazz);
        if (results != null) {
            return results;
        }
        return Collections.emptyList();
    }

    private void triggerEvent(final Event event,
            final List<? extends EventListener> listeners) {
        for (final EventListener listener : listeners) {
            try {
                logger.debug("Trigger event listener: " + listener);
                listener.onEvent(event);
            } catch (Throwable a) {
                logger.error("Fail to trigger event listener: " + listener, a);
            }
        }
    }

    public void registerListener(EventListener<? extends Event> listener) {
        try {
            addToListenerList(getEventType(listener), listener);
        } catch (Exception e) {
            logger.error("Fail to add listener: "
                    + listener.getClass().getName());
            throw new RuntimeException("Fail to add listener: "
                    + listener.getClass().getName(), e);
        }
    }

    private Class getEventType(EventListener<? extends Event> listener) {
        return listener.getEvent();
    }
}
