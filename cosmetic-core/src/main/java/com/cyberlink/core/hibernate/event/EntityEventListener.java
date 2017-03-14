package com.cyberlink.core.hibernate.event;

public interface EntityEventListener<Entity, PK> {
    Integer getOrder();

    void setOrder(Integer order);

    Class<Entity> getEntityClass();

    void postInsert(PK pk);

    void postUpdate(PK pk);

    void postDelete(PK pk);

}
