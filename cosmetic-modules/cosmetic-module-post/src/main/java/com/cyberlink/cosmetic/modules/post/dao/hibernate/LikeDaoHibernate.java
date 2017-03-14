package com.cyberlink.cosmetic.modules.post.dao.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.dao.LikeDao;
import com.cyberlink.cosmetic.modules.post.model.Like;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetSubType;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetType;
import com.cyberlink.cosmetic.modules.user.model.User;

public class LikeDaoHibernate extends AbstractDaoCosmetic<Like, Long>
    implements LikeDao {
    
    @Override
    public Map<Long, Long> hardGetLikeCountByTargetsWithoutEmpty(TargetType refType, List<Long> refIds)
    {
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.eq("refType", refType));
        d.add(Restrictions.in("refId", refIds));
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        d.setProjection(Projections.projectionList()
                .add(Projections.groupProperty("refId"))
                .add(Projections.rowCount(), "count"));
        Map<Long, Long> result = new HashMap<Long, Long>();
        List<Object> objs = findByCriteria(d);
        for (Object obj : objs) {
            Object[] row = (Object[]) obj;
            result.put((Long) row[0], (Long) row[1]);
        }
        for (Long id : refIds) {
        	if (result.containsKey(id))
        		continue;
        	result.put(id, (long) 0);
        }
        return result;
    }
    
    @Override public Like getLike(User user, TargetType refType, Long targetId) {
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.eq("refId", targetId));
        d.add(Restrictions.eq("refType", refType));
        d.add(Restrictions.eq("user", user));
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return uniqueResult(d);
    }
    
    @Override public List<Long> listLikedTarget(Long userId, TargetType refType, List<Long> targetIds) {
        List<Long> resultList = new ArrayList<Long>(0);
        if(targetIds == null || targetIds.size() <= 0)
            return resultList;
        
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.in("refId", targetIds));
        d.add(Restrictions.eq("refType", refType));
        d.add(Restrictions.eq("userId", userId));
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        List<Like> result = findByCriteria(d);
        for(Like r : result) {
            resultList.add(r.getRefId());
        }
        return resultList;
    }

    @Override
    public Long hardGetLikedPostCount(Long userId, TargetType refType, TargetSubType refSubType) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("refType", refType));
        dc.add(Restrictions.eq("refSubType", refSubType));
        dc.add(Restrictions.eq("userId", userId));
        dc.setProjection(Projections.rowCount());
        return uniqueResult(dc);
    }
    
    @Override
    public Integer hardGetLikedTargetId(Long userId, TargetType refType, TargetSubType refSubType, List<Long> result, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("refType", refType));
        dc.add(Restrictions.eq("refSubType", refSubType));
        dc.add(Restrictions.eq("userId", userId));
        dc.addOrder(Order.desc("id"));                
        dc.setProjection(Projections.property("refId"));
        PageResult<Long> tmpResult = blockQuery(dc, blockLimit);
        result.addAll(tmpResult.getResults());
        return tmpResult.getTotalSize();
    }
    
    @Override
    public void getLikedTargetIdWithoutSize(Long userId, TargetType refType, TargetSubType refSubType, List<Long> result, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("refType", refType));
        dc.add(Restrictions.eq("refSubType", refSubType));
        dc.add(Restrictions.eq("userId", userId));
        dc.addOrder(Order.desc("id"));                
        dc.setProjection(Projections.property("refId"));
        PageResult<Long> tmpResult = blockQueryWithoutSize(dc, blockLimit);
        result.addAll(tmpResult.getResults());
    }
    
    @Override
    public void updateByDeletePost(TargetType refType, Long refId) {
        String format = "UPDATE `BC_LIKE` SET `IS_DELETED`= '1' WHERE `REF_TYPE` = '%s' AND `REF_ID` = '%s'";
        String sql = String.format(format, refType.toString(), refId.toString());
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(sql);
        sqlPostsQuery.executeUpdate();
        return;		        
    }
    
    @Override
    public PageResult<Like> blockQueryWithoutSize(TargetType refType, Long refId, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("refType", refType));
        dc.add(Restrictions.eq("refId", refId));
        return blockQueryWithoutSize(dc, blockLimit);
    }
    
    @Override
    public PageResult<Like> findByUserId(Long userId, TargetType refType, Boolean withSize, BlockLimit blockLimit) {	
		DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("userId", userId));
        if (refType != null)
        	dc.add(Restrictions.eq("refType", refType));
        if(withSize != null && withSize)
            return blockQuery(dc, blockLimit);
        return blockQueryWithoutSize(dc, blockLimit);
    }

    @Override
    public PageResult<Like> getAllLikeUserIds(List<Long> postIds, Boolean withSize, BlockLimit blockLimit) {  
        if(postIds == null || postIds.size() <= 0)
            return new PageResult<Like>();
        
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.in("refId", postIds));
        dc.add(Restrictions.eq("refType", TargetType.Post));
        if(withSize != null && withSize)
            return blockQuery(dc, blockLimit);
        return blockQueryWithoutSize(dc, blockLimit);
    }
    
	@Override
    public int bacthDeleteByTargets(TargetType refType, List<Long> refIds) {
	    if(refIds == null || refIds.size() <= 0)
	        return 0;
	    
        String batchDeleteSqlCmd = "UPDATE `BC_LIKE` SET IS_DELETED = '1' WHERE `REF_TYPE` = :refType AND `REF_ID` IN (:refIds)";      
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(batchDeleteSqlCmd);
        sqlPostsQuery.setParameterList("refIds", refIds);
        sqlPostsQuery.setParameter("refType", refType.toString());
        return sqlPostsQuery.executeUpdate();
    }
	
	@Override
    public void doWithAllLikeBetween(TargetType refType, Long next, Long count, ScrollableResultsCallback callback) {
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("refType", refType));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.gt("id", next));
        dc.addOrder(Order.asc("id"));       
        dc.setProjection(Projections.projectionList()
                .add(Projections.property("id"))
                .add(Projections.property("refId"))
                .add(Projections.property("userId"))
                .add(Projections.property("createdTime")));
        final Criteria c  = dc.getExecutableCriteria(getSession());
        c.setFirstResult(0);
        c.setMaxResults(count.intValue());
        final ScrollableResults sr = c.scroll(ScrollMode.FORWARD_ONLY);
        try {
            callback.doInHibernate(sr);
        } finally {
            sr.close();
        }
    }
	
	@Override
    public void doWithAllLike(TargetType refType, List<Long> refIds, ScrollableResultsCallback callback) {
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("refType", refType));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.in("refId", refIds));
        dc.setProjection(Projections.projectionList()
                .add(Projections.property("refId"))
                .add(Projections.property("userId")));
        final Criteria c  = dc.getExecutableCriteria(getSession());
        final ScrollableResults sr = c.scroll(ScrollMode.FORWARD_ONLY);
        try {
            callback.doInHibernate(sr);
        } finally {
            sr.close();
        }
    }
}
