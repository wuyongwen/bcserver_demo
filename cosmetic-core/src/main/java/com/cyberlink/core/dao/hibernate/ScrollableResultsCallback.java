package com.cyberlink.core.dao.hibernate;

import org.hibernate.ScrollableResults;

public interface ScrollableResultsCallback {
    void doInHibernate(ScrollableResults sr);
}
