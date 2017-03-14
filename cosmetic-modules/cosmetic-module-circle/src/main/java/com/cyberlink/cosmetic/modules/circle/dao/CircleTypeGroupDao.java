package com.cyberlink.cosmetic.modules.circle.dao;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.cosmetic.modules.circle.model.CircleTypeGroup;

public interface CircleTypeGroupDao extends GenericDao<CircleTypeGroup, Long>{

    CircleTypeGroup findByTypeGroupName(String typeGroupName);

    Long findByDefaultTypeName(String defaultType);

}
