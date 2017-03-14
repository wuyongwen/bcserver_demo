package com.cyberlink.core.hibernate.event.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyberlink.core.hibernate.event.EntityEventListener;
import com.cyberlink.core.model.IdEntity;

public abstract class AbstractEntityEventListener<Entity extends IdEntity<PK>, PK extends Serializable>
        implements EntityEventListener<Entity, PK> {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private Class<Entity> entityClass;
    private Integer order = 1000;

    @SuppressWarnings("unchecked")
    public AbstractEntityEventListener() {
        entityClass = (Class<Entity>) (((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    @Override
    public final Class<Entity> getEntityClass() {
        return entityClass;
    }

    @Override
    public void postInsert(PK pk) {

    }

    @Override
    public void postUpdate(PK pk) {

    }

    @Override
    public void postDelete(PK pk) {

    }

    @Override
    public Integer getOrder() {
        return this.order;
    }

    @Override
    public void setOrder(Integer order) {
        this.order = order;
    }
}
