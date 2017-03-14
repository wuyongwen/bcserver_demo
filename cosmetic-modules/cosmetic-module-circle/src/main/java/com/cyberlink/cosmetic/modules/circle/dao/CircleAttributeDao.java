package com.cyberlink.cosmetic.modules.circle.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleAttribute;
import com.cyberlink.cosmetic.modules.circle.model.CircleAttribute.CircleAttrType;

public interface CircleAttributeDao extends GenericDao<CircleAttribute, Long> {
    
    List<Circle> getCircleAttribute(List<Circle> circles, Long curUserId, List<Long> subcribedCircleIds);
    List<CircleAttribute> findCircleAttribute(String region, Circle circle, CircleAttrType attrType);
    CircleAttribute createOrUpdateCircleAttr(Circle circle, CircleAttrType attrType, String value, Boolean createIfNotExist);
    List<Circle> getBcDefaultCircleAttribute(List<Circle> circles);
    
}
