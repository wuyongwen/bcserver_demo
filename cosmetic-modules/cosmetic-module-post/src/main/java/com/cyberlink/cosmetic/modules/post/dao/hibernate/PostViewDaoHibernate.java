package com.cyberlink.cosmetic.modules.post.dao.hibernate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.cosmetic.modules.post.dao.PostViewDao;
import com.cyberlink.cosmetic.modules.post.model.PostView;
import com.cyberlink.cosmetic.modules.post.model.PostViewAttr;

public class PostViewDaoHibernate extends AbstractDaoCosmetic<PostView, Long>
    implements PostViewDao {

    private String regionOfGetViewMapByCLPostIds = "com.cyberlink.cosmetic.modules.post.model.PostView.query.getViewMapByCLPostIds";
    
    @Override
    public Map<Long, PostView> getViewMapByPostIds(List<Long> postIds) {
        Map<Long, PostView> resultMap = new LinkedHashMap<Long, PostView>();
        if(postIds == null || postIds.size() <= 0)
            return resultMap;
        for(Long postId : postIds)
            resultMap.put(postId, null);
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("postId", postIds));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        List<PostView> resultList = findByCriteria(dc);
        for(PostView pV : resultList)
            resultMap.put(pV.getPostId(), pV);
        
        return resultMap;
    }
    
    @Override
    public PostView findByPostId(Long postId) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("postId", postId));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return uniqueResult(dc);
    }
    
    @Override
    public PostView createOrUpdate(Long postId, Long creatorId, String mainPost, String subPosts, PostViewAttr attribute) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("postId", postId));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        PostView result = uniqueResult(dc);
        if(result == null) {
            result = new PostView();
            result.setPostId(postId);
            result.setShardId(creatorId);
            if(mainPost != null)
                result.setMainPost(mainPost);
            if(subPosts != null)
                result.setSubPosts(subPosts);
            if(attribute != null)
                result.setAttribute(attribute);
            result = create(result);
        }
        else {
            if(mainPost != null)
                result.setMainPost(mainPost);
            if(subPosts != null)
                result.setSubPosts(subPosts);
            if(attribute != null)
                result.setAttribute(attribute);
            result = update(result);
        }
        
        return result;
    }
    
    @Override
    public Map<Long, String> getViewMapByCLPostIds(List<Long> postIds) {
        Map<Long, String> resultMap = new LinkedHashMap<Long, String>();
        if(postIds == null || postIds.size() <= 0)
            return resultMap;
        for(Long postId : postIds)
            resultMap.put(postId, null);
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("postId", postIds));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.projectionList()
                .add(Projections.property("postId"))
                .add(Projections.property("mainPost")));
        
        List<Object[]> resultList = findByCriteria(dc, regionOfGetViewMapByCLPostIds);
        for(Object[] objs : resultList)
            resultMap.put((Long)objs[0], (String)objs[1]);
        
        return resultMap;
    }

    @Override
    public int bacthDeleteByPostId(List<Long> postIds) {
        if(postIds == null || postIds.size() <= 0)
            return 0;
        
        String batchDeleteSqlCmd = "DELETE FROM `BC_POST_VIEW` WHERE `POST_ID` IN (:postIds)";      
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(batchDeleteSqlCmd);
        sqlPostsQuery.setParameterList("postIds", postIds);
        return sqlPostsQuery.executeUpdate();
    }
    
}
