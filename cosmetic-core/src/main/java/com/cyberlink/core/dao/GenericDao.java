package com.cyberlink.core.dao;

import java.io.Serializable;
import java.util.List;

import com.cyberlink.core.model.IdEntity;
import com.cyberlink.core.service.TransactionalService;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;

public interface GenericDao<T extends IdEntity<PK>, PK extends Serializable>
        extends TransactionalService {
    T create(T t);

    void delete(PK id);

    void delete(T t);

    boolean exists(PK id);

    List<T> findAll();

    T findById(PK id);

    Class<T> getEntityClass();

    T update(T t);

    void refresh(T t);

    PageResult<T> pageQuery(PageLimit limit);
    
    void clear();

}
