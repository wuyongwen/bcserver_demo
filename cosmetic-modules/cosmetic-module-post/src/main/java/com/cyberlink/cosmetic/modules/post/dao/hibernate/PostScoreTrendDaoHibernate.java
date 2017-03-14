package com.cyberlink.cosmetic.modules.post.dao.hibernate;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.dao.PostScoreTrendDao;
import com.cyberlink.cosmetic.modules.post.model.PostScoreTrend;
import com.cyberlink.cosmetic.modules.post.model.PostScore.PoolType;
import com.cyberlink.cosmetic.modules.post.model.PostScore.ResultType;

public class PostScoreTrendDaoHibernate extends AbstractDaoCosmetic<PostScoreTrend, Long>
    implements PostScoreTrendDao {
	
	@Override
    public List<PostScoreTrend> findByPostIds(List<Long> postIds, Boolean isDeleted) {
        DetachedCriteria dc = createDetachedCriteria();
        if(postIds == null || postIds.size() <= 0)
             return null;
        dc.add(Restrictions.in("postId", postIds));
        if(isDeleted != null)
            dc.add(Restrictions.eq("isDeleted", isDeleted));
        return findByCriteria(dc);
    }
	
	@Override
    public Integer markToHandle(Long reviewerId, ResultType resultType, List<Long> postIds) {
        if(postIds == null || postIds.size() <= 0)
            return 0;
        
        String updatePostSqlCmd = "UPDATE BC_POST_SCORE_TREND SET REVIEWER_ID=:reviewerId, ";
        if(resultType != null)
            updatePostSqlCmd += "RESULT_TYPE = :resultType, ";
        updatePostSqlCmd += "IS_HANDLED=0  WHERE POST_ID IN :postIds AND IS_DELETED=0 ";
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(updatePostSqlCmd);
        sqlPostsQuery.setParameter("reviewerId", reviewerId);
        if(resultType != null)
            sqlPostsQuery.setParameter("resultType", resultType.toString());
        sqlPostsQuery.setParameterList("postIds", postIds);
        return sqlPostsQuery.executeUpdate();
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

        String getRevivedSqlCmd = "FROM BC_POST_SCORE_TREND " 
                                  + "LEFT OUTER JOIN BC_POST_ATTR ON BC_POST_ATTR.REF_ID = BC_POST_SCORE_TREND.POST_ID "
                                  + "WHERE BC_POST_SCORE_TREND.IS_DELETED = 0 "
                                  + "AND BC_POST_SCORE_TREND.POOL_TYPE = :poolType "
                                  + "AND BC_POST_SCORE_TREND.RESULT_TYPE != 'ChangeKeyWord' "
                                  + "AND BC_POST_SCORE_TREND.REVIEWER_ID IS NOT NULL "
                                  + "AND BC_POST_SCORE_TREND.IS_HANDLED = 1 "
                                  + "AND BC_POST_SCORE_TREND.POST_LOCALE = :locale "
                                  + "AND (BC_POST_ATTR.REF_TYPE = 'Post' OR BC_POST_ATTR.REF_TYPE IS NULL) "
                                  + "AND (BC_POST_ATTR.ATTR_TYPE = 'PostLikeCount' OR 'PostCommentCount' OR BC_POST_ATTR.ATTR_TYPE IS NULL) ";
        
        if(circleTypeId != null)
            getRevivedSqlCmd += "AND BC_POST_SCORE_TREND.CIRCLE_TYPE_ID = :circleTypeId ";
        String getFieldsCmd = "SELECT BC_POST_SCORE_TREND.POST_ID as postId, BC_POST_SCORE_TREND.APP_NAME as appName, BC_POST_SCORE_TREND.SCORE as score, BC_POST_SCORE_TREND.CREATED_TIME as createdTime, BC_POST_SCORE_TREND.INFO as info, BC_POST_SCORE_TREND.SCORE as popularity ";
        SQLQuery sqlFieldQuery = getSession().createSQLQuery(getFieldsCmd + getRevivedSqlCmd + "GROUP BY BC_POST_SCORE_TREND.POST_ID ORDER BY BC_POST_SCORE_TREND.SCORE DESC LIMIT :offset , :limit");
        sqlFieldQuery.setParameter("poolType", poolType.toString());
        if(circleTypeId != null)
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
            if(circleTypeId != null)
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
    public PostScoreTrend getLastHandledRecord(String postLocale, PoolType poolType, List<ResultType> resultTypes) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        if(postLocale != null && postLocale.length() > 0)
            dc.add(Restrictions.eq("postLocale", postLocale));
        if(poolType != null)
            dc.add(Restrictions.eq("poolType", poolType));
        
        if(resultTypes != null && resultTypes.size() > 0)
            dc.add(Restrictions.in("resultType", resultTypes));
        else
            dc.add(Restrictions.isNotNull("resultType"));
        dc.add(Restrictions.isNotNull("reviewerId"));
        dc.add(Restrictions.isNotNull("isHandled"));
        BlockLimit blockLimit = new BlockLimit(0, 1);
        blockLimit.addOrderBy("lastModified", false);
        PageResult<PostScoreTrend> results = blockQuery(dc, blockLimit);
        if(results.getResults().size() <= 0)
            return null;
        else
            return results.getResults().get(0);
    }
    
    @Override
    public List<Object> getHandledPostScoreCountByDate(String postLocale, PoolType poolType, List<ResultType> resultTypes, Date begin, Date end) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.isNotNull("reviewerId"));
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
        else
            dc.add(Restrictions.isNotNull("resultType"));
        
        dc.add(Restrictions.eq("isHandled", true));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.projectionList()
                .add(Projections.groupProperty("resultType"))
                .add(Projections.countDistinct("postId"), "count"));
        return findByCriteria(dc);
    }
}