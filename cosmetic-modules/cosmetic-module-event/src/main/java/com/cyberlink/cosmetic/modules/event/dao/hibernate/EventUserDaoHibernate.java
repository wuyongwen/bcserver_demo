package com.cyberlink.cosmetic.modules.event.dao.hibernate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.SQLQuery;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.event.dao.EventUserDao;
import com.cyberlink.cosmetic.modules.event.model.EventUser;
import com.cyberlink.cosmetic.modules.event.model.EventUserStatus;

public class EventUserDaoHibernate extends AbstractDaoCosmetic<EventUser, Long>
implements EventUserDao {

	@Override
	public Map<Long, EventUserStatus> getEventUserStatusByEventIds(Long userId, List<Long> eventIds) {
		Map<Long, EventUserStatus> resultMap = new HashMap<Long, EventUserStatus>();
		if(eventIds == null || eventIds.size() <= 0)
            return resultMap;
		
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.add(Restrictions.eq("userId", userId));
		dc.add(Restrictions.in("eventId", eventIds));
		dc.setProjection(Projections.projectionList()
				.add(Projections.property("eventId"))
				.add(Projections.property("userStatus")));
		
		List<Object> objs = findByCriteria(dc);
		for (Object obj : objs) {
            Object[] row = (Object[]) obj;
            resultMap.put((Long) row[0], (EventUserStatus) row[1]);
        }
		return resultMap;
	}

	@Override
	public EventUser findByUserIdAndEventId(Long userId, Long eventId, List<EventUserStatus> userStatus) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.add(Restrictions.eq("userId", userId));
		dc.add(Restrictions.eq("eventId", eventId));
		if (userStatus != null && !userStatus.isEmpty())
			dc.add(Restrictions.in("userStatus", userStatus));
		return uniqueResult(dc);
	}
	
	@Override
	public PageResult<Long> findUserIdsByEventId(Long eventId, List<EventUserStatus> userStatus, BlockLimit blockLimit) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.add(Restrictions.eq("eventId", eventId));
		if (userStatus != null && !userStatus.isEmpty())
			dc.add(Restrictions.in("userStatus", userStatus));
		dc.setProjection(Projections.property("userId"));
		return blockQuery(dc, blockLimit);
	}
	
	@Override
	public PageResult<Long> findUserIdsByEventIdWithCurUser(Long eventId, List<EventUserStatus> userStatus, Long curUserId, BlockLimit blockLimit) {
		PageResult<Long> result = new PageResult<Long>();
		String sqlCmd = "FROM BC_EVENT_USER "
				+ "WHERE EVENT_ID = :eventId "
				+ "AND STATUS IN (:userStatus) ";	
		String sqlCountCmd = "SELECT count(*) " + sqlCmd;
		String sqlResultCmd = "SELECT USER_ID " + sqlCmd + "ORDER BY ( CASE USER_ID WHEN :curUserId THEN '0' ELSE '1' END ), USER_ID LIMIT :offset , :limit ";
		
		SQLQuery sqlPostsCountQuery = getSession().createSQLQuery(sqlCountCmd);
		if (eventId != null)
			sqlPostsCountQuery.setParameter("eventId", eventId);
		if (userStatus != null) {
			Set<String> status = new HashSet<String>();
			for (EventUserStatus s : userStatus) {
				status.add(s.toString());
			}
			sqlPostsCountQuery.setParameterList("userStatus", status);
		}
		Integer size = ((Number)sqlPostsCountQuery.uniqueResult()).intValue();
		if(size <= 0)
            return result;
		
		SQLQuery sqlPostsQuery = getSession().createSQLQuery(sqlResultCmd);
		if (eventId != null)
			sqlPostsQuery.setParameter("eventId", eventId);
		if (userStatus != null) {
			Set<String> status = new HashSet<String>();
			for (EventUserStatus s : userStatus) {
				status.add(s.toString());
			}
			sqlPostsQuery.setParameterList("userStatus", status);
		}
		if (curUserId != null)
			sqlPostsQuery.setParameter("curUserId", curUserId);
		sqlPostsQuery.setParameter("offset", blockLimit.getOffset());
        sqlPostsQuery.setParameter("limit", blockLimit.getSize());
        List<Object> list = sqlPostsQuery.list();
        List<Long> userIdlist = new ArrayList<Long>();
        for (Object obj : list) {
        	userIdlist.add(Long.valueOf(obj.toString()));
        }
		result.setResults(userIdlist);
		result.setTotalSize(size);
		return result;
	}

	@Override
	public PageResult<EventUser> findEventUserByEventId(Long eventId, BlockLimit blockLimit) {
	    DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("eventId", eventId));
        return blockQuery(dc, blockLimit);
	}
	
	@Override
	public PageResult<EventUser> findEventUserByEventIdAndStatus(List<Long> eventIds, List<EventUserStatus> userStatus, BlockLimit blockLimit) {
		DetachedCriteria dc = createDetachedCriteria();
		if (eventIds != null && eventIds.size() > 0)
			dc.add(Restrictions.in("eventId", eventIds));
		if (userStatus != null && userStatus.size() > 0)
			dc.add(Restrictions.in("userStatus", userStatus));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		return blockQuery(dc, blockLimit);
	}
	
	@Override
	public List<EventUser> findByIds(Long... ids) {
		if (ids == null || ids.length == 0) {
			return Collections.emptyList();
		}

		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.in("id", ids));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));

		return findByCriteria(dc);
	}
	
	@Override
	public PageResult<EventUser> findByIds(List<Long> ids, BlockLimit blockLimit) {
		if (ids == null || ids.isEmpty())
			return new PageResult<EventUser>();
		
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.in("id", ids));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		return blockQuery(dc, blockLimit);
	}
	
	@Override
	public List<EventUser> findSelectedEventUsersByEventId(Long eventId) {
		DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("eventId", eventId));
        dc.add(Restrictions.eq("userStatus", EventUserStatus.Selected));
        
        return findByCriteria(dc);
	}
	
	@Override
	public PageResult<EventUser> findSelectedEventUsersByEventId(Long eventId, BlockLimit blockLimit) {
		DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("eventId", eventId));
        dc.add(Restrictions.eq("userStatus", EventUserStatus.Selected));
        
        return blockQuery(dc, blockLimit);
	}
	
	@Override
	public PageResult<EventUser> findRedeemedEventUsersByEventId(Long eventId, BlockLimit blockLimit) {
		DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("eventId", eventId));
        dc.add(Restrictions.eq("userStatus", EventUserStatus.Redeemed));
        
        return blockQuery(dc, blockLimit);
	}
	
	@Override
	public List<Long> findRandomIdsByEventId(Long eventId, EventUserStatus userStatus, BlockLimit blockLimit, Boolean bGroupPhone) {
		if (eventId == null || userStatus == null || blockLimit == null)
			return null;
		
		String sqlCmd;
		if (bGroupPhone) {
			sqlCmd = "SELECT ID, MAIL "
				+ "FROM ( "
				+ "SELECT * FROM BC_EVENT_USER "
				+ "WHERE EVENT_ID = :eventId "
				+ "AND STATUS = :userStatus "
				+ "AND IS_INVALID = :isInvalid "
				+ "AND IS_DELETED = 0 "
				+ "GROUP BY PHONE "
				+ ") AS G "
				+ "GROUP BY MAIL "
				+ "ORDER BY RAND( ) "
				+ "LIMIT :offset, :limit ";
		} else {
			sqlCmd = "SELECT ID, MAIL "
					+ "FROM BC_EVENT_USER "
					+ "WHERE EVENT_ID = :eventId "
					+ "AND STATUS = :userStatus "
					+ "AND IS_INVALID = :isInvalid "
					+ "AND IS_DELETED = 0 "
					+ "GROUP BY MAIL "
					+ "ORDER BY RAND( ) "
					+ "LIMIT :offset, :limit ";
		}
		
		SQLQuery sqlQuery = getSession().createSQLQuery(sqlCmd);
		sqlQuery.setParameter("eventId", eventId);
		sqlQuery.setParameter("userStatus", userStatus.toString());
		sqlQuery.setParameter("isInvalid", Boolean.FALSE);
		sqlQuery.setParameter("offset", blockLimit.getOffset());
		sqlQuery.setParameter("limit", blockLimit.getSize());
		
		List<Object> list = sqlQuery.list();
        List<Long> userIdlist = new ArrayList<Long>();
        for (Object obj : list) {
        	Object[] row = (Object[]) obj;
        	userIdlist.add(Long.valueOf(row[0].toString()));
        }
        return userIdlist;
	}
	
	@Override
	public List<Long> findIdsByEventId(Long eventId, List<EventUserStatus> userStatus) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.add(Restrictions.eq("eventId", eventId));
		if (userStatus != null && !userStatus.isEmpty())
			dc.add(Restrictions.in("userStatus", userStatus));
		dc.setProjection(Projections.property("id"));
		return findByCriteria(dc);
	}
	
	@Override
	public Boolean batchRollbackStatus(Long eventId) {
		Session session = getSessionFactory().openSession();
		try {
			String sql = "SELECT * FROM `BC_EVENT_USER` "
					+ String.format("WHERE  `EVENT_ID` =  '%s' ",
							eventId.toString())
					+ "AND  `STATUS` =  'Selected' ";
			SQLQuery sqlPostsQuery = null;
			sqlPostsQuery = session.createSQLQuery(sql);
			sqlPostsQuery.addEntity(EventUser.class);

			if (sqlPostsQuery != null) {
				Transaction tx = session.beginTransaction();
				ScrollableResults sr = sqlPostsQuery.scroll();
				int count = 0;
				while (sr.next()) {
					EventUser eventUser = (EventUser) sr.get()[0];
					eventUser.setUserStatus(EventUserStatus.Joined);
					session.update(eventUser);
					if (++count % 50 == 0) {
						session.flush();
						session.clear();
					}
				}
				tx.commit();
			}
			session.close();
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
			if (session != null)
				session.close();
			return false;
		}
	}
	
	@Override
	public String getCouponCode(Long eventId, Long userId) {
		if (userId == null)
			return "";
		
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("eventId", eventId));
		dc.add(Restrictions.eq("userId", userId));
		dc.add(Restrictions.eq("userStatus", EventUserStatus.Selected));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.setProjection(Projections.property("code"));
		
		String code = uniqueResult(dc);
		if (code == null)
			return "";
		return code;
	}
	
	@Override
	public void UpdateEventUserInvailidByMailAndPhone(List<String> mailList, List<String> phoneList, Long  eventId, Boolean isInCludeSelectedType, Boolean bUpdateByPhone) {
		batchUpdateEventUserInvailidByField(mailList, "MAIL", eventId, isInCludeSelectedType);
		if (bUpdateByPhone)
			batchUpdateEventUserInvailidByField(phoneList, "PHONE", eventId, isInCludeSelectedType);
	}
	
	public void batchUpdateEventUserInvailidByField(List<String> valuelist, String fieldName, Long eventId, Boolean isInCludeSelectedType) {
		if(valuelist == null || valuelist.size() == 0)
			return;
		
    	int offset = 0;
        int limit = 50;
        do {
			if (valuelist.size() <= 0)
				break;
        	List<String> subValueList = valuelist.subList(offset,Math.min(offset + limit, valuelist.size()));
        	try {
        		UpdateEventUserInvailidByField(subValueList, fieldName, eventId, isInCludeSelectedType);
        	} catch(Exception e) {
        		logger.info("UpdateEventUserInvailidByMailAndPhone fail. valuelist:" + valuelist + " fieldName:" + fieldName + "  message:" + e.getMessage());
        		break;
        	}
        	offset += limit;
        	if(offset%200 == 0){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					break;
				}
        	}
        	if(offset >= valuelist.size())
        		break;
        }while(true);
	}
	
	public int UpdateEventUserInvailidByField(List<String> valuelist, String fieldName, Long eventId, Boolean isInCludeSelectedType) {
		if(valuelist.size() == 0)
			return 0;
		String sqlCmd = "UPDATE BC_EVENT_USER SET IS_INVALID = 1 , STATUS = 'Joined', CODE = NULL WHERE EVENT_ID = :eventId AND " + fieldName + " IN ( :listString ) "
				+ "AND IS_INVALID = 0 AND IS_DELETED = 0 ";
		if(isInCludeSelectedType){
			sqlCmd += "AND (STATUS = 'joined' OR STATUS = 'selected') ";
		}else{
			sqlCmd += "AND STATUS = 'joined' ";
		}
		SQLQuery sqlQuery = getSession().createSQLQuery(sqlCmd);
		sqlQuery.setParameter("eventId", eventId);
		sqlQuery.setParameterList("listString", valuelist);
		return sqlQuery.executeUpdate();
	}
}
