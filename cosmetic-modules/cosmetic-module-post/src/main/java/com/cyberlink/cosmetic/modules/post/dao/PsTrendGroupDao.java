package com.cyberlink.cosmetic.modules.post.dao;

import java.util.List;
import java.util.Map;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.cosmetic.modules.post.model.PsTrendGroup;

public interface PsTrendGroupDao extends GenericDao<PsTrendGroup, PsTrendGroup.PsTrendGroupKey> {
    
    Map<String, Long> getAvailableId();

    List<PsTrendGroup> findByStep(Integer step);
    
    void batchInsert(List<PsTrendGroup> list);
    
}
