package com.cyberlink.cosmetic.modules.user.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.cosmetic.modules.user.model.Admin;
import com.cyberlink.cosmetic.modules.user.model.Admin.UserEvent;

public interface AdminDao extends GenericDao<Admin, Long>{
	String findAttributebyRefInfo(UserEvent event, String refInfo);
	List<String> findAttributebyRefInfos(UserEvent event, List<String> refInfos);
	Admin findbyRefInfo(UserEvent event, String refInfo);
}