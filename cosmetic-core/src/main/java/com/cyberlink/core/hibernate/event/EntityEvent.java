package com.cyberlink.core.hibernate.event;

import java.io.Serializable;

import org.hibernate.Hibernate;
import org.springframework.context.ApplicationEvent;

import com.cyberlink.core.model.IdEntity;

public final class EntityEvent<PK extends Serializable> extends
        ApplicationEvent {

    private static final long serialVersionUID = 7352489090288883134L;
    private final Class<?> entityClass;
    private final PK pk;
    private final ChangeType changeType;

    public EntityEvent(IdEntity<PK> entity, ChangeType changeType) {
        super(entity);
        this.entityClass = Hibernate.getClass(entity);
        this.pk = entity.getId();
        this.changeType = changeType;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public PK getPk() {
        return pk;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

}
