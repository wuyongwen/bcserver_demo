package com.cyberlink.cosmetic.modules.event.dao;

import java.util.List;
import java.util.Map;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.event.model.EventUser;
import com.cyberlink.cosmetic.modules.event.model.EventUserStatus;

public interface EventUserDao extends GenericDao<EventUser, Long>{
	Map<Long, EventUserStatus> getEventUserStatusByEventIds(Long UserId, List<Long> eventIds);
	EventUser findByUserIdAndEventId(Long userId, Long eventId, List<EventUserStatus> userStatus);
	PageResult<Long> findUserIdsByEventId(Long eventId, List<EventUserStatus> userStatus, BlockLimit blockLimit);
	PageResult<Long> findUserIdsByEventIdWithCurUser(Long eventId, List<EventUserStatus> userStatus, Long curUserId, BlockLimit blockLimit);
	PageResult<EventUser> findEventUserByEventId(Long eventId, BlockLimit blockLimit);
	PageResult<EventUser> findEventUserByEventIdAndStatus(List<Long> eventIds, List<EventUserStatus> userStatus, BlockLimit blockLimit);
	List<EventUser> findByIds(Long... ids);
	PageResult<EventUser> findByIds(List<Long> ids, BlockLimit blockLimit);
	List<EventUser> findSelectedEventUsersByEventId(Long eventId);
	PageResult<EventUser> findSelectedEventUsersByEventId(Long eventId, BlockLimit blockLimit);
	PageResult<EventUser> findRedeemedEventUsersByEventId(Long eventId, BlockLimit blockLimit);
	List<Long> findRandomIdsByEventId(Long eventId, EventUserStatus userStatus, BlockLimit blockLimit, Boolean bGroupPhone);
	List<Long> findIdsByEventId(Long eventId, List<EventUserStatus> userStatus);
	Boolean batchRollbackStatus(Long eventId);
	String getCouponCode(Long eventId, Long userId);
	void UpdateEventUserInvailidByMailAndPhone(List<String> mailList, List<String> phoneList, Long eventId, Boolean isInCludeSelectedType, Boolean bUpdateByPhone);
}
