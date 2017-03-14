package com.cyberlink.cosmetic.modules.post.dao.hibernate;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.dao.CommentDao;
import com.cyberlink.cosmetic.modules.post.model.Comment;
import com.cyberlink.cosmetic.modules.post.model.Comment.CommentStatus;

public class CommentDaoHibernate extends AbstractDaoCosmetic<Comment, Long>
    implements CommentDao {
    
    @Override
    public Long getCommentCount(String refType, Long refId) {
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.eq("refType", refType));
        d.add(Restrictions.eq("refId", refId));
        d.add(Restrictions.eq("commentStatus", CommentStatus.Published));
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        d.setProjection(Projections.rowCount());
        return uniqueResult(d);
    }
    
	@Override
	public void updateCommentAttr(Long refId, Long subCommentId, Integer diff) {
		String updateSubCommentId = "";
		if (subCommentId == null || subCommentId != (long) -1) //subCommentId don't need to be updated
			updateSubCommentId = "LATEST_SUBCOMMENT_ID = :subCommentId, ";
		String updateSqlCmd = "UPDATE BC_COMMENT SET " + updateSubCommentId
				+ "SUBCOMMENT_COUNT = SUBCOMMENT_COUNT + :diff WHERE ID = :refId";
		SQLQuery sqlQuery = getSession().createSQLQuery(updateSqlCmd);
		if (subCommentId == null || subCommentId != (long) -1)
			sqlQuery.setParameter("subCommentId", subCommentId);
		sqlQuery.setParameter("diff", diff);
		sqlQuery.setParameter("refId", refId);
		sqlQuery.executeUpdate();
	}

	@Override
	public Long findLatestCommentId(String refType, Long refId) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("refType", refType));
		dc.add(Restrictions.eq("refId", refId));
		dc.add(Restrictions.eq("commentStatus", CommentStatus.Published));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));

		BlockLimit blocklimit = new BlockLimit(0, 1);
		blocklimit.addOrderBy("createdTime", false);
		
		PageResult<Comment> latestComment = blockQuery(dc, blocklimit);
		List<Comment> comment = latestComment.getResults();
		if (comment.isEmpty())
			return null;
		return comment.get(0).getId();
	}

	@Override
	public void deleteAllSubComments(String refType, Long refId){
		String updateSqlCmd = "UPDATE BC_COMMENT SET IS_DELETED = 1 WHERE REF_TYPE = :refType AND REF_ID = :refId";
		SQLQuery sqlQuery = getSession().createSQLQuery(updateSqlCmd);
		sqlQuery.setParameter("refType", refType);
		sqlQuery.setParameter("refId", refId);
		sqlQuery.executeUpdate();
	}

    @Override
    public Map<Long, Long> getCommentCountByTargetsWithoutEmpty(String refType, List<Long> refIds) {
    	Map<Long, Long> resultMap = new HashMap<Long, Long>();
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.eq("refType", refType));
        d.add(Restrictions.in("refId", refIds));
        d.add(Restrictions.eq("commentStatus", CommentStatus.Published));
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        d.setProjection(Projections.projectionList()
        		.add(Projections.groupProperty("refId"))
        		.add(Projections.rowCount()));
        List<Object> objs = findByCriteria(d);
        for (Object obj : objs) {
            Object[] row = (Object[]) obj;
            resultMap.put((Long) row[0], (Long) row[1]);
        }
        for (Long id : refIds) {
        	if (resultMap.containsKey(id))
        		continue;
        	resultMap.put(id, (long) 0);
        }
        return resultMap;
    }
    
    @Override
    public Map<Long, Long> getCommentCountByTargets(String refType, Long... refIds) {
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.eq("refType", refType));
        d.add(Restrictions.in("refId", refIds));
        d.add(Restrictions.eq("commentStatus", CommentStatus.Published));
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
        return result;
    }
    
    @Override
    public Map<Long, Long> getCommentCountByTargetsWithDate(String refType, Date startTime , Date endTime, Long... refIds) {
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.eq("refType", refType));
        d.add(Restrictions.in("refId", refIds));
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        if(startTime != null)
            d.add(Restrictions.ge("createdTime", startTime));
        if(endTime != null)
            d.add(Restrictions.le("createdTime", endTime));
        d.setProjection(Projections.projectionList()
                .add(Projections.groupProperty("refId"))
                .add(Projections.rowCount(), "count"));
        Map<Long, Long> result = new HashMap<Long, Long>();
        List<Object> objs = findByCriteria(d);
        for (Object obj : objs) {
            Object[] row = (Object[]) obj;
            result.put((Long) row[0], (Long) row[1]);
        }
        return result;
    }
    
    @Override
    public Map<Long, Map<String, Long>> getCommentRegionCountByTargetsWithDate(String refType, Date startTime , Date endTime, Long... refIds) {
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.eq("refType", refType));
        d.add(Restrictions.in("refId", refIds));
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        d.createAlias("creator", "creator");
        if(startTime != null)
            d.add(Restrictions.ge("createdTime", startTime));
        if(endTime != null)
            d.add(Restrictions.le("createdTime", endTime));
        d.setProjection(Projections.projectionList()
                .add(Projections.groupProperty("refId"))
                .add(Projections.groupProperty("creator.region"))
                .add(Projections.rowCount(), "count"));
        Map<Long, Map<String, Long>> result = new HashMap<Long, Map<String, Long>>();
        List<Object> objs = findByCriteria(d);
        for (Object obj : objs) {
            Object[] row = (Object[]) obj;
            if(!result.containsKey((Long) row[0]))
                result.put((Long) row[0], new HashMap<String, Long>());
            String reg = (String)row[1];
            if(reg == null)
                reg = "Unknown";
            result.get((Long) row[0]).put(reg, (Long)row[2]);
        }
        return result;
    }
    
    @Override
    public PageResult<Comment> blockQuery(String refType, Long refId, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("refType", refType));
        dc.add(Restrictions.eq("refId", refId));
        dc.add(Restrictions.eq("commentStatus", CommentStatus.Published));
        return blockQuery(dc, blockLimit);
    }
    
    @Override
    public PageResult<Comment> findByDate(Date start, Date end, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        if(start != null)
            dc.add(Restrictions.ge("createdTime", start));
        if(end != null)
            dc.add(Restrictions.le("createdTime", end));
        return blockQuery(dc, blockLimit);
    }
    
    @Override
    public PageResult<Comment> findAllActiveComment(String start, String end, BlockLimit blockLimit) {
        String finalSql = " FROM BC_COMMENT WHERE ID in (SELECT REF_ID FROM BC_LIKE WHERE REF_TYPE = 'Comment' AND IS_DELETED = 0 AND CREATED_TIME >= '" + start + "' AND CREATED_TIME <= '" + end + "') AND IS_DELETED=0";
        String queryActiveComment = "SELECT * " + finalSql + " LIMIT "+ String.valueOf(blockLimit.getSize()) +" OFFSET " + String.valueOf(blockLimit.getOffset());
        String queryActiveCommentCount = "SELECT COUNT(*) " + finalSql ;
        
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(queryActiveComment);
        sqlPostsQuery.addEntity("comment", Comment.class);
        List<Comment> comments = sqlPostsQuery.list();
        
        SQLQuery sqlSizeQuery = getSession().createSQLQuery(queryActiveCommentCount);
        Integer size = ((Number)sqlSizeQuery.uniqueResult()).intValue();
        
        PageResult<Comment> result = new PageResult<Comment>();
        result.setTotalSize(size);
        result.setResults(comments);
        return result;
    }
    
    @Override
    public List<Comment> findAllActiveCommentByTarget(String start, String end, List<Long> refIds) {
        String inRef = "";
        if(refIds != null && refIds.size() > 0) {
            inRef += " AND REF_ID in (";
            for(Long id : refIds) {
                inRef += String.valueOf(id) + ",";
            }
            inRef = inRef.substring(0, inRef.length() - 1) + ")";
        }
        
        String finalSql = " FROM BC_COMMENT WHERE ID in (SELECT REF_ID FROM BC_LIKE WHERE REF_TYPE = 'Comment' AND IS_DELETED = 0 AND CREATED_TIME >= '" + start + "' AND CREATED_TIME <= '" + end + "') AND IS_DELETED=0" + inRef;
        String queryActiveComment = "SELECT * " + finalSql;
        
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(queryActiveComment);
        sqlPostsQuery.addEntity("comment", Comment.class);
        return sqlPostsQuery.list();
    }
    
    @Override
    public List<Comment> findByIds(Long... ids) {
        if (ids == null || ids.length == 0) {
            return Collections.emptyList();
        }

        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("id", ids));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));

        return findByCriteria(dc);
    }
    
    @Override
    public PageResult<Comment> findByUserId(Long userId, String refType, BlockLimit blockLimit) {	
		DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("creatorId", userId));
        dc.add(Restrictions.eq("refType", refType));
        return blockQuery(dc, blockLimit);
    }

    @Override
    public PageResult<Comment> getOlderComment(String refType, Long refId,
            Long commentId, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("refType", refType));
        dc.add(Restrictions.eq("refId", refId));
        if (commentId != null)
            dc.add(Restrictions.lt("id", commentId));
        dc.add(Restrictions.eq("commentStatus", CommentStatus.Published));
        return blockQuery(dc, blockLimit);
    }

	@SuppressWarnings("unchecked")
	@Override
	public List<Comment> getAllCommentsByDate(String refType, String locale, Date startTime, Date endTime) {
		List<Comment> commentsList = new LinkedList<Comment>();
		int offset = 0;
		int limit = 100;
		do{
	        String finalSql = "SELECT BC.COMMENT_TEXT, BC.USER_ID FROM BC_COMMENT BC JOIN BC_POST BP ON BC.REF_ID = BP.ID "
	        		+ "WHERE BP.IS_DELETED = FALSE AND BP.LOCALE = :locale AND BC.IS_DELETED = FALSE AND BC.REF_TYPE = :refType "
	        		+ "AND BC.COMMENT_STATUS = 'Published' AND  BC.CREATED_TIME <= :endTime AND BC.CREATED_TIME >= :startTime LIMIT :offset, :limit";
	        SQLQuery sqlCommentsQuery = getSession().createSQLQuery(finalSql);
	        sqlCommentsQuery.setParameter("refType", refType);
	        sqlCommentsQuery.setParameter("locale", locale);
	        sqlCommentsQuery.setParameter("startTime", startTime);
	        sqlCommentsQuery.setParameter("endTime", endTime);
	        sqlCommentsQuery.setParameter("offset", offset);
	        sqlCommentsQuery.setParameter("limit", limit);
	        List<Object> objectsList = sqlCommentsQuery.list();
	        if(!objectsList.isEmpty()){
		    	for(Object obj : objectsList){
		        	Comment comment = new Comment();
		        	Object[] row = (Object[]) obj;
		        	comment.setCommentText((row[0]==null)?null:String.valueOf(row[0].toString()));
		        	comment.setCreatorId((row[1]==null)?null:Long.valueOf(row[1].toString()));
		        	commentsList.add(comment);
		    	}
		    	offset += limit;
	    	}
	    	if(objectsList.size() < limit)
	    		break;
    	}while(true);
		return commentsList;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Comment> getAllSubCommentsByDate(String refType, String locale, Date startTime, Date endTime) {
		List<Comment> subCommentsList = new LinkedList<Comment>();
		int offset = 0;
		int limit = 100;
		do{
	        String finalSql = "SELECT BC.COMMENT_TEXT, BC.USER_ID FROM BC_COMMENT BC "
	        		+ "JOIN BC_COMMENT BC2 ON BC.REF_ID = BC2.ID JOIN BC_POST BP ON BC2.REF_ID = BP.ID "
	        		+ "WHERE BP.IS_DELETED = FALSE AND BP.LOCALE = :locale AND BC.IS_DELETED = FALSE "
	        		+ "AND BC2.IS_DELETED = FALSE AND BC.REF_TYPE = :refType AND BC.COMMENT_STATUS = 'Published' "
	        		+ "AND  BC.CREATED_TIME <= :endTime AND BC.CREATED_TIME >= :startTime LIMIT :offset, :limit";
	        SQLQuery sqlCommentsQuery = getSession().createSQLQuery(finalSql);
	        sqlCommentsQuery.setParameter("refType", refType);
	        sqlCommentsQuery.setParameter("locale", locale);
	        sqlCommentsQuery.setParameter("startTime", startTime);
	        sqlCommentsQuery.setParameter("endTime", endTime);
	        sqlCommentsQuery.setParameter("offset", offset);
	        sqlCommentsQuery.setParameter("limit", limit);
	        List<Object> objectsList = sqlCommentsQuery.list();
	        if(!objectsList.isEmpty()){
		    	for(Object obj : objectsList){
		        	Comment comment = new Comment();
		        	Object[] row = (Object[]) obj;
		        	comment.setCommentText((row[0]==null)?null:String.valueOf(row[0].toString()));
		        	comment.setCreatorId((row[1]==null)?null:Long.valueOf(row[1].toString()));
		        	subCommentsList.add(comment);
		    	}
		    	offset += limit;
	        }
	    	if(objectsList.size() < limit)
	    		break;
    	}while(true);
		return subCommentsList;
	}
}
