package com.cyberlink.cosmetic.modules.user.dao.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.dao.hibernate.ScrollableResultsCallback;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.model.Subscribe;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;

public class SubscribeDaoHibernate extends AbstractDaoCosmetic<Subscribe, Long> implements SubscribeDao{
	private String regionOfFindBySubscribee = "com.cyberlink.cosmetic.modules.user.model.Subscribe.query.findBySubscribee";
	
	public List<Subscribe> findBySubscribeeId(Long subscribeeId, PageLimit limit) {
		DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("subscribeeId", subscribeeId));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return findByCriteria(dc, limit);
	}
	
	public List<Subscribe> findBySubscriberId(Long subscriberId, PageLimit limit) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("subscriberId", subscriberId));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return findByCriteria(dc, limit);
	}

    @Override
    public Subscribe findBySubscriberAndSubscribee(Long subscriberId,
            Long subscribeeId, SubscribeType subscribeType) {
    	if(subscribeType == null)
    		return null;
    	
    	DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("subscriberId", subscriberId));
        dc.add(Restrictions.eq("subscribeeId", subscribeeId));
        dc.add(Restrictions.eq("subscribeType", subscribeType));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return uniqueResult(dc);
    }

	@Override
	public List<Subscribe> findBySubscriberAndSubscribees(Long subscriberId, SubscribeType subscribeType,
			Long... subscribeeIds) {
		if (subscribeeIds == null || subscribeeIds.length == 0) {
			return new ArrayList<Subscribe>();
		}
		
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("subscriberId", subscriberId));
        dc.add(Restrictions.in("subscribeeId", subscribeeIds));
        if(subscribeType != null)
            dc.add(Restrictions.eq("subscribeType", subscribeType));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return findByCriteria(dc);
	}
	
	@Override
	public List<Subscribe> findBySubscribeeAndSubscribers(Long subscribeeId, SubscribeType subscribeType,
			Long... subscriberIds) {
		if (subscriberIds == null || subscriberIds.length == 0) {
			return new ArrayList<Subscribe>();
		}
		
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("subscriberId", subscriberIds));
        dc.add(Restrictions.eq("subscribeeId", subscribeeId));
        if(subscribeType != null)
            dc.add(Restrictions.eq("subscribeType", subscribeType));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return findByCriteria(dc);
	}

	@Override
    public PageResult<Long> findBySubscribee(Long subscribeeId, SubscribeType subscribeType, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("subscribeeId", subscribeeId));
        if(subscribeType != null)
            dc.add(Restrictions.eq("subscribeType", subscribeType));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.distinct(Projections.property("subscriberId")));
        return groupQuery(dc, "subscriberId", blockLimit);
    }
	
	@Override
    public PageResult<Long> findBySubscribeeOrderByName(Long subscribeeId, SubscribeType subscribeType, BlockLimit blockLimit, Boolean withSize) {
		PageResult<Long> result = new PageResult<Long>();
		String sqlCmd = "LEFT(SUBSCRIBER_NAME, 1) REGEXP '[a-zA-Z]' as isAlpha "
				+ "FROM BC_USER_SUBSCRIBE "
				+ "USE INDEX (subscriberNameSort) "
				+ "WHERE SUBSCRIBEE_ID = :subscribeeId "
				+ "AND IS_DELETED = 0 ";
		if (subscribeType != null)
			sqlCmd += "AND SUBSCRIBE_TYPE = :subscribeType ";
		
		String selectCmd = "SELECT DISTINCT SUBSCRIBER_ID, " + sqlCmd 
				+ "ORDER BY ISNULL(SUBSCRIBER_NAME), isAlpha desc, SUBSCRIBER_NAME "
				+ "LIMIT :offset, :limit";
        SQLQuery sqlSelectQuery = getSession().createSQLQuery(selectCmd);
        sqlSelectQuery.setParameter("subscribeeId", subscribeeId);
        sqlSelectQuery.setParameter("offset", blockLimit.getOffset());
        sqlSelectQuery.setParameter("limit", blockLimit.getSize());
        if (subscribeType != null)
        	sqlSelectQuery.setParameter("subscribeType", subscribeType.toString());
        List<Object> list = sqlSelectQuery.list();
        List<Long> userIdlist = new ArrayList<Long>();
        for (Object obj : list) {
        	Object[] row = (Object[]) obj;
        	userIdlist.add(Long.valueOf(row[0].toString()));
        }
        result.setResults(userIdlist);
        
		if (withSize) {
			String countCmd = "SELECT COUNT(DISTINCT SUBSCRIBER_ID), " + sqlCmd;
			SQLQuery sqlCountQuery = getSession().createSQLQuery(countCmd);
			sqlCountQuery.setParameter("subscribeeId", subscribeeId);
			if (subscribeType != null)
				sqlCountQuery.setParameter("subscribeType", subscribeType.toString());
			Object[] row = (Object[]) sqlCountQuery.uniqueResult();
			Integer size = ((Number) row[0]).intValue();
			result.setTotalSize(size);
		}
        return result;
    }

	@Override
	public PageResult<Long> findBySubscriber(Long subscriberId, SubscribeType subscribeType, BlockLimit blockLimit) {
        return findBySubscriber(subscriberId, subscribeType, blockLimit, true);
	}
	
	@Override
	public PageResult<Long> findBySubscriber(Long subscriberId, SubscribeType subscribeType, BlockLimit blockLimit, Boolean withSize) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("subscriberId", subscriberId));
        if(subscribeType != null)
            dc.add(Restrictions.eq("subscribeType", subscribeType));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.distinct(Projections.property("subscribeeId")));
        if (withSize)
        	return groupQuery(dc, "subscribeeId", blockLimit);
        else
        	return groupQueryWithoutSize(dc, "subscribeeId", blockLimit);
	}
	
	@Override
	public PageResult<Long> findBySubscriberOrderByName(Long subscriberId, SubscribeType subscribeType, BlockLimit blockLimit, Boolean withSize) {
		PageResult<Long> result = new PageResult<Long>();
		String sqlCmd = "LEFT(SUBSCRIBEE_NAME, 1) REGEXP '[a-zA-Z]' as isAlpha "
				+ "FROM BC_USER_SUBSCRIBE "
				+ "USE INDEX (subscribeeNameSort) "
				+ "WHERE SUBSCRIBER_ID = :subscriberId "
				+ "AND IS_DELETED = 0 ";
		if (subscribeType != null)
			sqlCmd += "AND SUBSCRIBE_TYPE = :subscribeType ";
		
		String selectCmd = "SELECT DISTINCT SUBSCRIBEE_ID, " + sqlCmd 
				+ "ORDER BY ISNULL(SUBSCRIBEE_NAME), isAlpha desc, SUBSCRIBEE_NAME "
				+ "LIMIT :offset, :limit";
        SQLQuery sqlSelectQuery = getSession().createSQLQuery(selectCmd);
        sqlSelectQuery.setParameter("subscriberId", subscriberId);
        sqlSelectQuery.setParameter("offset", blockLimit.getOffset());
        sqlSelectQuery.setParameter("limit", blockLimit.getSize());
        if (subscribeType != null)
        	sqlSelectQuery.setParameter("subscribeType", subscribeType.toString());
        List<Object> list = sqlSelectQuery.list();
        List<Long> userIdlist = new ArrayList<Long>();
        for (Object obj : list) {
        	Object[] row = (Object[]) obj;
        	userIdlist.add(Long.valueOf(row[0].toString()));
        }
        result.setResults(userIdlist);
        
		if (withSize) {
			String countCmd = "SELECT COUNT(DISTINCT SUBSCRIBEE_ID), " + sqlCmd;
			SQLQuery sqlCountQuery = getSession().createSQLQuery(countCmd);
			sqlCountQuery.setParameter("subscriberId", subscriberId);
			if (subscribeType != null)
				sqlCountQuery.setParameter("subscribeType", subscribeType.toString());
			Object[] row = (Object[]) sqlCountQuery.uniqueResult();
			Integer size = ((Number) row[0]).intValue();
			result.setTotalSize(size);
		}
        return result;
	}

	@Override
	public PageResult<User> findFollowingWithoutUserType(Long userId,
			UserType userType, Long offset, Long limit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.createAlias("subscribee", "s");
        dc.add(Restrictions.eq("subscriberId", userId));
        dc.add(Restrictions.ne("s.userType", userType));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("subscribeType", SubscribeType.User));
        dc.setProjection(Projections.property("subscribee"));
        return findByCriteria(dc, offset, limit, null);
	}

	@Override
	public Set<Long> findIdBySubscriberAndSubscribees(Long subscriberId, SubscribeType subscribeType, 
			Long... subscribeeIds) {
		if (subscribeeIds == null || subscribeeIds.length == 0) {
			return new HashSet<Long>();
		}
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("subscriberId", subscriberId));
        dc.add(Restrictions.in("subscribeeId", subscribeeIds));
        if(subscribeType != null)
            dc.add(Restrictions.eq("subscribeType", subscribeType));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.distinct(Projections.property("subscribeeId")));
        List<Long> list = findByCriteria(dc);
        return new HashSet<Long>(list);
	}
	
    @Override
    public List<Long> findBySubscriber(Long subscriberId, SubscribeType subscribeType) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("subscriberId", subscriberId));
        if(subscribeType != null)
            dc.add(Restrictions.eq("subscribeType", subscribeType));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.distinct(Projections.property("subscribeeId")));
        return findByCriteria(dc);
    }
    
	@Override
	public List<Long> findBySubscribee(Long subscribeeId, SubscribeType subscribeType) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("subscribeeId", subscribeeId));
        if(subscribeType != null)
            dc.add(Restrictions.eq("subscribeType", subscribeType));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.distinct(Projections.property("subscriberId")));
        return findByCriteria(dc);
	}

	@Override
	public List<Subscribe> findByUser(Long userId) {
        DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.or(
                Restrictions.eq("subscriberId", userId),
                Restrictions.eq("subscribeeId", userId)));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		//dc.add(Restrictions.eq("subscribeType", SubscribeType.User));
		return findByCriteria(dc);
	}

	@Override
	public PageResult<Subscribe> findAllSubscribe(Date startTime, Date endTime, Long offset, Long limit) {
        DetachedCriteria dc = createDetachedCriteria();
        if (startTime != null)
        	dc.add(Restrictions.ge("lastModified", startTime));
        if (endTime != null)
        	dc.add(Restrictions.le("lastModified", endTime));
        dc.add(Restrictions.eq("subscribeType", SubscribeType.User));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return findByCriteria(dc, offset, limit, null);
	}

	@Override
	public Map<Long, Long> getFollowerCountByUserIds(List<Long> userIds, SubscribeType subscribeType) {
        Map<Long, Long> resultMap = new HashMap<Long, Long>();
        if(userIds == null || userIds.size() <= 0)
            return resultMap;
        
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("subscribeeId", userIds));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.projectionList()
                .add(Projections.groupProperty("subscribeeId"))
                .add(Projections.countDistinct("subscriberId")));
        if(subscribeType != null)
            dc.add(Restrictions.eq("subscribeType", subscribeType));
        
        List<Object> objs = findByCriteria(dc);
        for (Object obj : objs) {
            Object[] row = (Object[]) obj;
            resultMap.put((Long) row[0], (Long) row[1]);
        }
        return resultMap;
	}

	@Override
	public Map<Long, Long> getFollowingCountByUserIds(List<Long> userIds, SubscribeType subscribeType) {
        Map<Long, Long> resultMap = new HashMap<Long, Long>();
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("subscriberId", userIds));
        if(subscribeType != null)
            dc.add(Restrictions.eq("subscribeType", subscribeType));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.projectionList()
                .add(Projections.groupProperty("subscriberId"))
                .add(Projections.countDistinct("subscribeeId")));
        List<Object> objs = findByCriteria(dc);
        for (Object obj : objs) {
            Object[] row = (Object[]) obj;
            resultMap.put((Long) row[0], (Long) row[1]);
        }
        return resultMap;
	}
	
	@Override
	public Integer bacthDeleteSubscribe(Long subscribeeId, SubscribeType subscribeType) {
	    String updateSqlCmd = "DELETE FROM BC_USER_SUBSCRIBE WHERE SUBSCRIBEE_ID = :subscribeeId AND SUBSCRIBE_TYPE = :subscribeType";
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(updateSqlCmd);
        sqlPostsQuery.setParameter("subscribeeId", subscribeeId);
        sqlPostsQuery.setParameter("subscribeType", subscribeType.toString());
        return sqlPostsQuery.executeUpdate();
	}

	@Override
	public Integer bacthDeleteBySubscriber(Long subscriberId, SubscribeType subscribeType) {
	    String updateSqlCmd = "DELETE FROM BC_USER_SUBSCRIBE WHERE SUBSCRIBER_ID = :subscriberId AND SUBSCRIBE_TYPE = :subscribeType";
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(updateSqlCmd);
        sqlPostsQuery.setParameter("subscriberId", subscriberId);
        sqlPostsQuery.setParameter("subscribeType", subscribeType.toString());
        return sqlPostsQuery.executeUpdate();
	}
	
	@Override
	public void batchDelete(List<Long> ids) {
    	if (ids == null || ids.isEmpty())
    		return;
    	
		String sqlCmd = "DELETE FROM BC_USER_SUBSCRIBE WHERE ID IN :ids";
		SQLQuery sqlQuery = getSession().createSQLQuery(sqlCmd);
		sqlQuery.setParameterList("ids", ids);
		sqlQuery.executeUpdate();
	}

    @Override
    public void doWithAllValidSubscribe(ScrollableResultsCallback callback) {
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.projectionList()
                .add(Projections.property("subscriberId"))
                .add(Projections.property("subscribeeId"))
                .add(Projections.property("subscribeType"))
                .add(Projections.property("createdTime")));
        final Criteria c  = dc.getExecutableCriteria(getSession());
        final ScrollableResults sr = c.scroll(ScrollMode.FORWARD_ONLY);
        try {
            callback.doInHibernate(sr);
        } finally {
            sr.close();
        }
    }

    @Override
    public void doWithAllValidSubscribeBetween(Date begin, Date end, ScrollableResultsCallback callback) {
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        if(begin != null)
            dc.add(Restrictions.ge("createdTime", begin));
        if(end != null)
            dc.add(Restrictions.lt("createdTime", end));
        
        dc.setProjection(Projections.projectionList()
                .add(Projections.property("subscriberId"))
                .add(Projections.property("subscribeeId"))
                .add(Projections.property("subscribeType"))
                .add(Projections.property("createdTime")));
        final Criteria c  = dc.getExecutableCriteria(getSession());
        final ScrollableResults sr = c.scroll(ScrollMode.FORWARD_ONLY);
        try {
            callback.doInHibernate(sr);
        } finally {
            sr.close();
        }
    }
    
    @Override
    public void doWithAllValidSubscribe(SubscribeType subscribeType,
            ScrollableResultsCallback callback) {
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("subscribeType", subscribeType));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.projectionList()
                .add(Projections.property("subscriberId"))
                .add(Projections.property("subscribeeId")));
        final Criteria c  = dc.getExecutableCriteria(getSession());
        final ScrollableResults sr = c.scroll(ScrollMode.FORWARD_ONLY);
        try {
            callback.doInHibernate(sr);
        } finally {
            sr.close();
        }        
    }
   
    @Override
    public void doWithAllValidSubscribeBetween(Date begin, Date end, SubscribeType subscribeType,
            ScrollableResultsCallback callback) {
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("subscribeType", subscribeType));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        if(begin != null)
            dc.add(Restrictions.ge("createdTime", begin));
        if(end != null)
            dc.add(Restrictions.lt("createdTime", end));
        
        dc.setProjection(Projections.projectionList()
                .add(Projections.property("subscriberId"))
                .add(Projections.property("subscribeeId")));
        final Criteria c  = dc.getExecutableCriteria(getSession());
        final ScrollableResults sr = c.scroll(ScrollMode.FORWARD_ONLY);
        try {
            callback.doInHibernate(sr);
        } finally {
            sr.close();
        }        
    }
    
}
