package com.cyberlink.core.dao.hibernate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.model.IdEntity;

@SuppressWarnings("unchecked")
public abstract class AbstractDaoCosmetic<T extends IdEntity<PK>, PK extends Serializable>
        extends CosmeticSupportDao<T, PK> implements GenericDao<T, PK> {

    public AbstractDaoCosmetic() {
        setEntityClass((Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
    }

}
