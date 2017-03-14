package com.cyberlink.core.dao.hibernate;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.DetachedCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.cyberlink.core.dao.result.ScrollableList;
import com.cyberlink.core.event.Event;
import com.cyberlink.core.model.IdEntity;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;

public abstract class MongoSupportDao<T extends IdEntity<PK>, PK extends Serializable>
        implements ApplicationEventPublisherAware {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private ApplicationEventPublisher publisher;

    private Class<T> entityClass;
    protected MongoTemplate mongoTemplate;

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    protected final void publishEvent(Event event) {
        publisher.publishEvent(event);
    }

    public T create(final T t) {
    	mongoTemplate.save(t, mongoTemplate.getCollectionName(entityClass));
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
        Criteria c = Criteria.where("Id").is(t.getId());
        final Query q = new Query(c);
        mongoTemplate.findAndRemove(q, entityClass);
        if (logger.isDebugEnabled()) {
            logger.debug("delete entity: " + t.getClass().getSimpleName()
                    + ", id: " + t.getId());
        }
    }

    public boolean exists(final PK id) {
        Criteria c = Criteria.where("Id").is(id);
        final Query q = new Query(c);
        return mongoTemplate.exists(q, entityClass);
    }
    protected T uniqueResult(Criteria c) {
        return uniqueResult(c, null);
    }

    protected T uniqueResult(Criteria c, String region) {
    	final Query q = new Query(c);
    	return mongoTemplate.findOne(q, entityClass);    
    }

    public List<T> findAll() {
        return mongoTemplate.findAll(entityClass);
    }

    public T findById(final PK id) {
        final T t = (T) mongoTemplate.findById(id, entityClass);
        return t;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    protected void setEntityClass(final Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public T update(final T t) {
    	mongoTemplate.save(t, mongoTemplate.getCollectionName(entityClass));
    	return mongoTemplate.findById(t.getId(), entityClass);
    }

    protected final Criteria createDetachedCriteria() {
        return Criteria.where(null);
    }

    protected final DetachedCriteria createDetachedCriteria(final String alias) {
        return DetachedCriteria.forClass(entityClass, alias);
    }

    public <E> List<E> scroll2List(ScrollableResults results) {
        return new ScrollableList<E>(results);
    }

    public void refresh(final T t) {
    }

    public void clear() {
        
    }
    
    public PageResult<T> pageQuery(PageLimit limit) {
        return pageQuery(Criteria.where(null), limit);
    }

    protected PageResult<T> pageQuery(Criteria dc, PageLimit limit) {
        return pageQuery(dc, limit, null);
    }

    protected PageResult<T> pageQuery(Criteria c, PageLimit limit,
            String region) {
        final Query q = new Query(c);
        q.skip(limit.getStartIndex());
        q.limit(limit.getPageSize());
		PageResult<T> pageResult = new PageResult<T>();
		pageResult.setResults(mongoTemplate.find(q, entityClass));
		pageResult.setTotalSize(Integer.valueOf((int)mongoTemplate.count(q, entityClass)));
        return pageResult;
    }
    
    protected final List<T> findByCriteria(Criteria dc,
            final PageLimit limit) {
        return findByCriteria(dc, null, limit);
    }
    
    protected final List<T> findByCriteria(final Criteria c,
            final String region, final PageLimit limit) {
        final Query q = new Query(c);
        q.skip(limit.getStartIndex());
        q.limit(limit.getPageSize());
        return mongoTemplate.find(q, entityClass);
    }
    
    protected final PageResult<T> findByCriteria(Criteria c, Long offset, Long limit,
            String region) {
        final Query q = new Query(c);
        q.skip(offset.intValue());
        q.limit(limit.intValue());
		PageResult<T> pageResult = new PageResult<T>();
		pageResult.setResults(mongoTemplate.find(q, entityClass));
		pageResult.setTotalSize(Integer.valueOf((int)mongoTemplate.count(q, entityClass)));
        return pageResult;
    }

    protected final List<T> findByCriteria(Criteria dc) {
        return findByCriteria(dc, "");
    }

    protected final List<T> findByCriteria(Criteria c,
            String region) {
        return mongoTemplate.find(new Query(c), entityClass);
    }

    /*private Integer countTotalRows(final Criteria c,
            final String region) {
        final Query q = new Query(c);
        q.limit(0);
        mongoTemplate.find(q, entityClass);
        return (int) mongoTemplate.count(q, entityClass);
    }*/
}
