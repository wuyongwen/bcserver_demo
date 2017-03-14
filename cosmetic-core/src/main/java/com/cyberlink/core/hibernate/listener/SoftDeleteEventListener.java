package com.cyberlink.core.hibernate.listener;

import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.event.internal.DefaultDeleteEventListener;
import org.hibernate.event.spi.DeleteEvent;

import com.cyberlink.core.model.SoftDeletableEntity;

public class SoftDeleteEventListener extends DefaultDeleteEventListener {

    private static final long serialVersionUID = -4950389664445837618L;

    @SuppressWarnings("rawtypes")
    @Override
    public void onDelete(DeleteEvent event, Set transientEntities)
            throws HibernateException {
        final Object o = event.getObject();
        if (o instanceof SoftDeletableEntity) {
            ((SoftDeletableEntity) o).setIsDeleted(Boolean.TRUE);
        } else {
            super.onDelete(event, transientEntities);
        }
    }
}
