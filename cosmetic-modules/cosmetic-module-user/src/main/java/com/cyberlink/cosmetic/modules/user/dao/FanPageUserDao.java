package com.cyberlink.cosmetic.modules.user.dao;

import java.util.List;
import java.util.Map;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.user.model.FanPageUser;

public interface FanPageUserDao extends GenericDao<FanPageUser, Long>{
	FanPageUser findFanPageUserByFanPageName(String FanPageName);
	List<FanPageUser> listAllFanPageUser();
	FanPageUser findFanPageUserByUserId(Long userId);
}
