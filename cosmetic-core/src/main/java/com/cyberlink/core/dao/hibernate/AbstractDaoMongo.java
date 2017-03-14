package com.cyberlink.core.dao.hibernate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.model.IdEntity;

@SuppressWarnings("unchecked")
public abstract class AbstractDaoMongo<T extends IdEntity<PK>, PK extends Serializable>
        extends MongoSupportDao<T, PK> implements GenericDao<T, PK> {	
	
    public AbstractDaoMongo() {
        setEntityClass((Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
    }

}