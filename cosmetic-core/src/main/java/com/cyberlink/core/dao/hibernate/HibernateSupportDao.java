package com.cyberlink.core.dao.hibernate;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.util.ReflectionUtils;

import com.cyberlink.core.dao.result.ScrollableList;
import com.cyberlink.core.event.Event;
import com.cyberlink.core.model.IdEntity;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;

@SuppressWarnings("unchecked")
public abstract class HibernateSupportDao<T extends IdEntity<PK>, PK extends Serializable>
        implements ApplicationEventPublisherAware {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private SessionFactory sessionFactory;

    private ApplicationEventPublisher publisher;

    private Class<T> entityClass;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    protected final void publishEvent(Event event) {
        publisher.publishEvent(event);
    }

    protected final SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    protected final Session getSession() {
        return getSessionFactory().getCurrentSession();
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public T create(final T t) {
        getSession().save(t);
        return t;
    }

    public void delete(final PK id) {
        delete(findById(id));
    }

    public void delete(final T t) {
        if (logger.isDebugEnabled()) {
            logger.debug((new StringBuilder()).append("Deleting object: ")
                    .append(t).toString());
        }
        getSession().delete(t);
        if (logger.isDebugEnabled()) {
            logger.debug("delete entity: " + t.getClass().getSimpleName()
                    + ", id: " + t.getId());
        }
    }

    protected final List<T> execute(HibernateCallback<T> callback) {
        StatelessSession ss = getSessionFactory().openStatelessSession();
        try {
            return (List<T>) callback.doInHibernate(ss);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            ss.close();
        }
        return null;
    }

    public boolean exists(final PK id) {
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.idEq(id));
        dc.setProjection(Projections.rowCount());
        final Long size = uniqueResult(dc);
        return size != 0;
    }

    protected <E> E uniqueResult(DetachedCriteria dc) {
        return uniqueResult(dc, null);
    }

    @SuppressWarnings("rawtypes")
    protected <E> E uniqueResult(DetachedCriteria dc, String region) {
        final List l = findByCriteria(dc, region);
        if (l.isEmpty()) {
            return null;
        }

        return (E) l.get(0);
    }

    public List<T> findAll() {
        return getSession().createCriteria(entityClass).list();
    }

    public T findById(final PK id) {
        final T t = (T) getSession().load(entityClass, id);
        return t;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    protected void setEntityClass(final Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public T update(final T t) {
        return (T) getSession().merge(t);
    }

    protected final DetachedCriteria createDetachedCriteria() {
        return DetachedCriteria.forClass(entityClass);
    }

    protected final DetachedCriteria createDetachedCriteria(final String alias) {
        return DetachedCriteria.forClass(entityClass, alias);
    }

    public <E> List<E> scroll2List(ScrollableResults results) {
        return new ScrollableList<E>(results);
    }

    public void refresh(final T t) {
        getSession().refresh(t);
    }

    public PageResult<T> pageQuery(PageLimit limit) {
        return pageQuery(createDetachedCriteria(), limit);
    }

    protected PageResult<T> pageQuery(DetachedCriteria dc, PageLimit limit) {
        return pageQuery(dc, limit, null);
    }

    protected PageResult<T> pageQuery(DetachedCriteria dc, PageLimit limit,
            String region) {
        final DetachedCriteria countDetachedCriteria = (DetachedCriteria) SerializationUtils
                .clone(dc);
        final Integer totalRows = countTotalRows(countDetachedCriteria, region);
        if (totalRows.intValue() == 0) {
            return new PageResult<T>();
        }
        dc = addOrder(dc, limit.getOrderBy(), limit.isAsc());

        final Criteria c = dc.getExecutableCriteria(getSession());
        if (StringUtils.isNotBlank(region)) {
            c.setCacheable(Boolean.TRUE);
            c.setCacheRegion(region);
        }
        c.setFirstResult(limit.getStartIndex());
        c.setMaxResults(limit.getPageSize());
        return new PageResult<T>(c.list(), totalRows.intValue());
    }

    protected final <E> PageResult<E> findByCriteria(DetachedCriteria dc,
            Long offset, Long limit, String region) {
        final DetachedCriteria countDetachedCriteria = (DetachedCriteria) SerializationUtils
                .clone(dc);
        final Integer totalRows = countTotalRows(countDetachedCriteria, region);
        if (totalRows.intValue() == 0) {
            return new PageResult<E>();
        }

        final Criteria c = dc.getExecutableCriteria(getSession());
        if (StringUtils.isNotBlank(region)) {
            c.setCacheable(Boolean.TRUE);
            c.setCacheRegion(region);
        }
        c.setFirstResult(offset.intValue());
        c.setMaxResults(limit.intValue());
        return new PageResult<E>(c.list(), totalRows.intValue());
    }

    protected final <E> List<E> findByCriteria(DetachedCriteria dc) {
        return findByCriteria(dc, null);
    }

    protected final <E> List<E> findByCriteria(DetachedCriteria dc,
            String region) {
        final Criteria c = dc.getExecutableCriteria(getSession());
        if (StringUtils.isNotBlank(region)) {
            c.setCacheable(Boolean.TRUE);
            c.setCacheRegion(region);
        }
        return c.list();
    }

    private Integer countTotalRows(final DetachedCriteria dc,
            final String region) {
        final Criteria c = dc.getExecutableCriteria(getSession());
        removeOrder(c);
        if (StringUtils.isNotBlank(region)) {
            c.setCacheable(Boolean.TRUE);
            c.setCacheRegion(region);
        }
        c.setProjection(Projections.rowCount());
        final Object o = c.uniqueResult();
        if (o == null) {
            return 0;
        }
        return ((Long) o).intValue();
    }

    private void removeOrder(Criteria c) {
        final Field field = ReflectionUtils.findField(c.getClass(),
                "orderEntries");
        field.setAccessible(Boolean.TRUE);
        ReflectionUtils.setField(field, c, Collections.emptyList());
        field.setAccessible(Boolean.FALSE);
    }

    private DetachedCriteria addOrder(final DetachedCriteria c,
            final String orderBy, final boolean asc) {
        final Order order = createOrder(orderBy, asc);

        if (order != null) {
            c.addOrder(order);
        }
        return c;
    }

    private Order createOrder(String orderBy, Boolean asc) {
        if (StringUtils.isEmpty(orderBy)) {
            return null;
        }
        if (asc) {
            return Order.asc(orderBy);
        }
        return Order.desc(orderBy);
    }

    public void clear() {
        getSession().clear();
    }

}
