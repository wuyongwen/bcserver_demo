package com.cyberlink.cosmetic.modules.user.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.cosmetic.modules.user.model.AttributeType;
import com.cyberlink.cosmetic.modules.user.model.Attribute;

public interface AttributeDao extends GenericDao<Attribute, Long>{
    List<Attribute> findByRefIdAndNames(AttributeType type, Long refId, String ... attrName);
    
    Attribute findOneByRefIdAndName(AttributeType type, Long refId, String attrName);

    List<Attribute> findByRefType(AttributeType type); // for init fbAd
    List<Attribute> findByRefId(AttributeType type, Long refId);

    List<Long> findAttributeUser(Long offset, Long limit);
    void deleteUserAttribute(List<Long> refId);
    
    List<Attribute> findByNameAndRefIds(AttributeType type, String attrName, Long... refIds);
    
    //for backend by Danny
    List<Attribute> listAllBackendUserAttrs(AttributeType type, String attrName);
}
