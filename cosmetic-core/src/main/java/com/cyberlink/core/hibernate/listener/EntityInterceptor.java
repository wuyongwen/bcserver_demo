package com.cyberlink.core.hibernate.listener;

import java.io.Serializable;
import java.util.Calendar;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.model.AbstractEntity;
import com.cyberlink.core.model.LastModifiedEntity;

public class EntityInterceptor extends EmptyInterceptor {
    private static final long serialVersionUID = 8501352125097631033L;

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state,
            String[] propertyNames, Type[] types) {
        if (entity instanceof AbstractEntity<?>
                || entity instanceof AbstractCoreEntity<?>) {
            for (int i = 0; i < propertyNames.length; i++) {
                if ("isDeleted".equals(propertyNames[i]) && state[i] == null) {
                    state[i] = Boolean.FALSE;
                } else if ("createdTime".equals(propertyNames[i])) {
                    Calendar c = Calendar.getInstance();
                    c.set(14, 0);
                    state[i] = c.getTime();
                }
            }
        }
        if (entity instanceof LastModifiedEntity) {
            for (int i = 0; i < propertyNames.length; i++) {
                if ("lastModified".equals(propertyNames[i]) && state[i] == null) {
                    state[i] = Calendar.getInstance().getTime();
                }
            }
        }
        return super.onSave(entity, id, state, propertyNames, types);
    }
}
