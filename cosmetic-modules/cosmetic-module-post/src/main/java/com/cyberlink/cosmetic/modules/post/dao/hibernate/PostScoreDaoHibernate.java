package com.cyberlink.cosmetic.modules.post.dao.hibernate;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.dao.PostScoreDao;
import com.cyberlink.cosmetic.modules.post.model.PostScore;
import com.cyberlink.cosmetic.modules.post.model.PostScore.CreatorType;
import com.cyberlink.cosmetic.modules.post.model.PostScore.PoolType;
import com.cyberlink.cosmetic.modules.post.model.PostScore.ResultType;

public class PostScoreDaoHibernate extends AbstractDaoCosmetic<PostScore, Long>
    implements PostScoreDao {

    @Override
    public PageResult<PostScore> getPostScoreBetween(Long reviewerId, String postLocale, Date begin, Date end, Boolean isHandled, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        if(reviewerId != null)
            dc.add(Restrictions.eq("reviewerId", reviewerId));
        if(postLocale != null && postLocale.length() > 0)
            dc.add(Restrictions.eq("postLocale", postLocale));
        if(begin != null)
            dc.add(Restrictions.ge("createdTime", begin));
        if(end != null)
            dc.add(Restrictions.le("createdTime", end));
        if(isHandled != null)
            dc.add(Restrictions.eq("isHandled", isHandled));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return blockQuery(dc, blockLimit);
    }
    
    @Override
    public PageResult<PostScore> getHandledPostScoreBetween(Long reviewerId, String postLocale, Date begin, Date end, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        if(reviewerId != null)
            dc.add(Restrictions.eq("reviewerId", reviewerId));
        if(postLocale != null && postLocale.length() > 0)
            dc.add(Restrictions.eq("postLocale", postLocale));
        if(begin != null)
            dc.add(Restrictions.ge("createdTime", begin));
        if(end != null)
            dc.add(Restrictions.le("createdTime", end));
        dc.add(Restrictions.isNotNull("isHandled"));
        dc.add(Restrictions.isNotNull("resultType"));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return blockQuery(dc, blockLimit);
    }
    
	@Override
	public Long getPostScoreCountBetween(Long reviewerId, String postLocale, Date begin, Date end) {
		DetachedCriteria dc = createDetachedCriteria();
		if (reviewerId != null)
			dc.add(Restrictions.eq("reviewerId", reviewerId));
		if (postLocale != null && postLocale.length() > 0)
			dc.add(Restrictions.eq("postLocale", postLocale));
		if (begin != null)
			dc.add(Restrictions.ge("createdTime", begin));
		if (end != null)
			dc.add(Restrictions.le("createdTime", end));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.setProjection(Projections.rowCount());
		return uniqueResult(dc);
	}
	
	@Override
	public Map<ResultType, Long> getHandledPostScoreCountBetween(String postLocale, List<ResultType> resultTypes, Date begin, Date end) {
		Map<ResultType, Long> summary = new HashMap<ResultType, Long>();
		DetachedCriteria dc = createDetachedCriteria();
		if (postLocale != null && postLocale.length() > 0)
			dc.add(Restrictions.eq("postLocale", postLocale));
		if (resultTypes != null && resultTypes.size() > 0)
			dc.add(Restrictions.in("resultType", resultTypes));
		if (begin != null)
			dc.add(Restrictions.ge("createdTime", begin));
		if (end != null)
			dc.add(Restrictions.le("createdTime", end));
		dc.add(Restrictions.isNotNull("reviewerId"));
		dc.add(Restrictions.isNotNull("isHandled"));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.setProjection(Projections.projectionList()
				.add(Projections.groupProperty("resultType"))
				.add(Projections.rowCount(), "count"));

		List<Object> objs = findByCriteria(dc);
		for (Object obj : objs) {
			Object[] row = (Object[]) obj;
			summary.put((ResultType) row[0], (Long) row[1]);
		}
		return summary;
	}
    
    @Override
    public PostScore getLastHandledRecord(String postLocale, PoolType poolType, List<ResultType> resultTypes) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        if(postLocale != null && postLocale.length() > 0)
            dc.add(Restrictions.eq("postLocale", postLocale));
        if(poolType != null)
            dc.add(Restrictions.eq("poolType", poolType));
        
        if(resultTypes != null && resultTypes.size() > 0)
            dc.add(Restrictions.in("resultType", resultTypes));
        
        dc.add(Restrictions.isNotNull("reviewerId"));
        dc.add(Restrictions.isNotNull("isHandled"));
        BlockLimit blockLimit = new BlockLimit(0, 1);
        blockLimit.addOrderBy("lastModified", false);
        PageResult<PostScore> results = blockQuery(dc, blockLimit);
        if(results.getResults().size() <= 0)
            return null;
        else
            return results.getResults().get(0);
    }

    @Override
    public Integer markToHandle(Long reviewerId, ResultType resultType, List<Long> postIds) {
        if(postIds == null || postIds.size() <= 0)
            return 0;
        
        String updatePostSqlCmd = "UPDATE BC_POST_SCORE SET REVIEWER_ID=:reviewerId, ";
        if(resultType != null)
            updatePostSqlCmd += "RESULT_TYPE = :resultType, ";
        updatePostSqlCmd += "IS_HANDLED=0  WHERE POST_ID IN :postIds AND IS_DELETED=0 AND REVIEWER_ID IS NULL ";
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(updatePostSqlCmd);
        sqlPostsQuery.setParameter("reviewerId", reviewerId);
        if(resultType != null)
            sqlPostsQuery.setParameter("resultType", resultType.toString());
        sqlPostsQuery.setParameterList("postIds", postIds);
        return sqlPostsQuery.executeUpdate();
    }
    
    @Override
    public Integer removeRepeatId(List<Long> postIds) {
        if(postIds == null || postIds.size() <= 0)
            return 0;
        
        String updatePostSqlCmd = "UPDATE BC_POST_SCORE SET IS_DELETED=1 WHERE POST_ID IN :postIds";
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(updatePostSqlCmd);
        sqlPostsQuery.setParameterList("postIds", postIds);
        return sqlPostsQuery.executeUpdate();
    }
    
    @Override
    public Boolean batchCreate(List<PostScore> list) {
        if(list == null || list.size() <= 0)
            return true;
        Session session = getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        int i = 0;
        for (PostScore toCreate : list) {
            try {
                session.save(toCreate);
                i++;
                if ( i % 50 == 0 ) {
                    session.flush();
                    session.clear();
                }       
                if (i % 200 == 0) {
                    Thread.sleep(500);                           
                }
            }
            catch (Exception e) {
                logger.debug(e.getMessage());
            }
        }       
        tx.commit();
        session.close();
        return true;
    }
    
    @Override
    public int batchDelete(Date startDate, Date endDate) {
        String criteria = " WHERE (IS_HANDLED = 1 OR IS_DELETED = 1)";
        if(startDate != null)
            criteria += " AND CREATED_TIME >= :startDate";
        if(endDate != null)
            criteria += " AND CREATED_TIME < :endDate";
        
        String updatePostSqlCmd = "DELETE FROM BC_POST_SCORE";
        if(criteria != null)
            updatePostSqlCmd += criteria;
        
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(updatePostSqlCmd);
        if(startDate != null)
            sqlPostsQuery.setParameter("startDate", startDate);
        if(endDate != null)
            sqlPostsQuery.setParameter("endDate", endDate);
        return sqlPostsQuery.executeUpdate();
    }
    
    @Override
    public int batchDeleteByPostIds(List<Long> postIds) {
        if(postIds == null || postIds.size() <= 0)
            return 0;
        
        String batchDeleteSqlCmd = "UPDATE BC_POST_SCORE SET IS_DELETED = '1' WHERE POST_ID IN (:postIds)";      
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(batchDeleteSqlCmd);
        sqlPostsQuery.setParameterList("postIds", postIds);
        return sqlPostsQuery.executeUpdate();
    }
    
    @Override
    public PageResult<Object[]> getDisputePostIds(String locale, Long circleTypeId, PoolType poolType, Date startTime, Date endTime, CreatorType creatorType, Boolean withTotalSize, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        
        if(locale != null)
            dc.add(Restrictions.eq("postLocale", locale));
        if(poolType != null)
            dc.add(Restrictions.eq("poolType", poolType));
        
        dc.add(Restrictions.isNull("reviewerId"));
        dc.add(Restrictions.isNull("isHandled"));
        
        if(creatorType != null)
            dc.add(Restrictions.eq("creatorType", creatorType));
        
        if(circleTypeId != null && circleTypeId > 0)
            dc.add(Restrictions.eq("circleTypeId", circleTypeId));
        if(startTime != null)
            dc.add(Restrictions.ge("createdTime", startTime));
        if(endTime != null)
            dc.add(Restrictions.le("createdTime", endTime));
        dc.setProjection(Projections.projectionList()
                .add(Projections.property("postId"))
                .add(Projections.property("appName"))
                .add(Projections.property("score"))
                .add(Projections.property("createdTime"))
                .add(Projections.property("info")));
        if(!withTotalSize)
            return blockQueryWithoutSize(dc, blockLimit);
        
        return blockQuery(dc, blockLimit);
    }
    
    @Override
    public PageResult<Object[]> getDisputePostIdsOrderByScore(String locale, Long circleTypeId, PoolType poolType, Date startTime, Date endTime, CreatorType creatorType, Boolean withTotalSize, BlockLimit blockLimit) {
        PageResult<Object[]> pgResult = new PageResult<Object[]>();
        if(blockLimit.getOffset() < 0) {
            if(withTotalSize)
                pgResult.setTotalSize(0);
            else
                pgResult.setTotalSize(Integer.MAX_VALUE);
            return pgResult;
        }

        String getRevivedSqlCmd = "FROM BC_POST_SCORE " 
                                  + "LEFT OUTER JOIN BC_POST_ATTR likeCount ON likeCount.REF_ID = BC_POST_SCORE.POST_ID AND likeCount.ATTR_TYPE = 'PostLikeCount' "
                                  + "LEFT OUTER JOIN BC_POST_ATTR circleInCount ON circleInCount.REF_ID = BC_POST_SCORE.POST_ID AND circleInCount.ATTR_TYPE = 'PostCircleInCount' "
                                  + "WHERE BC_POST_SCORE.IS_DELETED = 0 "
                                  + "AND BC_POST_SCORE.POOL_TYPE = :poolType "
                                  + "AND BC_POST_SCORE.REVIEWER_ID IS NULL "
                                  + "AND BC_POST_SCORE.IS_HANDLED IS NULL "
                                  + "AND BC_POST_SCORE.POST_LOCALE = :locale "
                                  + "AND (likeCount.REF_TYPE = 'Post' OR likeCount.REF_TYPE IS NULL) "
                                  + "AND (circleInCount.REF_TYPE = 'Post' OR circleInCount.REF_TYPE IS NULL) ";
        
        if(circleTypeId != null && circleTypeId > 0)
            getRevivedSqlCmd += "AND BC_POST_SCORE.CIRCLE_TYPE_ID = :circleTypeId ";
        if(creatorType != null)
            getRevivedSqlCmd += "AND BC_POST_SCORE.USER_TYPE = :creatorType ";
        
        String getFieldsCmd = "SELECT BC_POST_SCORE.POST_ID as postId, BC_POST_SCORE.APP_NAME as appName, BC_POST_SCORE.SCORE as score, BC_POST_SCORE.CREATED_TIME as createdTime, BC_POST_SCORE.INFO as info ";
        SQLQuery sqlFieldQuery = getSession().createSQLQuery(getFieldsCmd + getRevivedSqlCmd + "GROUP BY BC_POST_SCORE.POST_ID ORDER BY SUM(IFNULL(likeCount.ATTR_VALUE, 0) + (3 * IFNULL(circleInCount.ATTR_VALUE, 0))) DESC, createdTime DESC LIMIT :offset , :limit");
        sqlFieldQuery.setParameter("poolType", poolType.toString());
        if(circleTypeId != null && circleTypeId > 0)
            sqlFieldQuery.setParameter("circleTypeId", circleTypeId);
        if(creatorType != null)
            sqlFieldQuery.setParameter("creatorType", creatorType.toString());
        sqlFieldQuery.setParameter("locale", locale);
        sqlFieldQuery.setParameter("offset", blockLimit.getOffset());
        sqlFieldQuery.setParameter("limit", blockLimit.getSize());
        sqlFieldQuery.addScalar("postId", new LongType());
        sqlFieldQuery.addScalar("appName", new StringType());
        sqlFieldQuery.addScalar("score", new IntegerType());
        sqlFieldQuery.addScalar("createdTime", new DateType());
        sqlFieldQuery.addScalar("info", new StringType());
        List<Object[]> result = sqlFieldQuery.list();
        Integer totalSize = Integer.MAX_VALUE;
        if(withTotalSize) {
            String getCountCmd = "SELECT COUNT(*) ";
            SQLQuery sqlCountQuery = getSession().createSQLQuery(getCountCmd + getRevivedSqlCmd);
            sqlCountQuery.setParameter("poolType", poolType.toString());
            if(circleTypeId != null && circleTypeId > 0)
                sqlCountQuery.setParameter("circleTypeId", circleTypeId);
            if(creatorType != null)
                sqlCountQuery.setParameter("creatorType", creatorType.toString());
            sqlCountQuery.setParameter("locale", locale);
            List<Object> count = sqlCountQuery.list();
            if(count != null && count.size() > 0)
                totalSize = ((BigInteger)count.get(0)).intValue();
        }

        pgResult.setResults(result);
        pgResult.setTotalSize(totalSize);
        return pgResult;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public PageResult<Object[]> getRevivedDisputePostIds(String locale, Long circleTypeId, PoolType poolType, Date startTime, Date endTime, Boolean withTotalSize, BlockLimit blockLimit) {
        PageResult<Object[]> pgResult = new PageResult<Object[]>();
        if(blockLimit.getOffset() < 0) {
            if(withTotalSize)
                pgResult.setTotalSize(0);
            else
                pgResult.setTotalSize(Integer.MAX_VALUE);
            return pgResult;
        }

        String getRevivedSqlCmd = "FROM BC_POST_SCORE " 
                                  + "LEFT OUTER JOIN BC_POST_ATTR ON BC_POST_ATTR.REF_ID = BC_POST_SCORE.POST_ID "
                                  + "WHERE BC_POST_SCORE.IS_DELETED = 0 "
                                  + "AND BC_POST_SCORE.POOL_TYPE = :poolType "
                                  + "AND BC_POST_SCORE.REVIEWER_ID IS NULL "
                                  + "AND BC_POST_SCORE.IS_HANDLED IS NULL "
                                  + "AND BC_POST_SCORE.POST_LOCALE = :locale "
                                  + "AND (BC_POST_ATTR.REF_TYPE = 'Post' OR BC_POST_ATTR.REF_TYPE IS NULL) "
                                  + "AND ((BC_POST_ATTR.ATTR_TYPE = 'PostLikeCount') OR (BC_POST_ATTR.ATTR_TYPE = 'PostCommentCount') OR BC_POST_ATTR.ATTR_TYPE IS NULL) ";
        
        if(circleTypeId != null && circleTypeId > 0)
            getRevivedSqlCmd += "AND BC_POST_SCORE.CIRCLE_TYPE_ID = :circleTypeId ";
        String getFieldsCmd = "SELECT BC_POST_SCORE.POST_ID as postId, BC_POST_SCORE.APP_NAME as appName, BC_POST_SCORE.SCORE as score, BC_POST_SCORE.CREATED_TIME as createdTime, BC_POST_SCORE.INFO as info, SUM(BC_POST_ATTR.ATTR_VALUE) as popularity ";
        SQLQuery sqlFieldQuery = getSession().createSQLQuery(getFieldsCmd + getRevivedSqlCmd + "GROUP BY BC_POST_SCORE.POST_ID ORDER BY popularity DESC, postId DESC LIMIT :offset , :limit");
        sqlFieldQuery.setParameter("poolType", poolType.toString());
        if(circleTypeId != null && circleTypeId > 0)
            sqlFieldQuery.setParameter("circleTypeId", circleTypeId);
        sqlFieldQuery.setParameter("locale", locale);
        sqlFieldQuery.setParameter("offset", blockLimit.getOffset());
        sqlFieldQuery.setParameter("limit", blockLimit.getSize());
        sqlFieldQuery.addScalar("postId", new LongType());
        sqlFieldQuery.addScalar("appName", new StringType());
        sqlFieldQuery.addScalar("score", new IntegerType());
        sqlFieldQuery.addScalar("createdTime", new DateType());
        sqlFieldQuery.addScalar("info", new StringType());
        sqlFieldQuery.addScalar("popularity", new LongType());
        List<Object[]> result = sqlFieldQuery.list();
        Integer totalSize = Integer.MAX_VALUE;
        if(withTotalSize) {
            String getCountCmd = "SELECT COUNT(*) ";
            SQLQuery sqlCountQuery = getSession().createSQLQuery(getCountCmd + getRevivedSqlCmd);
            sqlCountQuery.setParameter("poolType", poolType.toString());
            if(circleTypeId != null && circleTypeId > 0)
                sqlCountQuery.setParameter("circleTypeId", circleTypeId);
            sqlCountQuery.setParameter("locale", locale);
            List<Object> count = sqlCountQuery.list();
            if(count != null && count.size() > 0)
                totalSize = ((BigInteger)count.get(0)).intValue();
        }

        pgResult.setResults(result);
        pgResult.setTotalSize(totalSize);
        return pgResult;
    }
    
    @Override
    public List<Long> findExPostIds(List<Long> postIds, ResultType resultType, Boolean isHandled, Boolean isDeleted) {
        DetachedCriteria dc = createDetachedCriteria();
        if(postIds == null || postIds.size() <= 0)
             return null;
        dc.add(Restrictions.in("postId", postIds));
        if(isDeleted != null)
            dc.add(Restrictions.eq("isDeleted", isDeleted));
        if(resultType != null)
            dc.add(Restrictions.eq("resultType", resultType));
        if(isHandled != null)
            dc.add(Restrictions.eq("isHandled", isHandled));
        
        dc.setProjection(Projections.property("postId"));
        return findByCriteria(dc);
    }
    
    @Override
    public List<PostScore> findByIds(List<Long> ids) {
        DetachedCriteria dc = createDetachedCriteria();
        if(ids == null || ids.size() <= 0)
             return null;
        dc.add(Restrictions.in("id", ids));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return findByCriteria(dc);
    }
    
    @Override
    public List<PostScore> findByPostIds(List<Long> postIds, Boolean isDeleted) {
        DetachedCriteria dc = createDetachedCriteria();
        if(postIds == null || postIds.size() <= 0)
             return null;
        dc.add(Restrictions.in("postId", postIds));
        if(isDeleted != null)
            dc.add(Restrictions.eq("isDeleted", isDeleted));
        return findByCriteria(dc);
    }
    
    @Override
    public List<Object> getHandledPostScoreCountByDate(String postLocale, PoolType poolType, List<ResultType> resultTypes, Date begin, Date end) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.isNotNull("reviewerId"));
        dc.add(Restrictions.isNotNull("isHandled"));
        
        if(postLocale != null && postLocale.length() > 0)
            dc.add(Restrictions.eq("postLocale", postLocale));
        if(begin != null)
            dc.add(Restrictions.ge("lastModified", begin));
        if(end != null)
            dc.add(Restrictions.le("lastModified", end));
        if(poolType != null)
            dc.add(Restrictions.eq("poolType", poolType));
        
        if(resultTypes != null && resultTypes.size() > 0)
            dc.add(Restrictions.in("resultType", resultTypes));
        
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.projectionList()
                .add(Projections.groupProperty("resultType"))
                .add(Projections.rowCount(), "count"));
        return findByCriteria(dc);
    }
    
    @Override
    public List<Object> getHandledPostScoreCounts(String postLocale, PoolType poolType, List<ResultType> resultTypes, Date begin, Date end) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.isNotNull("reviewerId"));
        dc.add(Restrictions.isNotNull("isHandled"));
        
        if(postLocale != null && postLocale.length() > 0)
            dc.add(Restrictions.eq("postLocale", postLocale));
        if(begin != null)
            dc.add(Restrictions.ge("lastModified", begin));
        if(end != null)
            dc.add(Restrictions.le("lastModified", end));
        if(poolType != null)
            dc.add(Restrictions.eq("poolType", poolType));

        if(resultTypes != null && resultTypes.size() > 0)
            dc.add(Restrictions.in("resultType", resultTypes));
        
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.projectionList()
        		.add(Projections.groupProperty("postLocale"))
        		.add(Projections.sqlGroupProjection("DATE(DATE_ADD(LAST_MODIFIED, INTERVAL 8 HOUR)) as lastModifiedDate", "lastModifiedDate", 
        											new String[] { "lastModifiedDate" }, new Type[] { StandardBasicTypes.DATE }))
        		.add(Projections.groupProperty("resultType"))
                .add(Projections.rowCount(), "count"));
        dc.addOrder(Order.asc("postLocale"));
        dc.addOrder(Order.desc("lastModified"));

		return findByCriteria(dc);
    }
    
    @Override
    public List<Object> listUnCuratedPostCounts(List<String> postLocale, Long circleTypeId, PoolType poolType){ //sync to getDisputePostIds & getRevivedDisputePostIds
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        if(postLocale != null && postLocale.size() >= 0)
            dc.add(Restrictions.in("postLocale", postLocale));
        if(poolType != null)
            dc.add(Restrictions.eq("poolType", poolType));
        dc.add(Restrictions.isNull("reviewerId"));
        dc.add(Restrictions.isNull("isHandled"));
        if(circleTypeId != null && circleTypeId > 0)
            dc.add(Restrictions.eq("circleTypeId", circleTypeId));
        dc.setProjection(Projections.projectionList()
        		.add(Projections.groupProperty("postLocale"))
        		.add(Projections.rowCount(), "count")
        		.add(Projections.sqlProjection("MIN(POST_CREATE_DATE) as minPostCreateDate", new String[] { "minPostCreateDate" }, new Type[] { StandardBasicTypes.TIMESTAMP })));
		return findByCriteria(dc);
    }

}
