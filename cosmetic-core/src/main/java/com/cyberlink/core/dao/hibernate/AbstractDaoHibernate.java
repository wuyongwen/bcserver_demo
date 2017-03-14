package com.cyberlink.core.dao.hibernate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.model.IdEntity;

/**
 * This abstract class is for extended, if you need to add more
 * methods/functions/behaviors use it, don't use GenericDaoHibernate
 * 
 * @author steve_lee
 * @param <T>
 * @param <PK>
 */
@SuppressWarnings("unchecked")
public abstract class AbstractDaoHibernate<T extends IdEntity<PK>, PK extends Serializable>
        extends HibernateSupportDao<T, PK> implements GenericDao<T, PK> {

    public AbstractDaoHibernate() {
        setEntityClass((Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
    }

}
