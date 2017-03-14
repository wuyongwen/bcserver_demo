package com.cyberlink.core.hibernate.listener;

import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.event.spi.DeleteEvent;
import org.hibernate.event.spi.DeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import com.cyberlink.core.hibernate.event.ChangeType;
import com.cyberlink.core.hibernate.event.EntityEvent;
import com.cyberlink.core.model.IdEntity;
import com.cyberlink.core.model.SoftDeletableEntity;
import com.cyberlink.core.service.AbstractService;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class HibernateEntityEventPublishListener extends AbstractService
        implements PostUpdateEventListener, PostInsertEventListener,
        DeleteEventListener, ApplicationEventPublisherAware {

    private static final long serialVersionUID = 459801397030134486L;

    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        if (!(event.getEntity() instanceof IdEntity)) {
            return;
        }
        if (event.getEntity() instanceof SoftDeletableEntity) {
            if (((SoftDeletableEntity) event.getEntity()).getIsDeleted()) {
                return;
            }
        }
        publisher.publishEvent(new EntityEvent((IdEntity) event.getEntity(),
                ChangeType.PostUpdate));
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        if (!(event.getEntity() instanceof IdEntity)) {
            return;
        }
        publisher.publishEvent(new EntityEvent((IdEntity) event.getEntity(),
                ChangeType.PostInsert));
    }

    @Override
    public void onDelete(DeleteEvent event) throws HibernateException {
        if (!(event.getObject() instanceof IdEntity)) {
            return;
        }
        publisher.publishEvent(new EntityEvent((IdEntity) event.getObject(),
                ChangeType.PostDelete));
    }

    @Override
    public void onDelete(DeleteEvent event, Set transientEntities)
            throws HibernateException {

    }

}
