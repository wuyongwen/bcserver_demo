package com.cyberlink.cosmetic.modules.post.dao.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.dao.hibernate.ScrollableResultsCallback;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.dao.PostNewDao;
import com.cyberlink.cosmetic.modules.post.model.AppName;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostNew;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.model.PostType;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute.PostAttrType;

public class PostNewDaoHibernate extends AbstractDaoCosmetic<PostNew, Long>
    implements PostNewDao {

    private String regionOfFindNewPostView = "com.cyberlink.cosmetic.modules.post.model.Post.query.findNewPostView";
    private String regionOfFindNewPostByLook = "com.cyberlink.cosmetic.modules.post.model.Post.query.findNewPostViewByLook";
    
    @Override
    public PageResult<Post> findNewPost(Long circleTypeId, List<String> locales, List<PostStatus> status, Boolean withLook, BlockLimit blockLimit) {
        PageResult<Post> result = new PageResult<Post>();
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", false));
        dc.createAlias("post", "postAlias");
        dc.add(Restrictions.eq("postAlias.isDeleted", false));
        if(circleTypeId != null)
            dc.add(Restrictions.eq("circleTypeId", circleTypeId));
        else {
            dc.add(Restrictions.eq("mainType", true));
            dc.add(Restrictions.eq("forceHideInAll", false));
        }
        
        if(locales != null && locales.size() > 0)
            dc.add(Restrictions.in("locale", locales));
        
        if(status != null && status.size() > 0)
            dc.add(Restrictions.in("postAlias.postStatus", status));
        
        if(withLook != null && circleTypeId != null) {
            if(withLook)
                dc.add(Restrictions.isNotNull("postAlias.extLookUrl"));
            else
                dc.add(Restrictions.isNull("postAlias.extLookUrl"));
        }
        
        LinkedHashMap<String, Boolean> replace = new LinkedHashMap<String, Boolean>();
        replace.put("postAlias.promoteScore", false);
        replace.put("createdTime", false);
        blockLimit.setOrderBy(replace);
        
        PageResult<PostNew> tmpResults = blockQueryWithoutSize(dc, blockLimit);
        result.setTotalSize(tmpResults.getTotalSize());
        for(PostNew p : tmpResults.getResults()) {
            result.add(p.getPost());
        }
        return result;
    }

    @Override
    public Integer findNewPostView(Long circleTypeId, List<String> locales, List<PostStatus> status, List<Long> result, Boolean withLook, BlockLimit blockLimit, boolean disableCache) {
        if(result == null)
            return null;
        
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", false));
        dc.createAlias("post", "postAlias");
        dc.add(Restrictions.eq("postAlias.isDeleted", false));
        if(circleTypeId != null)
            dc.add(Restrictions.eq("circleTypeId", circleTypeId));
        else {
            dc.add(Restrictions.eq("mainType", true));
            dc.add(Restrictions.eq("forceHideInAll", false));
        }
        
        if(locales != null && locales.size() > 0)
            dc.add(Restrictions.in("locale", locales));
        
        if(status != null && status.size() > 0)
            dc.add(Restrictions.in("postAlias.postStatus", status));

        if(withLook != null && circleTypeId != null) {
            if(withLook)
                dc.add(Restrictions.isNotNull("postAlias.extLookUrl"));
            else
                dc.add(Restrictions.isNull("postAlias.extLookUrl"));
        }
        LinkedHashMap<String, Boolean> replace = new LinkedHashMap<String, Boolean>();
        replace.put("postAlias.promoteScore", false);
        replace.put("createdTime", false);
        blockLimit.setOrderBy(replace);
        dc.setProjection(Projections.property("post.id"));
        PageResult<Long> tmpResult = new PageResult<Long>();
        if (disableCache) {
            tmpResult = blockQueryWithoutSize(dc, blockLimit);
        } else {                
            tmpResult = blockQueryWithoutSize(dc, blockLimit, regionOfFindNewPostView);
        }
        result.addAll(tmpResult.getResults());
        return tmpResult.getTotalSize();
    }
    
    @Override
    public Integer findNewPostViewByLook(Long lookTypeId, String locale, PostType postType, List<PostStatus> status, List<Long> result, BlockLimit blockLimit) {
        if(result == null)
            return null;
        
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", false));
        dc.createAlias("post", "postAlias");
        dc.add(Restrictions.eq("postAlias.isDeleted", false));
        if(lookTypeId != null)
            dc.add(Restrictions.eq("lookTypeId", lookTypeId));
        
        dc.add(Restrictions.eq("mainType", true));
        dc.add(Restrictions.eq("forceHideInAll", false));
        
        if(locale != null)
            dc.add(Restrictions.eq("locale", locale));
        
        if(status != null && status.size() > 0)
            dc.add(Restrictions.in("postAlias.postStatus", status));
        
        if(postType != null)
            dc.add(Restrictions.eq("postAlias.postType", postType));
        
        dc.add(Restrictions.isNotNull("postAlias.extLookUrl"));
        LinkedHashMap<String, Boolean> replace = new LinkedHashMap<String, Boolean>();
        replace.put("postAlias.promoteScore", false);
        replace.put("createdTime", false);
        blockLimit.setOrderBy(replace);
        dc.setProjection(Projections.property("post.id"));
        PageResult<Long> tmpResult = blockQueryWithoutSize(dc, blockLimit, regionOfFindNewPostByLook);
        result.addAll(tmpResult.getResults());
        return tmpResult.getTotalSize();
    }
    
    @Override
    public List<PostNew> getPostNewByPosts(List<Post> posts, Boolean isDeleted) {
        if(posts == null || posts.size() <= 0)
            return new ArrayList<PostNew>();
        
        DetachedCriteria dc = createDetachedCriteria();
        if(isDeleted != null)
            dc.add(Restrictions.eq("isDeleted", isDeleted));
        dc.createAlias("post", "postAlias");
        dc.add(Restrictions.eq("postAlias.isDeleted", false));
        dc.add(Restrictions.in("post", posts));
        return findByCriteria(dc);
    }
    
    @Override
    public PostNew getLastModifiedRecord() {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", false));
        BlockLimit blockLimit = new BlockLimit(0, 1);
        blockLimit.addOrderBy("lastModified", false);
        PageResult<PostNew> results = blockQuery(dc, blockLimit);
        if(results.getResults().size() <= 0)
            return null;
        return results.getResults().get(0);
    }
    
    @Override
    public PageResult<PostNew> getByPostCreatedDate(Date startTime, Date endTime, List<Long> circleTypeIds, Long minBonus, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", false));
        dc.createAlias("post", "postAlias");
        dc.add(Restrictions.eq("postAlias.isDeleted", false));
        if(startTime != null)
            dc.add(Restrictions.ge("postAlias.createdTime", startTime));
        if(endTime != null)
            dc.add(Restrictions.le("postAlias.createdTime", endTime));
        if(minBonus != null)
            dc.add(Restrictions.ge("bonus", minBonus));
        if(circleTypeIds != null && circleTypeIds.size() > 0)
            dc.add(Restrictions.in("circleTypeId", circleTypeIds));
        return blockQuery(dc, blockLimit);
    }
    
    @Override
    public Boolean batchCreate(List<PostNew> list) {
        if(list == null || list.size() <= 0)
            return true;
        Session session = getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        int i = 0;
        for (PostNew toCreate : list) {
            session.save(toCreate);
            i++;
            if ( i % 50 == 0 ) {
                session.flush();
                session.clear();
            }       
            if (i % 200 == 0) {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    return false;
                }                           
            }
        }       
        tx.commit();
        session.close();
        return true;
    }
    
	@Override
	public int batchCheck(List<Long> postIds, Boolean isDeleted) {
        String updatePostSqlCmd = "UPDATE BC_POST_NEW SET IS_DELETED = :isDeleted WHERE POST_ID IN ( :postIds )";
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(updatePostSqlCmd);
        sqlPostsQuery.setParameter("isDeleted", isDeleted);
        sqlPostsQuery.setParameterList("postIds", postIds);
        return sqlPostsQuery.executeUpdate();
	}
    
    @Override
    public void doWithAllTrendPost(String locale, Long circleTypeId, Date begin, Date end, ScrollableResultsCallback callback) {
        if(callback == null)
            return;
        
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        if(locale != null)
            dc.add(Restrictions.eq("locale", locale));
        
        if(circleTypeId != null)
            dc.add(Restrictions.eq("circleTypeId", circleTypeId));
        
        if(begin != null)
            dc.add(Restrictions.ge("createdTime", begin));
        
        if(end != null)
            dc.add(Restrictions.lt("createdTime", end));

        dc.add(Restrictions.eq("mainType", true));
        dc.setProjection(Projections.projectionList()
                .add(Projections.property("post.id"))
                .add(Projections.property("locale"))
                .add(Projections.property("circleTypeId"))
                .add(Projections.property("bonus"))
                .add(Projections.property("forceHideInAll"))
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
    public List<PostNew> findByPost(Long postId, Boolean isDeleted) {
    	DetachedCriteria dc = createDetachedCriteria();
    	if(isDeleted != null)
            dc.add(Restrictions.eq("isDeleted", isDeleted));
    	dc.createAlias("post", "postAlias");
    	dc.add(Restrictions.eq("postAlias.id", postId));
    	return findByCriteria(dc);
    }
    
    @Override
    public void getLikeCirInCountPerPost(PostAttrType attrType, Date startTime, Date endTime, ScrollableResultsCallback callback) {
        if(callback == null)
            return;
        
        String querySql = "SELECT BC_POST.ID as postId, BC_POST.LOCALE as locale, BC_POST_NEW.CIRCLE_TYPE_ID as circleTypeId, BC_POST_NEW.CREATED_TIME AS createdTime, BC_POST_ATTR.ATTR_VALUE AS count FROM BC_POST_NEW INNER JOIN BC_POST ON BC_POST.ID = BC_POST_NEW.POST_ID AND BC_POST.POST_STATUS IN ('Published',  'Review',  'Unpublished') AND BC_POST.IS_DELETED =0 LEFT OUTER JOIN BC_POST_ATTR ON BC_POST_ATTR.REF_ID = BC_POST.ID AND BC_POST_ATTR.REF_TYPE = 'Post' AND BC_POST_ATTR.ATTR_TYPE = :attrType ";
        querySql += "WHERE BC_POST_NEW.IS_DELETED = 0 ";
        
        if(startTime != null)
            querySql += "AND BC_POST_NEW.CREATED_TIME >= :startDate ";
        if(endTime != null)
            querySql += "AND BC_POST_NEW.CREATED_TIME < :endTime ";
        
        SQLQuery sqlQuery = getSession().createSQLQuery(querySql);
        if(startTime != null)
            sqlQuery.setParameter("startDate", startTime);
        if(endTime != null)
            sqlQuery.setParameter("endTime", endTime);
        sqlQuery.setParameter("attrType", attrType.toString());
        sqlQuery.addScalar("postId", new LongType());
        sqlQuery.addScalar("locale", new StringType());
        sqlQuery.addScalar("circleTypeId", new LongType());
        sqlQuery.addScalar("createdTime", new TimestampType());
        sqlQuery.addScalar("count", new IntegerType());
        final ScrollableResults sr = sqlQuery.scroll(ScrollMode.FORWARD_ONLY);
        try {
            callback.doInHibernate(sr);
        } finally {
            sr.close();
        }
    }
    
    @Override
    public void getGenPerPost(String locale, Boolean promoted, Date startTime, Date endTime, ScrollableResultsCallback callback) {

        if(callback == null)
            return;
        
        String querySql = "SELECT BC_POST.ID as postId, BC_POST_NEW.CREATED_TIME as created, BC_POST.PROMOTE_SCORE as promoteScore FROM BC_POST_NEW INNER JOIN BC_POST ON BC_POST.ID = BC_POST_NEW.POST_ID AND BC_POST.IS_DELETED = 0 AND BC_POST.LOCALE = :locale INNER JOIN BC_CIRCLE ON BC_CIRCLE.ID = BC_POST.CIRCLE_ID AND BC_CIRCLE.IS_SECRET = 0 ";
        querySql += "WHERE BC_POST_NEW.IS_DELETED = 0 AND BC_POST_NEW.FORCE_HIDE_IN_ALL = 0 AND BC_POST_NEW.MAIN_TYPE = 1 ";
        
        if(startTime != null)
            querySql += "AND BC_POST_NEW.CREATED_TIME >= :startDate ";
        if(endTime != null)
            querySql += "AND BC_POST_NEW.CREATED_TIME < :endTime ";
        
        if(promoted)
            querySql += "AND BC_POST.PROMOTE_SCORE IS NOT NULL ";
        
        SQLQuery sqlQuery = getSession().createSQLQuery(querySql);
        if(startTime != null)
            sqlQuery.setParameter("startDate", startTime);
        if(endTime != null)
            sqlQuery.setParameter("endTime", endTime);
        sqlQuery.setParameter("locale", locale.toString());
        sqlQuery.addScalar("postId", new LongType());
        sqlQuery.addScalar("created", new TimestampType());
        sqlQuery.addScalar("promoteScore", new LongType());
        final ScrollableResults sr = sqlQuery.scroll(ScrollMode.FORWARD_ONLY);
        try {
            callback.doInHibernate(sr);
        } finally {
            sr.close();
        }
    }
}
