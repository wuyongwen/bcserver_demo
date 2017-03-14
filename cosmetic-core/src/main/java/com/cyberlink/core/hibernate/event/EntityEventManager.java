package com.cyberlink.core.hibernate.event;

import java.io.Serializable;

import com.cyberlink.core.model.IdEntity;

public interface EntityEventManager {
    void registerListener(
            EntityEventListener<? extends IdEntity<?>, ? extends Serializable> listener);

}
