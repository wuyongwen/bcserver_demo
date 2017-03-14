package com.cyberlink.cosmetic.modules.user.dao;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.user.model.UserBlocked;

public interface UserBlockedDao extends GenericDao<UserBlocked, Long> {
	UserBlocked findByTargetAndCreater(Long targetId, Long createrId);

	PageResult<Long> findByUserOrderByName(Long userId, BlockLimit blockLimit, Boolean withSize);
}