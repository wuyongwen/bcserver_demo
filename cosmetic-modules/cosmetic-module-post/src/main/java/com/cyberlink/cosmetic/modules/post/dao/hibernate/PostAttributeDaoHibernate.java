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
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.dao.hibernate.ScrollableResultsCallback;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.dao.PostAttributeDao;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute.PostAttrType;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;

public class PostAttributeDaoHibernate extends AbstractDaoCosmetic<PostAttribute, Long>
    implements PostAttributeDao {

    @Override
    public Map<Long, Long> checkPostAttriButeByIds(String refType, PostAttrType attrType, Long... refIds) {
        Map<Long, Long> result = new HashMap<Long, Long>();
        if(refIds.length <= 0)
            return result;
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.eq("refType", refType));
        d.add(Restrictions.in("refId", refIds));
        d.add(Restrictions.eq("attrType", attrType));
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        List<PostAttribute> attrs = findByCriteria(d);
        if(attrs == null)
            return result;
        for (PostAttribute attr : attrs) {
            result.put(attr.getRefId(), attr.getAttrValue());
        }
        return result;
    }
    
    @Override
    public Map<Long, Map<PostAttrType, Long>> listPostAttriButeByIds(String refType, Long... refIds) {
        Map<Long, Map<PostAttrType, Long>> result = new HashMap<Long, Map<PostAttrType, Long>>();
        if(refIds.length <= 0)
            return result;
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.eq("refType", refType));
        d.add(Restrictions.in("refId", refIds));
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        List<PostAttribute> attrs = findByCriteria(d);
        if(attrs == null)
            return result;
        for (PostAttribute attr : attrs) {
            if(!result.containsKey(attr.getRefId())) {
                Map<PostAttrType, Long> attrMap = new HashMap<PostAttrType, Long>();
                result.put(attr.getRefId(), attrMap);
            }
            result.get(attr.getRefId()).put(attr.getAttrType(), attr.getAttrValue());
        }
        return result;
    }
    
    @Override
    public PostAttribute findByTarget(String refType, Long refId, PostAttrType attrType) {
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.eq("refType", refType));
        d.add(Restrictions.eq("refId", refId));
        d.add(Restrictions.eq("attrType", attrType));
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        List<PostAttribute> results = findByCriteria(d);
        if(results == null || results.size() <= 0)
            return null;
        return results.get(0);
    }

    public Map<Long, Long> getLikeCountByUserIds(List<Long> userIds) {
        Map<Long, Long> resultMap = new HashMap<Long, Long>();
        DetachedCriteria dc = createDetachedCriteria();
        dc.createAlias("post", "p");
        dc.add(Restrictions.eq("refType", "Post"));
        dc.add(Restrictions.eq("attrType", PostAttrType.PostLikeCount));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("p.isDeleted", Boolean.FALSE));
        dc.add(Restrictions.in("p.creatorId", userIds));
        dc.setProjection(Projections.projectionList()
                .add(Projections.groupProperty("p.creatorId"))
                .add(Projections.sum("attrValue")));
        List<Object> objs = findByCriteria(dc);
        for (Object obj : objs) {
            Object[] row = (Object[]) obj;
            resultMap.put((Long) row[0], (Long) row[1]);
        }
        return resultMap;
    }

    public Map<Long, Long> getPromoteByUserIds(List<Long> userIds) {
        Map<Long, Long> resultMap = new HashMap<Long, Long>();
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("refType", "User"));
        dc.add(Restrictions.eq("attrType", PostAttrType.PromoteScore));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.in("refId", userIds));
        dc.setProjection(Projections.projectionList()
                .add(Projections.groupProperty("refId"))
                .add(Projections.sum("attrValue")));
        List<Object> objs = findByCriteria(dc);
        for (Object obj : objs) {
            Object[] row = (Object[]) obj;
            resultMap.put((Long) row[0], (Long) row[1]);
        }
        return resultMap;
    }
    
    @Override
    public List<Long> listUserIdsByPromote(List<Long> userIds) {
    	DetachedCriteria dc = createDetachedCriteria();
    	dc.add(Restrictions.eq("refType", "User"));
    	dc.add(Restrictions.eq("attrType", PostAttrType.PromoteScore));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.ne("attrValue", (long)0));
        dc.add(Restrictions.in("refId", userIds));
        dc.addOrder(Order.desc("attrValue"));
        dc.setProjection(Projections.property("refId"));
    	return findByCriteria(dc);
    }
    
    @Override
    public List<Long> listUserIdsByPostCount(List<Long> userIds) {
    	DetachedCriteria dc = createDetachedCriteria();
    	dc.add(Restrictions.eq("refType", "User"));
    	dc.add(Restrictions.eq("attrType", PostAttrType.PostTotalCount));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.ne("attrValue", (long)0));
        dc.add(Restrictions.in("refId", userIds));
        dc.addOrder(Order.desc("attrValue"));
        dc.setProjection(Projections.property("refId"));
    	return findByCriteria(dc);
    }
    
    @Override
	public PageResult<User> getTopLikedUserByUserType(List<UserType> userType, List<String> locale, Long offset, Long limit) {
        PageResult<User> page = new PageResult<User>();
        DetachedCriteria dc = createDetachedCriteria("pt");
        dc.createAlias("post", "p");
        dc.createAlias("p.creator", "u");

        dc.add(Restrictions.eq("refType", "Post"));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("p.isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("u.isDeleted", Boolean.FALSE));
        dc.add(Restrictions.in("u.userType", userType));
        dc.add(Restrictions.in("u.region", locale));
        dc.setProjection(Projections.projectionList()
                .add(Projections.groupProperty("u.id"))
                .add(Projections.sum("pt.attrValue"), "sum")
                .add(Projections.groupProperty("p.creator")));
        dc.addOrder(Order.desc("sum"));
        
        List<User> list = new ArrayList<User>();
        List<Object> objs = findByCriteria(dc);
        page.setTotalSize(objs.size());
        if (offset >= objs.size()) {
        	page.setResults(list);
        } else {
        	objs = objs.subList(offset.intValue(), Math.min(offset.intValue()+limit.intValue(), objs.size()));
        	for (Object obj : objs) {
        		Object[] row = (Object[]) obj;
        		list.add((User) row[2]);
        	}
            page.setResults(list);
        }
        return page;
	}

	@Override
	public PostAttribute getPromoteScoreByUserId(Long userId) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("refType", "User"));
        dc.add(Restrictions.eq("attrType", PostAttrType.PromoteScore));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("refId", userId));
        return uniqueResult(dc);
	}
	
	@Override
    public int updatePostLikeCount() {
        String updateSqlCmd = "INSERT BC_POST_ATTR (REF_TYPE, REF_ID, ATTR_TYPE, ATTR_VALUE, IS_DELETED, CREATED_TIME, LAST_MODIFIED, OBJ_VERSION) "
                              + "SELECT 'Post', REF_ID, 'PostLikeCount', COUNT(*), 0, NOW(), CURRENT_TIMESTAMP, 0 FROM BC_LIKE "
                              + "WHERE REF_TYPE = 'Post' AND IS_DELETED = 0 AND REF_ID IN ( "
                              + "SELECT REF_ID FROM BC_LIKE WHERE LAST_MODIFIED > NOW() - INTERVAL 6 HOUR AND REF_TYPE = 'Post' GROUP BY REF_ID ) "
                              + "GROUP BY REF_ID ON DUPLICATE KEY UPDATE "
                              + "ATTR_VALUE = VALUES(ATTR_VALUE)";
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(updateSqlCmd);
        return sqlPostsQuery.executeUpdate();
    }
	
	@Override
    public int updatePostCommentCount() {
        String updateSqlCmd = "INSERT BC_POST_ATTR (REF_TYPE, REF_ID, ATTR_TYPE, ATTR_VALUE, IS_DELETED, CREATED_TIME, LAST_MODIFIED, OBJ_VERSION) "
                              + "SELECT 'Post', REF_ID, 'PostCommentCount', COUNT(*), 0, NOW(), CURRENT_TIMESTAMP, 0 FROM BC_COMMENT "
                              + "WHERE REF_TYPE = 'Post' AND IS_DELETED = 0 AND COMMENT_STATUS = 'Published' AND REF_ID IN (SELECT REF_ID FROM BC_COMMENT WHERE LAST_MODIFIED > NOW() - INTERVAL 6 HOUR "
                              + "AND REF_TYPE = 'Post' GROUP BY REF_ID ) "
                              + "GROUP BY REF_ID ON DUPLICATE KEY UPDATE "
                              + "ATTR_VALUE = VALUES(ATTR_VALUE)";
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(updateSqlCmd);
        return sqlPostsQuery.executeUpdate();
    }
	
	@Override
    public int updateCommentLikeCount() {
        String updateSqlCmd = "INSERT BC_POST_ATTR (REF_TYPE, REF_ID, ATTR_TYPE, ATTR_VALUE, IS_DELETED, CREATED_TIME, LAST_MODIFIED, OBJ_VERSION) "
                              + "SELECT 'Comment', REF_ID, 'CommentLikeCount', COUNT(*), 0, NOW(), CURRENT_TIMESTAMP, 0 FROM BC_LIKE "
                              + "WHERE REF_TYPE = 'Comment' AND IS_DELETED = 0 AND REF_ID IN (SELECT REF_ID FROM BC_LIKE WHERE LAST_MODIFIED > NOW() - INTERVAL 6 HOUR "
                              + "AND REF_TYPE = 'Comment' GROUP BY REF_ID) "
                              + "GROUP BY REF_ID ON DUPLICATE KEY UPDATE "
                              + "ATTR_VALUE = VALUES(ATTR_VALUE)";
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(updateSqlCmd);
        return sqlPostsQuery.executeUpdate();
    }
	
	@Override
	public Map<Long, Map<String, Object>> getAllPostAttributeAfter(Date beginDate, Date endDate) {
	    String updateSqlCmd = "SELECT * FROM BC_POST_ATTR WHERE REF_TYPE = 'Post' AND REF_ID IN ( "
	                          + "SELECT REF_ID FROM BC_POST_ATTR WHERE LAST_MODIFIED >= :beginDate AND LAST_MODIFIED <= :endDate AND REF_TYPE = 'Post' GROUP BY REF_ID)";
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(updateSqlCmd);
        sqlPostsQuery.setParameter("beginDate", beginDate);
        sqlPostsQuery.setParameter("endDate", endDate);
        sqlPostsQuery.addEntity("PostAttribute", PostAttribute.class);
        List<PostAttribute> result = sqlPostsQuery.list();
        if(result == null)
            return null;
        Map<Long, Map<String, Object>> attrMap = new HashMap<Long, Map<String, Object>>();
        for(PostAttribute postAttr : result) {
            Long refId = postAttr.getRefId();
            if(!attrMap.containsKey(refId)) {
                attrMap.put(refId, new HashMap<String, Object>());
                attrMap.get(refId).put("Post", postAttr.getPost());
            }
            attrMap.get(refId).put(postAttr.getAttrType().toString(), postAttr.getAttrValue());
        }
        return attrMap;
	}
	

	@Override
    public PostAttribute createOrUpdatePostAttr(String refType, Long refId, PostAttrType attrType, Long value, Boolean createIfNotExist, Boolean updateOnly) {
        //String region = Constants.getPostRegion();
    	PostAttribute postAttr = findByTarget(refType, refId, attrType);
        if(postAttr != null) {
        	if (updateOnly) {
        		postAttr.setAttrValue(value);
        		return update(postAttr);
        	}
        	Long newValue = postAttr.getAttrValue();
        	newValue += value;
        	postAttr.setAttrValue(newValue);
        	return update(postAttr);
        }
        else if(createIfNotExist){
        	postAttr = new PostAttribute();
        	postAttr.setRefType(refType);
        	postAttr.setRefId(refId);
        	postAttr.setAttrType(attrType);
        	if (value >= 0)
        		postAttr.setAttrValue(value);
        	return create(postAttr);
        }
        return null;
    }

	@Override
	public void doOnTopLikedPostAfter(Date beginDate, BlockLimit blockLimit, ScrollableResultsCallback callback) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.ge("lastModified", beginDate));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("refType", "Post"));
        dc.add(Restrictions.eq("attrType", PostAttrType.PostLikeCount));
        dc.setProjection(Projections.projectionList()
                .add(Projections.property("refId"))
                .add(Projections.property("attrValue"))
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
	
	@Override
    public Long createOrUpdateAttrValue(String refType, Long refId, PostAttrType attrType, Integer diff) {
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("refType", refType));
        dc.add(Restrictions.eq("refId", refId));
        dc.add(Restrictions.eq("attrType", attrType));
        PostAttribute exPostAttr = uniqueResult(dc);
        if(exPostAttr == null) {
            exPostAttr = new PostAttribute();
            exPostAttr.setRefType(refType);
            exPostAttr.setRefId(refId);
            exPostAttr.setAttrType(attrType);
            exPostAttr.setAttrValue(0L);
            Session session = null;
            try {
                session = getSessionFactory().openSession();
                Transaction tx = session.beginTransaction();
                session.save(exPostAttr);
                tx.commit();
            }
            catch (Exception e) {
                logger.error(e.getMessage());
            }
            finally {
                if(session != null) {
                    try {
                        session.close();
                    }
                    catch(Exception e) {
                    }
                }
            }
        }
            
        String updateSqlCmd;
        if(diff > 0)
            updateSqlCmd = "UPDATE BC_POST_ATTR SET ATTR_VALUE = ATTR_VALUE + :diff WHERE REF_TYPE = :refType AND REF_ID = :refId AND ATTR_TYPE = :attrType";
        else
            updateSqlCmd = "UPDATE BC_POST_ATTR SET ATTR_VALUE = CASE WHEN ATTR_VALUE > 0 THEN ATTR_VALUE :diff ELSE 0 END WHERE REF_TYPE = :refType AND REF_ID = :refId AND ATTR_TYPE = :attrType";
        SQLQuery sqlQuery = getSession().createSQLQuery(updateSqlCmd);
        sqlQuery.setParameter("diff", diff);
        sqlQuery.setParameter("refType", refType);
        sqlQuery.setParameter("refId", refId);
        sqlQuery.setParameter("attrType", attrType.toString());
        try {
            sqlQuery.executeUpdate();
        }
        catch(Exception e) {
            return null;
        }
        
        if(exPostAttr.getAttrValue() == null)
            return null;
        Long curValue = exPostAttr.getAttrValue() + Long.valueOf(diff);
        if(curValue < 0)
            curValue = 0L;
        return curValue;
    }
	
    @Override
    public void getLikeCirInCountPerUser(PostAttrType attrType, Date startDate, Date endTime, ScrollableResultsCallback callback) {
        if(attrType == null || callback == null)
            return;
        
        String querySql = "SELECT BC_POST.USER_ID as userId, BC_USER.REGION as region, BC_USER.CREATED_TIME as createdTime, SUM(BC_POST_ATTR.ATTR_VALUE) as count FROM BC_POST_ATTR ";
        querySql += "INNER JOIN BC_POST ON BC_POST.ID = BC_POST_ATTR.REF_ID ";
        querySql += "INNER JOIN BC_USER ON BC_USER.ID = BC_POST.USER_ID ";
        querySql += "WHERE BC_POST.POST_STATUS IN ('Published', 'Review', 'Unpublished') ";
        querySql += "AND BC_POST.CREATED_TIME >= '2016-01-01 00:00:00' ";
        querySql += "AND BC_POST.IS_DELETED = 0 ";
        querySql += "AND BC_POST_ATTR.REF_TYPE = 'Post' ";
        querySql += "AND BC_POST_ATTR.ATTR_TYPE = :attrType ";
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
        sqlQuery.setParameter("attrType", attrType.toString());
        sqlQuery.addScalar("userId", new LongType());
        sqlQuery.addScalar("region", new StringType());
        sqlQuery.addScalar("createdTime", new TimestampType());
        sqlQuery.addScalar("count", new IntegerType());
        final ScrollableResults sr = sqlQuery.scroll(ScrollMode.FORWARD_ONLY);
        try {
            callback.doInHibernate(sr);
        } finally {
            sr.close();
        }
    }
}
