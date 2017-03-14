package com.cyberlink.core.hibernate.event.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.StringUtils;

import com.cyberlink.core.hibernate.event.EntityEventListener;
import com.cyberlink.core.hibernate.event.EntityEventManager;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class EntityEventListenerRegistrationListener implements
        ApplicationListener<ContextRefreshedEvent> {
    private EntityEventManager entityEventManager;

    public void setEntityEventManager(EntityEventManager entityEventManager) {
        this.entityEventManager = entityEventManager;
    }

    public void onApplicationEvent(ContextRefreshedEvent event) {
        final Map<String, EntityEventListener> beans = event
                .getApplicationContext().getBeansOfType(
                        EntityEventListener.class);

        final List<EntityEventListener> listeners = new ArrayList<EntityEventListener>();
        for (Map.Entry<String, EntityEventListener> e : beans.entrySet()) {
            // ignore advised proxy of background job
            if (StringUtils.startsWithIgnoreCase(e.getKey(), "backgroundjob.")) {
                continue;
            }
            listeners.add(e.getValue());
        }

        Collections.sort(listeners, new Comparator<EntityEventListener>() {
            public int compare(EntityEventListener o1, EntityEventListener o2) {
                return o1.getOrder().compareTo(o2.getOrder());
            }
        });

        for (final EntityEventListener el : listeners) {
            entityEventManager.registerListener(el);
        }
    }
}
