package com.cyberlink.core.hibernate.event.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import com.cyberlink.core.hibernate.event.EntityEvent;
import com.cyberlink.core.hibernate.event.EntityEventListener;
import com.cyberlink.core.hibernate.event.EntityEventManager;
import com.cyberlink.core.model.IdEntity;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DefaultEntityEventManager implements EntityEventManager,
        ApplicationListener<EntityEvent> {
    private Logger logger = LoggerFactory.getLogger(getClass());
    protected final ExecutorService executorService;

    protected final ConcurrentMap<Class<? extends IdEntity>, List<EntityEventListener>> listenersByClass;

    public DefaultEntityEventManager() {
        listenersByClass = new ConcurrentHashMap<Class<? extends IdEntity>, List<EntityEventListener>>();
        executorService = Executors.newSingleThreadExecutor();
    }

    public DefaultEntityEventManager(ExecutorService executorService) {
        listenersByClass = new ConcurrentHashMap<Class<? extends IdEntity>, List<EntityEventListener>>();
        this.executorService = executorService;
    }

    private void addToListenerList(Class clazz, EntityEventListener listener) {
        if (!listenersByClass.containsKey(clazz)) {
            listenersByClass.put(clazz, new ArrayList<EntityEventListener>());
        }
        listenersByClass.get(clazz).add(listener);
    }

    public void onApplicationEvent(EntityEvent event) {
        triggerEvent(event, getListeners(event.getEntityClass()));
    }

    private List<? extends EntityEventListener> getListeners(
            Class<? extends EntityEvent> clazz) {
        final List<? extends EntityEventListener> results = listenersByClass
                .get(clazz);
        if (results != null) {
            return results;
        }
        return Collections.emptyList();
    }

    private void triggerEvent(final EntityEvent event,
            final List<? extends EntityEventListener> listeners) {
        for (final EntityEventListener listener : listeners) {
            try {
                logger.debug("Trigger event listener: " + listener);
                if (event.getChangeType().isPostInsert()) {
                    listener.postInsert(event.getPk());
                }
                if (event.getChangeType().isPostUpdate()) {
                    listener.postUpdate(event.getPk());
                }
                if (event.getChangeType().isPostDelete()) {
                    listener.postDelete(event.getPk());
                }
            } catch (Throwable a) {
                logger.error("Fail to trigger event listener: " + listener, a);
            }
        }
    }

    public void registerListener(
            EntityEventListener<? extends IdEntity<?>, ? extends Serializable> listener) {
        try {
            addToListenerList(getEntityClass(listener), listener);
        } catch (Exception e) {
            logger.error("Fail to add listener: "
                    + listener.getClass().getName());
            throw new RuntimeException("Fail to add listener: "
                    + listener.getClass().getName(), e);
        }
    }

    private Class getEntityClass(
            EntityEventListener<? extends IdEntity, ? extends Serializable> listener) {
        return listener.getEntityClass();
    }

}
