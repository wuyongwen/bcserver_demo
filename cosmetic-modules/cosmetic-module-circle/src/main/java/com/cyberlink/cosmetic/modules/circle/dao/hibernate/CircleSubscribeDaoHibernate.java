package com.cyberlink.cosmetic.modules.circle.dao.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.dao.hibernate.ScrollableResultsCallback;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.circle.dao.CircleSubscribeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleSubscribe;
import com.cyberlink.cosmetic.modules.user.model.User;

public class CircleSubscribeDaoHibernate extends AbstractDaoCosmetic<CircleSubscribe, Long>
		implements CircleSubscribeDao {

    @Override
    public List<CircleSubscribe> getCircleSubscribe(Long userId, Long circleId) {
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.eq("userId", userId));
        d.add(Restrictions.eq("circleId", circleId));
        return findByCriteria(d);
    }

    @Override
	public List<CircleSubscribe> findSubscribeByUserId(Long userId) {
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.eq("userId", userId));
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		return findByCriteria(d);
	}	
    
	@Override
	public PageResult<Circle> findByUserId(Long userId, BlockLimit blockLimit) {
		final DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("userId", userId));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.createAlias("circle", "circle");
        dc.add(Restrictions.eq("circle.isSecret", Boolean.FALSE));
		PageResult<CircleSubscribe> circleSubscribeResult = blockQuery(dc, blockLimit);
		PageResult<Circle> circleResult = new PageResult<Circle>();
		circleResult.setTotalSize(circleSubscribeResult.getTotalSize());
		for(CircleSubscribe cs : circleSubscribeResult.getResults()) {
		    circleResult.getResults().add(cs.getCircle());
		}
		return circleResult;
	}
	
	@Override
    public List<Long> findByUserId(Long userId) {
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("userId", userId));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.property("circleId"));
        return findByCriteria(dc);
    }
	
	@Override
    public PageResult<User> findByCircleId(Long circleId, BlockLimit blockLimit) {
	    final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("circleId", circleId));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        PageResult<CircleSubscribe> circleSubscribeResult = blockQuery(dc, blockLimit);
        PageResult<User> userResult = new PageResult<User>();
        userResult.setTotalSize(circleSubscribeResult.getTotalSize());
        for(CircleSubscribe cs : circleSubscribeResult.getResults()) {
            userResult.getResults().add(cs.getUser());
        }
        return userResult;
    }
	
	@Override
	public List<Long> listSubcribeCircle(Long userId, List<Circle> circles) {
        if(circles == null || circles.size() <= 0 || userId == null)
            return new ArrayList<Long>(0);
        
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.eq("userId", userId));
        d.add(Restrictions.in("circle", circles));
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        d.setProjection(Projections.property("circleId"));
        return findByCriteria(d);
    }
	
	@Override
    public List<CircleSubscribe> listCircleSubcribe(Long userId, Long creatorId) {
        if(creatorId == null || userId == null)
            return new ArrayList<CircleSubscribe>();
        
        DetachedCriteria d = createDetachedCriteria();
        d.createAlias("circle", "circle");
        d.add(Restrictions.eq("userId", userId));
        d.add(Restrictions.eq("circle.creatorId", creatorId));
        return findByCriteria(d);
    }
	
	@Override
	public Long getSubscribeCountByCircleCreator(Long userId, Long circleCreatorId) {
	    DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.eq("userId", userId));
        d.createAlias("circle", "relCircle");
        d.add(Restrictions.eq("relCircle.creatorId", circleCreatorId));
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        d.setProjection(Projections.rowCount());
        return uniqueResult(d);
	}
	
	@Override
	public PageResult<CircleSubscribe> findAllCircleSubscribe(Date startTime, Date endTime, BlockLimit blockLimit) {
	    final DetachedCriteria dc = createDetachedCriteria();
	    if (startTime != null)
            dc.add(Restrictions.ge("lastModified", startTime));
        if (endTime != null)
            dc.add(Restrictions.le("lastModified", endTime));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return blockQuery(dc, blockLimit);
	}
	
	@Override
    public Integer bacthDeleteSubscribe(Long circleId) {
        String updateSqlCmd = "UPDATE BC_CIRCLE_SUBSCRIBE SET IS_DELETED = 1 WHERE CIRCLE_ID = :circleId";
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(updateSqlCmd);
        sqlPostsQuery.setParameter("circleId", circleId);
        return sqlPostsQuery.executeUpdate();
    }
	
    @Override
	public void bacthDeleteByCircleCreator(Long userId) {
		String format = "UPDATE `BC_CIRCLE_SUBSCRIBE` SET IS_DELETED = '1' WHERE `CIRCLE_ID` IN ("
                + "SELECT `ID` FROM `BC_CIRCLE` WHERE `BC_CIRCLE`.`USER_ID` = '%s')";
		String sql = String.format(format, userId.toString());			
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(sql);
        sqlPostsQuery.executeUpdate();
	}
    	
	@Override
	public List<Long> listByCircleId(Long circleId) {
		final DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("circleId", circleId));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.setProjection(Projections.projectionList()
				.add(Projections.groupProperty("userId"))
				.add(Projections.property("id")));
		return findByCriteria(dc);
	}
	
	@Override
	public List<Long> listUserIdByCircleId(Long circleId) {
		final DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("circleId", circleId));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.setProjection(Projections.property("userId"));
		return findByCriteria(dc);
	}

    @Override
    public void doWithAllCircleSubscribe(ScrollableResultsCallback callback) {
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.projectionList()
                .add(Projections.property("userId"))
                .add(Projections.property("circleId")));
        final Criteria c = dc.getExecutableCriteria(getSession());
        final ScrollableResults sr = c.scroll(ScrollMode.FORWARD_ONLY);
        try {
            callback.doInHibernate(sr);
        } finally {
            sr.close();
        }
    }
	
    @Override
    public void doWithAllCircleSubscribeBetween(Date begin, Date end, ScrollableResultsCallback callback) {
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        if(begin != null)
            dc.add(Restrictions.ge("createdTime", begin));
        if(end != null)
            dc.add(Restrictions.lt("createdTime", end));
        
        dc.setProjection(Projections.projectionList()
                .add(Projections.property("userId"))
                .add(Projections.property("circleId"))
                .add(Projections.property("createdTime")));
        final Criteria c = dc.getExecutableCriteria(getSession());
        final ScrollableResults sr = c.scroll(ScrollMode.FORWARD_ONLY);
        try {
            callback.doInHibernate(sr);
        } finally {
            sr.close();
        }
    }
    
}
