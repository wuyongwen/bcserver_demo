package com.cyberlink.cosmetic.modules.circle.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.cosmetic.modules.circle.model.CircleTag;

public interface CircleTagDao extends GenericDao<CircleTag, Long>{
	CircleTag create(String circleTagName);
	List<CircleTag> listAllTags();
    List<CircleTag> findByIds(Long... ids);
    List<CircleTag> findByGroupId(Long id);    
}
