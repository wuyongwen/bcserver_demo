package com.cyberlink.cosmetic.modules.post.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.post.dao.PostCircleInDao;
import com.cyberlink.cosmetic.modules.post.model.PostCircleIn;
import com.cyberlink.cosmetic.modules.user.model.User;

public class PostCircleInDaoHibernate extends AbstractDaoCosmetic<PostCircleIn, Long>
    implements PostCircleInDao{
    
    @Override
    public Map<Long, User> getSourceUser(List<Long> postIds)
    {
        Map<Long, User> result = new HashMap<Long, User>();
        if(postIds == null || postIds.size() <= 0)
            return result;
        
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.in("postId", postIds));
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        List<PostCircleIn> circleInList = findByCriteria(d);
        for (PostCircleIn cIn : circleInList) {
            result.put(cIn.getPostId(), cIn.getSourceUser());
        }
        return result;
    }
    
    @Override
    public Map<Long, Long> getCircleInCounts(List<Long> sourcePostIds)
    {
        Map<Long, Long> result = new HashMap<Long, Long>();
        if(sourcePostIds == null || sourcePostIds.size() <= 0)
            return result;
        
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.in("sourcePostId", sourcePostIds));
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        d.setProjection(Projections.projectionList()
                .add(Projections.groupProperty("sourcePostId"))
                .add(Projections.rowCount(), "count"));
        List<Object> objs = findByCriteria(d);
        for (Object obj : objs) {
            Object[] row = (Object[]) obj;
            result.put((Long) row[0], (Long) row[1]);
        }
        return result;
    }
    
    @Override
    public PageResult<User> listCircleInUser(Long postId, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("sourcePostId", postId));
        dc.setProjection(Projections.groupProperty("user"));
        return blockQuery(dc, blockLimit);
    }
    
	@Override
    public PageResult<Circle> listCircleInCircle(Long postId, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("sourcePostId", postId));
        dc.add(Restrictions.isNotNull("circleId")); //skip processing old data (when circleId is null)
        dc.setProjection(Projections.groupProperty("circle"));
        return blockQuery(dc, blockLimit);
    }

	@Override
	public List<Long> listPostIdBySource(Long userId, Long postId) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("sourcePostId", postId));
        dc.add(Restrictions.eq("userId", userId));
        dc.setProjection(Projections.property("postId"));		
		return findByCriteria(dc);
	}
    
	@Override
    public PageResult<Circle> listCircleInCircle(Long userId, Long postId, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("sourcePostId", postId));
        dc.add(Restrictions.eq("userId", userId));
        dc.createAlias("post", "postAlias");
        dc.createAlias("postAlias.circle", "circle");
        dc.add(Restrictions.eq("postAlias.isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("circle.isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.property("postAlias.circle"));
        return blockQuery(dc, blockLimit);
    }

    @Override
    public PostCircleIn findByPostId(Long postId) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("postId", postId));
        return uniqueResult(dc);
    }	
}
