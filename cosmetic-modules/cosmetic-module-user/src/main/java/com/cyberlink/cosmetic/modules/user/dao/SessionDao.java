package com.cyberlink.cosmetic.modules.user.dao;


import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.cosmetic.modules.user.model.Session;
import com.cyberlink.cosmetic.modules.user.model.SessionStatus;

public interface SessionDao extends GenericDao<Session, Long>{
    Session findByToken(String token);

	List<Session> findByUserId(Long userId);
	
	Session findUniqueByUserId(Long userId);

	Session findByUserIdAndStatus(Long userId, SessionStatus status);

	void deleteByUser(Long userId);
}
