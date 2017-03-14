package com.cyberlink.cosmetic.modules.post.dao.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.dao.PostReportedDao;
import com.cyberlink.cosmetic.modules.post.model.PostReported;
import com.cyberlink.cosmetic.modules.post.model.PostReported.PostReportedStatus;
import com.cyberlink.cosmetic.modules.post.result.PostReportedWrapper;
import com.cyberlink.cosmetic.modules.user.model.User;

public class PostReportedDaoHibernate extends AbstractDaoCosmetic<PostReported, Long>
    implements PostReportedDao {

    @Override
    public PageResult<PostReported> findByTarget(Object target, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("target", target));
        return blockQuery(dc, blockLimit);
    }
    
    @Override
    public PageResult<PostReported> findByTargetAndUser(Object target, User reporter, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("target", target));
        dc.add(Restrictions.eq("reporter", reporter));
        return blockQuery(dc, blockLimit);
    }
    
    @Override
    public List<PostReported> getByTargets(String refType, PostReportedStatus status, Long... targetIds) {
        DetachedCriteria d = createDetachedCriteria();
        String sql = "REF_TYPE = '" + refType + "' AND STATUS = '"+ status.toString() +"' AND REF_ID in (";
        for(Long id : targetIds) {
            sql += String.valueOf(id) + ",";
        }
        sql = sql.substring(0, sql.length() - 1);
        sql += ")";
        d.add(Restrictions.sqlRestriction(sql));
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return findByCriteria(d);
    }
    
    @Override
    public PageResult<PostReportedWrapper> getReportedPostCount(Long searchAuthorId, Long searchReportedId, String refType, PostReportedStatus status, String region, BlockLimit blockLimit)
    {
        PageResult<PostReportedWrapper> result = new PageResult<PostReportedWrapper>();
        String queryReportedPost = "SELECT BC_POST_REPORTED.REF_TYPE as refType, BC_POST_REPORTED.REF_ID as refId, COUNT(*) as count FROM BC_POST_REPORTED "
                                 + "INNER JOIN BC_POST ON BC_POST_REPORTED.REF_ID = BC_POST.ID AND BC_POST_REPORTED.REF_TYPE='Post' "
                                 + "WHERE BC_POST.IS_DELETED=0 AND BC_POST.LOCALE=:region AND BC_POST_REPORTED.STATUS=:status ";
        if(status.equals(PostReportedStatus.NewReported))
            queryReportedPost += "AND BC_POST.POST_STATUS='Review' ";
        if(searchAuthorId != null)
            queryReportedPost += "AND BC_POST.USER_ID = " + String.valueOf(searchAuthorId) + " ";
        if(searchReportedId != null)
            queryReportedPost += "AND BC_POST_REPORTED.REPORTER_ID = " + String.valueOf(searchReportedId) + " ";
        queryReportedPost += "GROUP BY BC_POST_REPORTED.REF_ID ";

        String queryReportedComment = "SELECT BC_POST_REPORTED.REF_TYPE as refType, BC_POST_REPORTED.REF_ID as refId, COUNT(*) as count FROM BC_POST_REPORTED "
                                    + "INNER JOIN BC_COMMENT ON BC_POST_REPORTED.REF_ID = BC_COMMENT.ID AND BC_POST_REPORTED.REF_TYPE='Comment' "
                                    + "INNER JOIN BC_POST ON BC_COMMENT.REF_ID = BC_POST.ID "
                                    + "WHERE BC_POST.IS_DELETED=0 AND BC_COMMENT.IS_DELETED=0 AND BC_POST.LOCALE=:region AND BC_POST_REPORTED.STATUS=:status ";
        if(searchAuthorId != null)
            queryReportedComment += "AND BC_COMMENT.USER_ID = " + String.valueOf(searchAuthorId) + " ";
        if(searchReportedId != null)
            queryReportedComment += "AND BC_POST_REPORTED.REPORTER_ID = " + String.valueOf(searchReportedId) + " ";
        queryReportedComment += "GROUP BY BC_POST_REPORTED.REF_ID ";

		String queryReportedSubComment = "SELECT BC_POST_REPORTED.REF_TYPE as refType, BC_POST_REPORTED.REF_ID as refId, COUNT(*) as count FROM BC_POST_REPORTED "
									+ "INNER JOIN BC_COMMENT as commentA ON BC_POST_REPORTED.REF_ID = commentA.ID AND BC_POST_REPORTED.REF_TYPE='Comment' AND commentA.REF_TYPE='Comment' "
									+ "INNER JOIN BC_COMMENT as commentB ON commentA.REF_ID = commentB.ID "
									+ "INNER JOIN BC_POST ON commentB.REF_ID = BC_POST.ID "
									+ "WHERE BC_POST.IS_DELETED=0 AND commentA.IS_DELETED=0 AND commentB.IS_DELETED=0 AND BC_POST.LOCALE=:region AND BC_POST_REPORTED.STATUS=:status ";
		if (searchAuthorId != null)
			queryReportedSubComment += "AND commentA.USER_ID = " + String.valueOf(searchAuthorId) + " ";
		if (searchReportedId != null)
			queryReportedSubComment += "AND BC_POST_REPORTED.REPORTER_ID = " + String.valueOf(searchReportedId) + " ";
		queryReportedSubComment += "GROUP BY BC_POST_REPORTED.REF_ID ";

        String queryResult = null;
        String queryCount = null;
        if(refType == null) {
            queryResult = queryReportedPost + "UNION " + queryReportedComment + "UNION " + queryReportedSubComment + "ORDER BY refId DESC LIMIT :limit OFFSET :offset ";
            queryCount = "SELECT COUNT(*) FROM ( " + queryReportedPost + "UNION " + queryReportedComment + ") t";
        }
        else if(refType.equals("Post")) {
            queryResult = queryReportedPost + "ORDER BY refId DESC LIMIT :limit OFFSET :offset ";
            queryCount = "SELECT COUNT(*) FROM ( " + queryReportedPost + ") t";
        }
        else if(refType.equals("Comment")) {
            queryResult = queryReportedComment + "UNION " + queryReportedSubComment + "ORDER BY refId DESC LIMIT :limit OFFSET :offset ";
            queryCount = "SELECT COUNT(*) FROM ( " + queryReportedComment + ") t";
        }
        else {
            return result;
        }
        
        //String queryReportedTarget = "SELECT BC_POST_REPORTED.REF_TYPE as refType, BC_POST_REPORTED.REF_ID as refId, COUNT(*) as count FROM BC_POST_REPORTED INNER JOIN BC_POST ON BC_POST_REPORTED.REF_ID = BC_POST.ID AND BC_POST_REPORTED.REF_TYPE='Post' WHERE BC_POST.LOCALE=:region AND BC_POST_REPORTED.STATUS=:status GROUP BY BC_POST_REPORTED.REF_ID UNION SELECT BC_POST_REPORTED.REF_TYPE as refType, BC_POST_REPORTED.REF_ID as refId, COUNT(*) as count FROM BC_POST_REPORTED INNER JOIN BC_COMMENT ON BC_POST_REPORTED.REF_ID = BC_COMMENT.ID AND BC_POST_REPORTED.REF_TYPE='Comment' INNER JOIN BC_POST ON BC_COMMENT.REF_ID = BC_POST.ID WHERE BC_POST.LOCALE=:region AND BC_POST_REPORTED.STATUS=:status GROUP BY BC_POST_REPORTED.REF_ID ORDER BY refId LIMIT :limit OFFSET :offset";
        Query sqlTargetQuery = getSession().createSQLQuery(queryResult).setResultTransformer(Transformers.aliasToBean(PostReportedWrapper.class));
        sqlTargetQuery.setParameter("region", region);
        sqlTargetQuery.setParameter("status", status.toString());
        sqlTargetQuery.setParameter("offset", blockLimit.getOffset());
        sqlTargetQuery.setParameter("limit", blockLimit.getSize());
        List<PostReportedWrapper> objs = sqlTargetQuery.list();
        result.setResults(objs);
        
        //String queryReportedTargetCount = "SELECT COUNT(*) FROM ( SELECT BC_POST_REPORTED.REF_TYPE as refType, BC_POST_REPORTED.REF_ID as refId, COUNT(*) as count FROM BC_POST_REPORTED INNER JOIN BC_POST ON BC_POST_REPORTED.REF_ID = BC_POST.ID AND BC_POST_REPORTED.REF_TYPE='Post' WHERE BC_POST.LOCALE=:region AND BC_POST_REPORTED.STATUS=:status GROUP BY BC_POST_REPORTED.REF_ID UNION SELECT BC_POST_REPORTED.REF_TYPE as refType, BC_POST_REPORTED.REF_ID as refId, COUNT(*) as count FROM BC_POST_REPORTED INNER JOIN BC_COMMENT ON BC_POST_REPORTED.REF_ID = BC_COMMENT.ID AND BC_POST_REPORTED.REF_TYPE='Comment' INNER JOIN BC_POST ON BC_COMMENT.REF_ID = BC_POST.ID WHERE BC_POST.LOCALE=:region AND BC_POST_REPORTED.STATUS=:status GROUP BY BC_POST_REPORTED.REF_ID ORDER BY refId ) t";
        Query sqlTargetCountQuery = getSession().createSQLQuery(queryCount);
        sqlTargetCountQuery.setParameter("region", region);
        sqlTargetCountQuery.setParameter("status", status.toString());
        Object totalSize = sqlTargetCountQuery.uniqueResult();
        if(totalSize != null)
            result.setTotalSize(((Number)totalSize).intValue());
        else
            result.setTotalSize(0);
        return result;
    }

    @Override
    public PageResult<PostReportedWrapper> getRelatedPostComment(Long searchAuthorId, PostReportedStatus status, String region, BlockLimit blockLimit)
    {
        PageResult<PostReportedWrapper> result = new PageResult<PostReportedWrapper>();
        if(searchAuthorId == null)
            return result;
        
        String queryReportedPost = "SELECT BC_POST_REPORTED.REF_TYPE as refType, BC_POST_REPORTED.REF_ID as refId FROM BC_POST_REPORTED "
                                 + "INNER JOIN BC_POST ON BC_POST_REPORTED.REF_ID = BC_POST.ID AND BC_POST_REPORTED.REF_TYPE='Post' "
                                 + "WHERE BC_POST.LOCALE=:region AND BC_POST_REPORTED.STATUS=:status AND BC_POST.USER_ID = " + String.valueOf(searchAuthorId) + " "
                                 + "GROUP BY BC_POST_REPORTED.REF_ID ";

        String queryReportedComment = "SELECT BC_POST_REPORTED.REF_TYPE as refType, BC_POST_REPORTED.REF_ID as refId FROM BC_POST_REPORTED "
                                    + "INNER JOIN BC_COMMENT ON BC_POST_REPORTED.REF_ID = BC_COMMENT.ID AND BC_POST_REPORTED.REF_TYPE='Comment' "
                                    + "INNER JOIN BC_POST ON BC_COMMENT.REF_ID = BC_POST.ID "
                                    + "WHERE BC_POST.LOCALE=:region AND BC_POST_REPORTED.STATUS=:status AND BC_COMMENT.USER_ID = " + String.valueOf(searchAuthorId) + " "
                                    + "GROUP BY BC_POST_REPORTED.REF_ID ";
        
        String queryRelatedPost = "SELECT 'Post' as refType, BC_POST.ID as refId FROM BC_POST "
                                + "WHERE BC_POST.LOCALE=:region AND BC_POST.POST_STATUS='Published' AND BC_POST.IS_DELETED=0 AND BC_POST.USER_ID = " + String.valueOf(searchAuthorId) + " "
                                + "GROUP BY BC_POST.ID ";

        String queryRelatedComment = "SELECT 'Comment' as refType, BC_COMMENT.ID as refId FROM BC_COMMENT "
                + "WHERE BC_COMMENT.COMMENT_STATUS='Published' AND BC_COMMENT.IS_DELETED=0 AND BC_COMMENT.USER_ID = " + String.valueOf(searchAuthorId) + " "
                + "GROUP BY BC_COMMENT.ID ORDER BY refId ";
        
        String queryResult = queryReportedPost + "UNION " + queryReportedComment + "UNION " + queryRelatedPost + "UNION " + queryRelatedComment + "LIMIT :limit OFFSET :offset";

        String queryCount = "SELECT COUNT(*) FROM ( " + queryReportedPost + "UNION " + queryReportedComment + "UNION " + queryRelatedPost + "UNION " + queryRelatedComment + ") t";
                
        //String queryReportedTarget = "SELECT BC_POST_REPORTED.REF_TYPE as refType, BC_POST_REPORTED.REF_ID as refId, COUNT(*) as count FROM BC_POST_REPORTED INNER JOIN BC_POST ON BC_POST_REPORTED.REF_ID = BC_POST.ID AND BC_POST_REPORTED.REF_TYPE='Post' WHERE BC_POST.LOCALE=:region AND BC_POST_REPORTED.STATUS=:status GROUP BY BC_POST_REPORTED.REF_ID UNION SELECT BC_POST_REPORTED.REF_TYPE as refType, BC_POST_REPORTED.REF_ID as refId, COUNT(*) as count FROM BC_POST_REPORTED INNER JOIN BC_COMMENT ON BC_POST_REPORTED.REF_ID = BC_COMMENT.ID AND BC_POST_REPORTED.REF_TYPE='Comment' INNER JOIN BC_POST ON BC_COMMENT.REF_ID = BC_POST.ID WHERE BC_POST.LOCALE=:region AND BC_POST_REPORTED.STATUS=:status GROUP BY BC_POST_REPORTED.REF_ID ORDER BY refId LIMIT :limit OFFSET :offset";
        Query sqlTargetQuery = getSession().createSQLQuery(queryResult).setResultTransformer(Transformers.aliasToBean(PostReportedWrapper.class));
        sqlTargetQuery.setParameter("region", region);
        sqlTargetQuery.setParameter("status", status.toString());
        sqlTargetQuery.setParameter("offset", blockLimit.getOffset());
        sqlTargetQuery.setParameter("limit", blockLimit.getSize());
        List<PostReportedWrapper> objs = sqlTargetQuery.list();
        result.setResults(objs);
        
        //String queryReportedTargetCount = "SELECT COUNT(*) FROM ( SELECT BC_POST_REPORTED.REF_TYPE as refType, BC_POST_REPORTED.REF_ID as refId, COUNT(*) as count FROM BC_POST_REPORTED INNER JOIN BC_POST ON BC_POST_REPORTED.REF_ID = BC_POST.ID AND BC_POST_REPORTED.REF_TYPE='Post' WHERE BC_POST.LOCALE=:region AND BC_POST_REPORTED.STATUS=:status GROUP BY BC_POST_REPORTED.REF_ID UNION SELECT BC_POST_REPORTED.REF_TYPE as refType, BC_POST_REPORTED.REF_ID as refId, COUNT(*) as count FROM BC_POST_REPORTED INNER JOIN BC_COMMENT ON BC_POST_REPORTED.REF_ID = BC_COMMENT.ID AND BC_POST_REPORTED.REF_TYPE='Comment' INNER JOIN BC_POST ON BC_COMMENT.REF_ID = BC_POST.ID WHERE BC_POST.LOCALE=:region AND BC_POST_REPORTED.STATUS=:status GROUP BY BC_POST_REPORTED.REF_ID ORDER BY refId ) t";
        Query sqlTargetCountQuery = getSession().createSQLQuery(queryCount);
        sqlTargetCountQuery.setParameter("region", region);
        sqlTargetCountQuery.setParameter("status", status.toString());
        Object totalSize = sqlTargetCountQuery.uniqueResult();
        if(totalSize != null)
            result.setTotalSize(((Number)totalSize).intValue());
        else
            result.setTotalSize(0);
        return result;
    }
    
}
