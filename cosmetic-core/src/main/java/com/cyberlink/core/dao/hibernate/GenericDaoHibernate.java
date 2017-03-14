package com.cyberlink.core.dao.hibernate;

import java.io.Serializable;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.model.IdEntity;

/**
 * This class is for generic only(the related model doesn't need to be
 * customized)
 * 
 * @author steve_lee
 * 
 * @param <T>
 * @param <PK>
 */
public final class GenericDaoHibernate<T extends IdEntity<PK>, PK extends Serializable>
        extends HibernateSupportDao<T, PK> implements GenericDao<T, PK> {

    public GenericDaoHibernate(final Class<T> entityClass) {
        setEntityClass(entityClass);
    }

}
