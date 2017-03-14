package com.cyberlink.core.hibernate.listener;

import org.hibernate.SessionFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class HibernateListenerConfigurerListener implements
        ApplicationListener<ContextRefreshedEvent> {
    private SessionFactory sessionFactory;
    private HibernateEntityEventPublishListener hibernateEventListener;

    public void setHibernateEventListener(
            HibernateEntityEventPublishListener hibernateEventListener) {
        this.hibernateEventListener = hibernateEventListener;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        final ServiceRegistryImplementor sr = ((SessionFactoryImpl) sessionFactory)
                .getServiceRegistry();
        final EventListenerRegistry r = sr
                .getService(EventListenerRegistry.class);
        r.setListeners(EventType.DELETE, new SoftDeleteEventListener(),
                hibernateEventListener);
        r.appendListeners(EventType.POST_UPDATE, hibernateEventListener);
        r.appendListeners(EventType.POST_INSERT, hibernateEventListener);
    }

}
