package com.cyberlink.cosmetic.modules.post.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.cosmetic.modules.post.model.PsTrendUser;

public interface PsTrendUserDao extends GenericDao<PsTrendUser, Long> {

    Long findGroupByUuid(String uuid);

    List<PsTrendUser> findGroupByUuids(List<String> uuids);
    
}
