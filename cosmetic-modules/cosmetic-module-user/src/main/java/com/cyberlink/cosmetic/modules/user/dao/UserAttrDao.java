package com.cyberlink.cosmetic.modules.user.dao;

import java.util.Date;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.dao.hibernate.ScrollableResultsCallback;
import com.cyberlink.cosmetic.modules.user.model.UserAttr;

public interface UserAttrDao extends GenericDao<UserAttr, Long> {
    
    UserAttr findByUserId(Long userId);
    int increaseNonNullValue(Long userId, String columnName);
    int decreaseNonNullValue(Long userId, String columnName);
    int updateNullValue(Long userId, String columnName, long value);
    int deleteByUserId(Long userId);
    
    int increaseNonNullValueBy(Long userId, String columnName, Long diff);
    int decreaseNonNullValueBy(Long userId, String columnName, Long diff);
    int setNonNullValue(Long userId, String columnName, Long value);
    
    void getFollowerCountPerUser(Date startDate, Date endTime,
            ScrollableResultsCallback callback);
    
}
