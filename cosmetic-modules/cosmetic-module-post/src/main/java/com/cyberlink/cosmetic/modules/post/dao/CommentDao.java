package com.cyberlink.cosmetic.modules.post.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.model.Comment;

public interface CommentDao extends GenericDao<Comment, Long>{
    
    Long getCommentCount(String refType, Long refId);
	void updateCommentAttr(Long refId, Long subCommentId, Integer diff);
	Long findLatestCommentId(String refType, Long refId);
	void deleteAllSubComments(String refType, Long refId);
    PageResult<Comment> blockQuery(String refType, Long refId, BlockLimit blockLimit);
    Map<Long, Long> getCommentCountByTargets(String refType, Long... refIds);
    Map<Long, Long> getCommentCountByTargetsWithoutEmpty(String refType, List<Long> refIds);
    PageResult<Comment> findByDate(Date start, Date end, BlockLimit blockLimit);
    Map<Long, Long> getCommentCountByTargetsWithDate(String refType, Date startTime, Date endTime, Long... refIds);
    PageResult<Comment> findAllActiveComment(String start, String end, BlockLimit blockLimit);
    Map<Long, Map<String, Long>> getCommentRegionCountByTargetsWithDate(String refType, Date startTime, Date endTime, Long... refIds);
    List<Comment> findAllActiveCommentByTarget(String start, String end, List<Long> refIds);
    List<Comment> findByIds(Long... ids);
    PageResult<Comment> findByUserId(Long userId, String refType, BlockLimit blockLimit);
    PageResult<Comment> getOlderComment(String refType, Long refId, Long commentId, BlockLimit blockLimit);
    List<Comment> getAllCommentsByDate(String refType, String locale, Date startTime, Date endTime);
    List<Comment> getAllSubCommentsByDate(String refType, String locale, Date startTime, Date endTime);
}