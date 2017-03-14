package com.cyberlink.cosmetic.modules.post.dao.hibernate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.dao.hibernate.ScrollableResultsCallback;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.model.AppName;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.model.PostType;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute.PostAttrType;
import com.cyberlink.cosmetic.modules.user.model.UserType;

public class PostDaoHibernate extends AbstractDaoCosmetic<Post, Long>
    implements PostDao {
    private String regionOfFindPostByUsers = "com.cyberlink.cosmetic.modules.post.model.Post.query.findPostByUsers";
    private String regionOfFindPostIdByUsers = "com.cyberlink.cosmetic.modules.post.model.Post.query.findPostIdByUsers";
	private String regionOfFindSubPostByPost = "com.cyberlink.cosmetic.modules.post.model.Post.query.findSubPostByPost";
	private String regionOfFindLookPostByUsers = "com.cyberlink.cosmetic.modules.post.model.Post.query.findLookPostByUsers";

    @Override
    public Post existAndNonDeleted(Long postId)
    {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("id", postId));
        return uniqueResult(dc);
    }
	
    @Override
    public PageResult<Post> findPostByUsers(List<Long> creatorIds, List<PostStatus> postStatus, Boolean withSecret, Boolean isDeleted, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        if(isDeleted != null)
            dc.add(Restrictions.eq("isDeleted", isDeleted));
        dc.add(Restrictions.isNull("parentId"));
        if(postStatus != null && postStatus.size() > 0)
            dc.add(Restrictions.in("postStatus", postStatus));
        dc.add(Restrictions.in("creatorId", creatorIds));
        if(withSecret != null && !withSecret) {
            dc.createAlias("circle", "circle", JoinType.LEFT_OUTER_JOIN);
            dc.add(Restrictions.or(Restrictions.isNull("circle.isSecret"), Restrictions.eq("circle.isSecret", withSecret)));
        }
        return blockQuery(dc, blockLimit);//, regionOfFindPostByUsers);
    }

    @Override
    public Integer findPostIdsByUsers(List<Long> creatorIds, List<PostStatus> postStatus, Boolean withSecret, Boolean withSize, List<Long> result, BlockLimit blockLimit) {
        if(result == null)
            return null;
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.isNull("parentId"));
        dc.add(Restrictions.in("postStatus", postStatus));
        dc.add(Restrictions.in("creatorId", creatorIds));
        if(withSecret != null && !withSecret) {
            dc.createAlias("circle", "circle", JoinType.LEFT_OUTER_JOIN);
            dc.add(Restrictions.or(Restrictions.isNull("circle.isSecret"), Restrictions.eq("circle.isSecret", withSecret)));
        }
        dc.setProjection(Projections.property("id"));
        PageResult<Long> tmpResult;
        if(withSize)
            tmpResult = blockQuery(dc, blockLimit);
        else
            tmpResult = blockQueryWithoutSize(dc, blockLimit);
        result.addAll(tmpResult.getResults());
        return tmpResult.getTotalSize();
    }
    
    @Override
    public Integer findLookPostIdsByUser(Long userId, PostType postType, List<PostStatus> postStatus, Boolean withSecret, Boolean withSize, List<Long> result, BlockLimit blockLimit) {
        if(result == null)
            return null;
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.isNull("parentId"));
        dc.add(Restrictions.in("postStatus", postStatus));
        if(postType != null)
            dc.add(Restrictions.eq("postType", postType));
        if(userId != null)
            dc.add(Restrictions.eq("creatorId", userId));
        
        dc.add(Restrictions.isNotNull("extLookUrl"));
        if(withSecret != null && !withSecret) {
            dc.createAlias("circle", "circle", JoinType.LEFT_OUTER_JOIN);
            dc.add(Restrictions.or(Restrictions.isNull("circle.isSecret"), Restrictions.eq("circle.isSecret", withSecret)));
        }
        dc.setProjection(Projections.property("id"));
        PageResult<Long> tmpResult;
        if(withSize)
            tmpResult = blockQuery(dc, blockLimit);
        else
            tmpResult = blockQueryWithoutSize(dc, blockLimit);
        result.addAll(tmpResult.getResults());
        return tmpResult.getTotalSize();
    }
    
    @Override
    public Integer findLookPostIdsByUsers(List<Long> creatorIds, PostType postType, List<PostStatus> postStatus, List<Long> result, BlockLimit blockLimit) {
        if(result == null)
            return null;
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.isNull("parentId"));
        dc.add(Restrictions.in("postStatus", postStatus));
        if(postType != null)
            dc.add(Restrictions.eq("postType", postType));
        if(creatorIds != null && creatorIds.size() > 0)
            dc.add(Restrictions.in("creatorId", creatorIds));
        
        dc.add(Restrictions.isNotNull("extLookUrl"));
        dc.createAlias("circle", "circle", JoinType.LEFT_OUTER_JOIN);
        dc.add(Restrictions.or(Restrictions.isNull("circle.isSecret"), Restrictions.eq("circle.isSecret", false)));
        dc.setProjection(Projections.property("id"));
        PageResult<Long> tmpResult = blockQuery(dc, blockLimit, regionOfFindLookPostByUsers);
        result.addAll(tmpResult.getResults());
        return tmpResult.getTotalSize();
    }
    
    @Override
    public Map<Long, AppName> findLookPostSource(List<Long> postIds) {
        Map<Long, AppName> result = new HashMap<Long, AppName>();
        if(postIds == null || postIds.size() <= 0)
            return result;

        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.isNull("parentId"));        
        dc.add(Restrictions.or(Restrictions.isNotNull("extLookUrl"), Restrictions.isNotNull("lookTypeId")));
        dc.add(Restrictions.in("id", postIds));
        dc.setProjection(Projections.projectionList()
                .add(Projections.property("id"))
                .add(Projections.property("appName")));
        List<Object> objs = findByCriteria(dc);
        if(objs == null || objs.size() <= 0)
            return result;
        
        for(Object obj : objs) {
            Object[] row = (Object[]) obj;
            result.put((Long)row[0], (AppName)row[1]);
        }
        return result;
    }
    
    @Override
    public void doWithPotentialDiscoverPosts(Date startDate, Date endTime, Long minBasicBonus, BlockLimit blockLimit, ScrollableResultsCallback callback) {
        if(callback == null)
            return;
        
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.isNull("parentId"));
        dc.add(Restrictions.or(Restrictions.isNull("postSource"), Restrictions.ne("postSource", "circle_in_posting")));
        dc.add(Restrictions.or(Restrictions.isNull("basicSortBonus"), Restrictions.lt("basicSortBonus", minBasicBonus)));
        
        dc.add(Restrictions.eq("postStatus", PostStatus.Published));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));

        if(startDate != null)
            dc.add(Restrictions.ge("createdTime", startDate));
        if(endTime != null)
            dc.add(Restrictions.le("createdTime", endTime));

        if(blockLimit != null) {
            for(String ord : blockLimit.getOrderBy().keySet()) {
                Boolean asc = blockLimit.getOrderBy().get(ord);
                if(asc)
                    dc.addOrder(Order.asc(ord));
                else
                    dc.addOrder(Order.desc(ord));
            }
        }
        else {
            dc.addOrder(Order.asc("createdTime"));
        }
        final Criteria c  = dc.getExecutableCriteria(getSession());
        if(blockLimit != null) {
            c.setFirstResult(blockLimit.getOffset());
            c.setMaxResults(blockLimit.getSize());
        }

        final ScrollableResults sr = c.scroll(ScrollMode.FORWARD_ONLY);
        try {
            callback.doInHibernate(sr);
        } finally {
            sr.close();
        }
    }
    
    @Override
    public Long findPostIdsCountByUsers(List<Long> creatorIds, List<PostStatus> postStatus, Boolean withSecret) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.isNull("parentId"));
        dc.add(Restrictions.in("postStatus", postStatus));
        dc.add(Restrictions.in("creatorId", creatorIds));
        if(withSecret != null && !withSecret) {
            dc.createAlias("circle", "circle", JoinType.LEFT_OUTER_JOIN);
            dc.add(Restrictions.or(Restrictions.isNull("circle.isSecret"), Restrictions.eq("circle.isSecret", withSecret)));
        }
        dc.setProjection(Projections.rowCount());
        return uniqueResult(dc);
    }
    
    @Override
    public PageResult<Post> findPostByCLUsers(List<Long> creatorIds, List<PostStatus> postStatus, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.isNull("parentId"));
        dc.add(Restrictions.in("postStatus", postStatus));
        dc.add(Restrictions.in("creatorId", creatorIds));
        return blockQueryWithoutSize(dc, blockLimit, regionOfFindPostByUsers);
    }

    @Override
    public Integer findPostIdsByCLUsers(List<Long> creatorIds, List<PostStatus> postStatus, List<Long> result, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.isNull("parentId"));
        dc.add(Restrictions.in("postStatus", postStatus));
        dc.add(Restrictions.in("creatorId", creatorIds));
        dc.setProjection(Projections.property("id"));
        PageResult<Long> tmpResult = blockQueryWithoutSize(dc, blockLimit, regionOfFindPostIdByUsers);
        result.addAll(tmpResult.getResults());
        return tmpResult.getTotalSize();
    }
    
    @Override
    public PageResult<Post> findPostByLocale(List<String> locales, List<PostStatus> postStatus, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.in("postStatus", postStatus));
        dc.add(Restrictions.isNull("parentId"));
        if(locales != null && locales.size() > 0)
            dc.add(Restrictions.in("locale", locales));
        return blockQuery(dc, blockLimit);
    }
    
    @Override
    public PageResult<Post> findSubPostByPost(Long postId, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("parentId", postId));
        dc.add(Restrictions.neOrIsNotNull("id", postId));
        return blockQuery(dc, blockLimit, regionOfFindSubPostByPost);
    }

    @Override
    public PageResult<Post> findAllRelatedPostByPost(Long postId, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.or(Restrictions.eq("parentId", postId), Restrictions.eq("id", postId)));
        return blockQuery(dc, blockLimit, regionOfFindSubPostByPost);
    }
    
    @Override
    public List<Long> findByCreatorOrCircleAndStatus(Set<Long> userIds, Set<Long> circleIds, List<PostStatus> postStatus) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        if(postStatus != null && postStatus.size() > 0)
            dc.add(Restrictions.in("postStatus", postStatus));
        if(userIds != null && userIds.size() > 0 && circleIds != null && circleIds.size() > 0)
            dc.add(Restrictions.or(Restrictions.in("creatorId", userIds), Restrictions.in("circleId", circleIds)));
        else if(circleIds != null && circleIds.size() > 0)
            dc.add(Restrictions.in("circleId", circleIds));
        else if(userIds != null && userIds.size() > 0)
            dc.add(Restrictions.in("creatorId", userIds));
        dc.setProjection(Projections.distinct(Projections.property("id")));        
        dc.addOrder(Order.desc("createdTime"));
        return findByCriteria(dc);
    }
    
    @Override
    public List<Long> findAllPostByDateAndCircleAndStatus(Long circleId, Date start, Date end, PostStatus status) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("postStatus", status));
        if (start != null)
            dc.add(Restrictions.ge("lastModified", start));
        if (end != null)
            dc.add(Restrictions.lt("lastModified", end));
        if(circleId != null)
            dc.add(Restrictions.eq("circleId", circleId));
        dc.addOrder(Order.desc("id"));
        dc.setProjection(Projections.property("id"));
        return findByCriteria(dc);
    }
    
    @Override
    public PageResult<Post> findByCreatorOrCircle(Set<Long> userIds, Set<Long> circleIds, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.createAlias("circle", "circle");
        
        dc.add(Restrictions.eq("postStatus", PostStatus.Published));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("circle.isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("circle.isSecret", Boolean.FALSE));

        if(userIds != null && userIds.size() > 0 && circleIds != null && circleIds.size() > 0)
            dc.add(Restrictions.or(Restrictions.in("circle.creatorId", userIds), Restrictions.in("circleId", circleIds)));
        else if(userIds == null || userIds.size() <= 0)
            dc.add(Restrictions.in("circleId", circleIds));
        else if(circleIds == null || circleIds.size() <= 0)
            dc.add(Restrictions.in("circle.creatorId", userIds));

        return blockQueryWithoutSize(dc, blockLimit);
    }
    
    @Override
    public Integer findPostViewByCreatorOrCircle(Set<Long> userIds, Set<Long> circleIds, List<PostStatus> postStatuses, List<Long> result, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.createAlias("circle", "circle");
        
        dc.add(Restrictions.in("postStatus", postStatuses));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("circle.isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("circle.isSecret", Boolean.FALSE));

        if(userIds != null && userIds.size() > 0 && circleIds != null && circleIds.size() > 0)
            dc.add(Restrictions.or(Restrictions.in("circle.creatorId", userIds), Restrictions.in("circleId", circleIds)));
        else if(userIds == null || userIds.size() <= 0)
            dc.add(Restrictions.in("circleId", circleIds));
        else if(circleIds == null || circleIds.size() <= 0)
            dc.add(Restrictions.in("circle.creatorId", userIds));

        dc.setProjection(Projections.property("id"));
        PageResult<Long> tmpResult = blockQueryWithoutSize(dc, blockLimit);
        result.addAll(tmpResult.getResults());
        return tmpResult.getTotalSize();
    }
    
    @Override
    public int bacthDeletePostByCircle(Long circleId) {
        String updatePostSqlCmd = "UPDATE BC_POST SET IS_DELETED=1 WHERE CIRCLE_ID = :circleId";
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(updatePostSqlCmd);
        sqlPostsQuery.setParameter("circleId", circleId);
        return sqlPostsQuery.executeUpdate();
    }
    
    @Override
    public PageResult<Post> findByCircle(Long circleId, List<String> locales, List<PostStatus> postStatus, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("circleId", circleId));      
        
        dc.add(Restrictions.isNull("parentId"));
        dc.add(Restrictions.in("postStatus", postStatus));
        
        return blockQuery(dc, blockLimit);//, regionOfFindByCircle);
    }
    
    @Override
    public Integer findPostViewByCircle(Long circleId, List<String> locales, List<PostStatus> postStatus, List<Long> result, BlockLimit blockLimit) {
        if(result == null)
            return null;
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("circleId", circleId));      
        
        dc.add(Restrictions.isNull("parentId"));
        dc.add(Restrictions.in("postStatus", postStatus));
        
        dc.setProjection(Projections.property("id"));
        PageResult<Long> tmpResult = blockQuery(dc, blockLimit);//, regionOfFindByCircle);
        result.addAll(tmpResult.getResults());
        return tmpResult.getTotalSize();
    }
    
	@Override
	public PageResult<Post> findAllPost(BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.isNull("parentId"));
        return blockQuery(dc, blockLimit);
	}
	
	@Override
    public PageResult<Post> findAllActivePost(String start, String end, String activeType, BlockLimit blockLimit) {
        String queryCommentedPost = "SELECT REF_ID FROM BC_COMMENT WHERE IS_DELETED=0 AND REF_TYPE='Post'";
        String queryLikedPost = "SELECT REF_ID FROM BC_LIKE WHERE IS_DELETED=0 AND REF_TYPE='Post'";
        String queryLikedCommentPost = "SELECT REF_ID FROM BC_COMMENT WHERE ID IN ( SELECT REF_ID FROM  BC_LIKE  WHERE IS_DELETED =0 AND  REF_TYPE =  'Comment' AND CREATED_TIME >=  '" + start + "' AND CREATED_TIME <=  '" + end + "' ) AND IS_DELETED =0";
        if(start != null) {
            queryCommentedPost += " AND CREATED_TIME > '" + start + "' ";
            queryLikedPost += " AND CREATED_TIME > '" + start + "' ";
        }
        if(end != null) {
            queryCommentedPost += " AND CREATED_TIME < '" + end + "' ";
            queryLikedPost += " AND CREATED_TIME < '" + end + "' ";
        }
        String finalSql = "";
        switch(activeType) {
            case "Like":
            {
                finalSql = "FROM BC_POST WHERE ID in ( " + queryLikedPost +  " UNION " + queryLikedCommentPost + ")";
                break;
            }
            case "Comment":
            {
                finalSql = "FROM BC_POST WHERE ID in ( " + queryCommentedPost +  ")";
                break;
            }
            default:
            {
                finalSql = "FROM BC_POST WHERE ID in ( " + queryCommentedPost + " UNION " + queryLikedPost + " UNION " + queryLikedCommentPost + ")";
                break;
            }
        }
        String queryActivePost = "SELECT * " + finalSql + " LIMIT "+ String.valueOf(blockLimit.getSize()) +" OFFSET " + String.valueOf(blockLimit.getOffset());
        String queryActivePostCount = "SELECT COUNT(*) " + finalSql ;
        
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(queryActivePost);
        sqlPostsQuery.addEntity("post", Post.class);
        List<Post> posts = sqlPostsQuery.list();
        
        SQLQuery sqlSizeQuery = getSession().createSQLQuery(queryActivePostCount);
        Integer size = ((Number)sqlSizeQuery.uniqueResult()).intValue();
        
        PageResult<Post> result = new PageResult<Post>();
        result.setTotalSize(size);
        result.setResults(posts);
        return result;
    }

    @Override
    public PageResult<Post> findMainPostByCreatedDateAndStatus(Date start, Date end, PostStatus status, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.isNull("parentId"));
        if(start != null)
            dc.add(Restrictions.ge("createdTime", start));
        if(end != null)
            dc.add(Restrictions.lt("createdTime", end));
        
        if (status != null) 
            dc.add(Restrictions.eq("postStatus", status));
        return blockQuery(dc, blockLimit);
    }
    
    @Override
    public PageResult<Post> findMainPostByCreatedDateAndStatus(Date start, Date end, PostStatus status, String locale, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.isNull("parentId"));
        if(start != null)
            dc.add(Restrictions.ge("createdTime", start));
        if(end != null)
            dc.add(Restrictions.lt("createdTime", end));
        
        if (status != null) 
            dc.add(Restrictions.eq("postStatus", status));
        dc.add(Restrictions.eq("locale", locale));        
        return blockQuery(dc, blockLimit);
    }

    @Override
    public PageResult<Post> findMainPostByDateAndStatus(Date start, Date end, PostStatus status, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.isNull("parentId"));
        if (start != null)
        	dc.add(Restrictions.ge("lastModified", start));
        if (end != null)        
        	dc.add(Restrictions.lt("lastModified", end));
        if (status != null) 
        	dc.add(Restrictions.eq("postStatus", status));
        return blockQuery(dc, blockLimit);
    }

    @Override
    public Map<Long, List<Post>> findSubPostByPostIds(Long... postIds) {
        Map<Long, List<Post>> result = new HashMap<Long, List<Post>>();
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        d.add(Restrictions.in("parentId", postIds));
        List<Post> posts = findByCriteria(d);
        for (Post post : posts) {
            if(!result.containsKey(post.getParentId()))
                result.put(post.getParentId(), new ArrayList<Post>());
            
            result.get(post.getParentId()).add(post);
        }
        return result;
    }

    @Override
	public Map<Long, Long> findSubPostCountByPostIds(Long... postIds) {
        Map<Long, Long> result = new HashMap<Long, Long>();
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        d.add(Restrictions.in("parentId", postIds));
        d.setProjection(Projections.projectionList()
                .add(Projections.groupProperty("parentId"))
                .add(Projections.rowCount()));
        List<Object> objs = findByCriteria(d);
        for (Object obj : objs) {
            Object[] row = (Object[]) obj;
            Long parentId = (Long) row[0];
            result.put(parentId, (Long) row[1]);
        }        
        return result;
	}
    
    @Override
	public Map<Long, String> findPostLocaleByPostIds(Long... postIds) {
        Map<Long, String> result = new HashMap<Long, String>();
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        d.add(Restrictions.in("id", postIds));
        d.setProjection(Projections.projectionList()
                .add(Projections.groupProperty("id"))
                .add(Projections.groupProperty("locale")));
        List<Object> objs = findByCriteria(d);
        for (Object obj : objs) {
            Object[] row = (Object[]) obj;
            Long parentId = (Long) row[0];
            result.put(parentId, (String) row[1]);
        }        
        return result;
	}
    
    @Override
    public List<Post> findByIds(Long... ids) {
        if (ids == null || ids.length == 0) {
            return Collections.emptyList();
        }

        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("id", ids));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));

        return findByCriteria(dc);
    }
    
	@Override
	public List<Long> findAllMainPostByDateAndStatus(Long creatorId, Date start, Date end,
			PostStatus status) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.isNull("parentId"));
        if (start != null)
        	dc.add(Restrictions.ge("lastModified", start));
        if (end != null)
        	dc.add(Restrictions.lt("lastModified", end));
        dc.add(Restrictions.eq("postStatus", status));
        dc.add(Restrictions.eq("creatorId", creatorId));
        dc.addOrder(Order.desc("id"));
        dc.setProjection(Projections.property("id"));
        return findByCriteria(dc);
	}

	@Override
	public List<Long> findAllPostIdByUsers(List<Long> creatorIds) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.isNull("parentId"));
        dc.add(Restrictions.eq("postStatus", PostStatus.Published));
        dc.add(Restrictions.in("creatorId", creatorIds));
        dc.addOrder(Order.desc("id"));
        dc.setProjection(Projections.property("id"));
        return findByCriteria(dc);
	}

	public Long countUndeleted() {
		final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.rowCount());
        return uniqueResult(dc);
	}
	
	public List<Post> findUndeleted(Integer pageIndex, Integer pageSize) {
		final DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.addOrder(Order.desc("createdTime"));
		return findByCriteria(dc, new PageLimit(pageIndex, pageSize)); 
	}
    
	@Override
	public PageResult<Post> findPostByUsersType(UserType userType, BlockLimit blockLimit) {
		DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.isNull("parentId"));
        dc.createAlias("creator", "creator");
        dc.add(Restrictions.eq("creator.userType", userType));
        return blockQuery(dc, blockLimit);
	}
	
	@Override
    public void doWithAllPost(List<UserType> userTypes, String locale, BlockLimit blockLimit, ScrollableResultsCallback callback) {
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("locale", locale));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("postStatus", PostStatus.Published));
        dc.createAlias("creator", "creator");
        dc.add(Restrictions.in("creator.userType", userTypes));
        dc.add(Restrictions.eq("creator.isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.projectionList()
                .add(Projections.property("id"))
                .add(Projections.property("creatorId"))
                .add(Projections.property("circleId"))
                .add(Projections.property("createdTime")));
        for(String ord : blockLimit.getOrderBy().keySet()) {
            Boolean asc = blockLimit.getOrderBy().get(ord);
            if(asc)
                dc.addOrder(Order.asc(ord));
            else
                dc.addOrder(Order.desc(ord));
        }
        final Criteria c  = dc.getExecutableCriteria(getSession());
        c.setFirstResult(blockLimit.getOffset());
        c.setMaxResults(blockLimit.getSize());
        final ScrollableResults sr = c.scroll(ScrollMode.FORWARD_ONLY);
        try {
            callback.doInHibernate(sr);
        } finally {
            sr.close();
        }
    }
	
	/* The bject in the list is a Object array.
	 * Object[] obj
	 * obj[0] is locale
	 * obj[1] is date
	 * obj[2] is post count by date
	 */ 
	@Override
	public List<Object> countPostByCreateTimeAndLocale(Date startTime, Date endTime, PostStatus status, String locale) {
		String sqlCmd = "SELECT BC_POST.LOCALE, DATE_FORMAT(BC_POST.CREATED_TIME + INTERVAL :GMT HOUR , '%Y-%c-%d') , COUNT( * ) AS cnt "
				+ "FROM BC_POST "
				+ "WHERE BC_POST.IS_DELETED = 0 ";
		if (locale != null)		
			sqlCmd += "AND BC_POST.LOCALE = :locale ";
		if (status != null)	
			sqlCmd += "AND BC_POST.POST_STATUS = :status ";
		if (startTime != null)
			sqlCmd += "AND BC_POST.CREATED_TIME > :startTime ";
		if (endTime != null)
			sqlCmd += "AND BC_POST.CREATED_TIME <= :endTime ";
		sqlCmd += "GROUP BY DATE_FORMAT(BC_POST.CREATED_TIME + INTERVAL :GMT HOUR, '%Y-%c-%d'), BC_POST.LOCALE "
				+ "ORDER BY BC_POST.CREATED_TIME ASC, BC_POST.TITLE DESC ";
		
		SQLQuery sqlPostsQuery = getSession().createSQLQuery(sqlCmd);
		if (locale != null)
			sqlPostsQuery.setParameter("locale", locale);
		if (status != null)	
			sqlPostsQuery.setParameter("status", status.toString());
		if (startTime != null)	
			sqlPostsQuery.setParameter("startTime", startTime);
		if (endTime != null)	
			sqlPostsQuery.setParameter("endTime", endTime);
		TimeZone localTz = Calendar.getInstance().getTimeZone();
		int gmt = (TimeZone.getTimeZone("GMT+8:00").getRawOffset() - localTz.getRawOffset()) / (60 * 60 * 1000);
		sqlPostsQuery.setParameter("GMT", gmt);
		
		return sqlPostsQuery.list();
	}
	
	@Override
    public List<Long> findMainPostIdsByCreatedDateAndStatus(Date start, Date end, PostStatus status, String locale) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.isNull("parentId"));
        if(start != null)
            dc.add(Restrictions.ge("createdTime", start));
        if(end != null)
            dc.add(Restrictions.lt("createdTime", end));
        if (status != null) 
            dc.add(Restrictions.eq("postStatus", status));
        if (locale != null) 
            dc.add(Restrictions.eq("locale", locale));
        dc.setProjection(Projections.property("id"));
        return findByCriteria(dc);
    }

	@Override
	public void publishUnpublished(Long userId) {
	    String format = "UPDATE BC_POST SET POST_STATUS = 'Published' WHERE USER_ID = :userId AND POST_STATUS = 'Unpublished' AND IS_DELETED = 0";     
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(format);
        sqlPostsQuery.setParameter("userId", userId);
        sqlPostsQuery.executeUpdate();
	}
	
    @Override
	public void bacthDeleteByPostCreator(Long userId) {
		String format = "UPDATE `BC_POST` SET IS_DELETED = '1' WHERE `USER_ID` = '%s'";
		String sql = String.format(format, userId.toString());			
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(sql);
        sqlPostsQuery.executeUpdate();
	}
    
    @Override
    public Map<Long, Long> countByUserIds(List<Long> userIds) {
    	Map<Long, Long> resultMap = new HashMap<Long, Long>();
        if(userIds == null || userIds.size() <= 0)
            return resultMap;
        
		final DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.in("creatorId", userIds));
		dc.add(Restrictions.in("postStatus", Arrays.asList(PostStatus.Published, PostStatus.Unpublished)));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        Calendar cal = Calendar.getInstance();
        dc.add(Restrictions.lt("createdTime", cal.getTime()));
		cal.add(Calendar.HOUR, -336);
        dc.add(Restrictions.ge("createdTime", cal.getTime()));       
        dc.setProjection(Projections.projectionList()
                .add(Projections.groupProperty("creatorId"))
                .add(Projections.rowCount()));
        List<Object> objs = findByCriteria(dc);
        for (Object obj : objs) {
            Object[] row = (Object[]) obj;
            resultMap.put((Long) row[0], (Long) row[1]);
        }    
        return resultMap;
	}
    
    @Override
    public void getPostCountPerUser(Date startDate, Date endTime, ScrollableResultsCallback callback) {
        if(callback == null)
            return;
        
        String querySql = "SELECT BC_POST.USER_ID as userId, BC_USER.REGION as region, BC_USER.CREATED_TIME as createdTime, COUNT(*) as postCount FROM BC_POST ";
        querySql += "INNER JOIN BC_USER ON BC_USER.ID = BC_POST.USER_ID ";
        querySql += "INNER JOIN BC_CIRCLE ON BC_CIRCLE.ID = BC_POST.CIRCLE_ID ";
        querySql += "WHERE BC_POST.POST_STATUS IN ('Published', 'Review', 'Unpublished') ";
        querySql += "AND BC_POST.IS_DELETED = 0 ";
        querySql += "AND BC_POST.CREATED_TIME >= '2016-01-01 00:00:00' ";
        querySql += "AND BC_CIRCLE.IS_SECRET = 0 ";
        if(startDate != null)
            querySql += "AND BC_USER.CREATED_TIME >= :startDate ";
        if(endTime != null)
            querySql += "AND BC_USER.CREATED_TIME < :endTime ";
        querySql += "GROUP BY BC_POST.USER_ID";
        
        SQLQuery sqlQuery = getSession().createSQLQuery(querySql);
        if(startDate != null)
            sqlQuery.setParameter("startDate", startDate);
        if(endTime != null)
            sqlQuery.setParameter("endTime", endTime);
        sqlQuery.addScalar("userId", new LongType());
        sqlQuery.addScalar("region", new StringType());
        sqlQuery.addScalar("createdTime", new TimestampType());
        sqlQuery.addScalar("postCount", new IntegerType());
        final ScrollableResults sr = sqlQuery.scroll(ScrollMode.FORWARD_ONLY);
        try {
            callback.doInHibernate(sr);
        } finally {
            sr.close();
        }
    }
    
}
