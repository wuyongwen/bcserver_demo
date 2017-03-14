package com.cyberlink.cosmetic.core.repository;

import java.util.List;

import com.cyberlink.cosmetic.core.model.AbstractESEntity;

public interface EsRepository<T extends AbstractESEntity> {
    
    public class EsResult<T> {
        public String error = null;
        public T result = null;
        public String response;
    }
    
    EsResult<Boolean> create(T t);
    
    EsResult<Boolean> updateField(String id, String script);
    
    EsResult<Boolean> isExists(String id);
    
    EsResult<T> findById(String id);

    EsResult<Boolean> batchCreateOrUpdate(List<T> ts);
    
}
