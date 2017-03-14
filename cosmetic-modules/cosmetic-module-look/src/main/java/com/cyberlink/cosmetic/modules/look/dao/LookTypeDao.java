package com.cyberlink.cosmetic.modules.look.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.cosmetic.modules.look.model.LookType;

public interface LookTypeDao extends GenericDao<LookType, Long>{
    
    List<LookType> listByLocale(String locale);
    
    Map<String, Long> findMapByCodeName(String codeName);
    
    Map<Long, LookType> findMapByIds(Set<Long> lookTypeIds); 
}
