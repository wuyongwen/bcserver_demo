package com.cyberlink.cosmetic.modules.notify.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.cosmetic.modules.notify.model.NotifyEvent;

public interface NotifyEventDao extends GenericDao<NotifyEvent, Long> {
	
	Map<Long, Long> getGroupMap(String notifyType, PageLimit limit, Date checkTime);

	List<NotifyEvent> listByGroupId(String notifyType, Long groupId, PageLimit limit, Date checkTime);
}