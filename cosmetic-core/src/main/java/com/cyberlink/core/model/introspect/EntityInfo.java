package com.cyberlink.core.model.introspect;

import java.io.Serializable;

public class EntityInfo implements Serializable {
    private static final long serialVersionUID = 6822494620068980136L;
    private final Class<? extends Object> entityClass;

    public EntityInfo(final Class<? extends Object> entityClass) {
        this.entityClass = entityClass;
    }

    public Class<? extends Object> getEntityClass() {
        return entityClass;
    }

}
