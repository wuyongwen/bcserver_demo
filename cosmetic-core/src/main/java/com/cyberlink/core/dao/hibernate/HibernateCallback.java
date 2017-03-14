package com.cyberlink.core.dao.hibernate;

import java.util.List;

import org.hibernate.StatelessSession;

public interface HibernateCallback<T> {
    List<T> doInHibernate(StatelessSession session);
}
