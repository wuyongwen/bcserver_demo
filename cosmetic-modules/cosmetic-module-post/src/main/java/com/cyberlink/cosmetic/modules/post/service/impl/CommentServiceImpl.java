package com.cyberlink.cosmetic.modules.post.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.event.post.CommentCreateEvent;
import com.cyberlink.cosmetic.event.post.CommentDeleteEvent;
import com.cyberlink.cosmetic.event.post.CommentStatusChangeEvent;
import com.cyberlink.cosmetic.modules.post.dao.CommentDao;
import com.cyberlink.cosmetic.modules.post.dao.CommentTagDao;
import com.cyberlink.cosmetic.modules.post.dao.LikeDao;
import com.cyberlink.cosmetic.modules.post.dao.PostAttributeDao;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.dao.PostReportedDao;
import com.cyberlink.cosmetic.modules.post.dao.PostViewDao;
import com.cyberlink.cosmetic.modules.post.event.PostViewUpdateEvent;
import com.cyberlink.cosmetic.modules.post.model.Comment;
import com.cyberlink.cosmetic.modules.post.model.Comment.CommentStatus;
import com.cyberlink.cosmetic.modules.post.model.CommentTag;
import com.cyberlink.cosmetic.modules.post.model.CommentTags;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute;
import com.cyberlink.cosmetic.modules.post.model.PostReported;
import com.cyberlink.cosmetic.modules.post.model.PostTargetType;
import com.cyberlink.cosmetic.modules.post.model.PostView;
import com.cyberlink.cosmetic.modules.post.model.PostViewAttr;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetType;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute.PostAttrType;
import com.cyberlink.cosmetic.modules.post.model.PostReported.PostReportedResult;
import com.cyberlink.cosmetic.modules.post.model.PostReported.PostReportedStatus;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.service.CommentService;
import com.cyberlink.cosmetic.modules.user.dao.UserBlockedDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserBlocked;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public class CommentServiceImpl extends AbstractService implements CommentService {

    private CommentDao commentDao;
    private PostDao postDao;
    private CommentTagDao commentTagDao;
    private PostReportedDao postReportedDao;
    private UserDao userDao;
    private PostAttributeDao postAttributeDao;
    private PostViewDao postViewDao;
    private ObjectMapper objectMapper;
    private LikeDao likeDao;
    private UserBlockedDao userBlockedDao;
    
    public LikeDao getLikeDao() {
		return likeDao;
	}

	public void setLikeDao(LikeDao likeDao) {
		this.likeDao = likeDao;
	}

	public void setPostAttributeDao(PostAttributeDao postAttributeDao) {
        this.postAttributeDao = postAttributeDao;
    }
    
	public void setPostViewDao(PostViewDao postViewDao) {
        this.postViewDao = postViewDao;
    }
	
    public void setCommentDao(CommentDao commentDao) {
        this.commentDao = commentDao;
    }
    
    public void setPostDao(PostDao postDao) {
        this.postDao = postDao;
    }
    
    public void setCommentTagDao(CommentTagDao commentTagDao) {
        this.commentTagDao = commentTagDao;
    }
    
    public void setPostReportedDao(PostReportedDao postReportedDao) {
        this.postReportedDao = postReportedDao;
    }
    
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    public void setUserBlockedDao(UserBlockedDao userBlockedDao) {
		this.userBlockedDao = userBlockedDao;
	}

	private CommentTags transferTags(String tags) throws InvalidFormatException {
        CommentTags result = new CommentTags();        
        if(tags == null)
            return result;
        try {
            result = objectMapper.readValue(tags,
                    new TypeReference<CommentTags>() {
                    });
        } catch (Exception e) {
            logger.error("", e);
            if(tags.length() == 0)
                return result;
            else
                throw new InvalidFormatException(tags, e, null);
        }
        return result;
    }
    
    private void updatePostViewCommentCount(Long postId, Long count) {
        if(!Constants.getIsPostCacheView())
            return;
        PostView postView = postViewDao.findByPostId(postId);
        if(postView == null)
            return;
        PostViewAttr postAttr = postView.getAttribute();
        if(postAttr == null)
            postAttr = new PostViewAttr();
        if(postAttr.getCommentCount() != null && postAttr.getCommentCount().equals(count))
            return;
        postAttr.setCommentCount(count);
        postView.setAttribute(postAttr);
        postViewDao.update(postView);
        publishDurableEvent(new PostViewUpdateEvent(postId, postAttr));
    }
    
    private void updateCommentCount(String refType, Long refId, Long refCommentCount) {
        Long commentCount = refCommentCount;
        if(commentCount == null)
            commentCount = commentDao.getCommentCount(refType, refId);    
        updatePostViewCommentCount(refId, commentCount);
        PostAttribute postAttr = postAttributeDao.findByTarget(refType, refId, PostAttrType.PostCommentCount);
        if(postAttr != null) {
            if(postAttr.getAttrValue().equals(commentCount))
                return;
            postAttr.setAttrValue(commentCount);
            postAttributeDao.update(postAttr);
            return;
        }
        
        try {
            postAttr = new PostAttribute();
            postAttr.setRefId(refId);
            postAttr.setRefType("Post");
            postAttr.setAttrType(PostAttrType.PostCommentCount);
            postAttr.setAttrValue(commentCount);
            postAttributeDao.create(postAttr);
        }
        catch(Exception ex) {
            
        }
    }
    
    @Override
    public PostApiResult <Comment> createComment(Long creatorId, String targetType, Long targetId, String comment, String jTags) {
        PostApiResult <Comment> result = new PostApiResult<Comment>();
        if(!userDao.exists(creatorId)) {
            result.setErrorDef(ErrorDef.InvalidUserId);
            return result;
        }

        Post post = null;
		if (targetType.equals(PostTargetType.POST)) {
			if (!postDao.exists(targetId)) {
				result.setErrorDef(ErrorDef.InvalidPostTargetId);
				return result;
			}
			post = postDao.findById(targetId);
		} 
		else if (targetType.equals(PostTargetType.COMMENT)) {
			if (!commentDao.exists(targetId)) {
				result.setErrorDef(ErrorDef.InvalidPostTargetId);
				return result;
			}
			Comment parentComment = commentDao.findById(targetId);
			post = postDao.findById(parentComment.getRefId());
		} 
		else {
			result.setErrorDef(ErrorDef.InvalidPostTargetType);
			return result;
		}
		UserBlocked blocked = userBlockedDao.findByTargetAndCreater(post.getCreatorId(), creatorId);
		if (blocked != null && !blocked.getIsDeleted()) {
			result.setErrorDef(ErrorDef.BlockedTheUser);
			return result;
		}
		UserBlocked beblocked = userBlockedDao.findByTargetAndCreater(creatorId, post.getCreatorId());
		if (beblocked != null && !beblocked.getIsDeleted()) {
			result.setErrorDef(ErrorDef.BlockedByUser);
			return result;
		}
		
        if(comment == null || comment.length() <= 0) {
            result.setErrorDef(ErrorDef.InvalidPostComment);
            return result;
        }
        
        Comment newComment = null;
        Comment resultComment = null;
        User creator = userDao.findById(creatorId);
        newComment = new Comment();
        newComment.setShardId(creatorId);
        newComment.setCreator(creator);
        newComment.setRefType(targetType);
        newComment.setRefId(targetId);
        newComment.setCommentText(comment);
        newComment.setCommentStatus(CommentStatus.Published);
        resultComment = commentDao.create(newComment);
        if(resultComment == null) {
            result.setErrorDef(ErrorDef.UnknownPostError);
            return result;
        }
        result.setResult(resultComment);
        
		if (targetType.equals(PostTargetType.POST)) {
			publishDurableEvent(new CommentCreateEvent(resultComment.getId(), targetId, creator.getId()));
			updateCommentCount(PostTargetType.POST, targetId, null);
		} 
		else if (targetType.equals(PostTargetType.COMMENT)) {
			commentDao.updateCommentAttr(targetId, resultComment.getId(), 1);
			publishDurableEvent(new CommentCreateEvent(resultComment.getId(), targetId, creator.getId()));
		}
        
        CommentTags tags = null;
        try {
            tags = transferTags(jTags);
            for(Long receiverId : tags.receiverTags) {
                CommentTag cTag = new CommentTag();
                cTag.setShardId(creatorId);
                cTag.setCommentId(resultComment.getId());
                User receiver = userDao.findById(receiverId);
                if(receiver != null) {
                    cTag.setTagTarget(receiver);           
                    commentTagDao.create(cTag);
                }
            }
            
        } catch (InvalidFormatException e) {
            result.setErrorDef(ErrorDef.InvalidPostJsonFormat);
            return result;
        }
            
        return result;
    }
    
    @Override
    public PostApiResult <Comment> updateComment(Long userId, Long commentId, String comment, String jTags) {
        PostApiResult <Comment> result = new PostApiResult<Comment>();
        if(!userDao.exists(userId)) {
            result.setErrorDef(ErrorDef.InvalidUserId);
            return result;
        }
        
        if(!commentDao.exists(commentId)) {
            result.setErrorDef(ErrorDef.InvalidPostTargetId);
            return result;
        }
        
        if(comment == null || comment.length() <= 0) {
            result.setErrorDef(ErrorDef.InvalidPostComment);
            return result;
        }
        
        Comment toUpdateComment = commentDao.findById(commentId);
        User commentCreator = toUpdateComment.getCreator();
        if(!userId.equals(commentCreator.getId())) {
            result.setErrorDef(ErrorDef.InvalidPostNotAuth);
            return result;
        }
        
		String targetType = toUpdateComment.getRefType();
		Long targetId = toUpdateComment.getRefId();
		Post post = null;
		if (targetType.equals(PostTargetType.POST)) {
			if (!postDao.exists(targetId)) {
				result.setErrorDef(ErrorDef.InvalidPostTargetId);
				return result;
			}
			post = postDao.findById(targetId);
		} 
		else if (targetType.equals(PostTargetType.COMMENT)) {
			if (!commentDao.exists(targetId)) {
				result.setErrorDef(ErrorDef.InvalidPostTargetId);
				return result;
			}
			Comment parentComment = commentDao.findById(targetId);
			post = postDao.findById(parentComment.getRefId());
		}
		else {
			result.setErrorDef(ErrorDef.InvalidPostTargetType);
			return result;
		}
		UserBlocked blocked = userBlockedDao.findByTargetAndCreater(post.getCreatorId(), userId);
		if (blocked != null && !blocked.getIsDeleted()) {
			result.setErrorDef(ErrorDef.BlockedTheUser);
			return result;
		}
		UserBlocked beblocked = userBlockedDao.findByTargetAndCreater(userId, post.getCreatorId());
		if (beblocked != null && !beblocked.getIsDeleted()) {
			result.setErrorDef(ErrorDef.BlockedByUser);
			return result;
		}
		
        Comment resultComment = null;
        if(comment != null && comment.length() > 0) {
            toUpdateComment.setCommentText(comment);
            resultComment = commentDao.update(toUpdateComment);
        }
        
        result.setResult(resultComment);
        CommentTags tags = null;
        if(jTags != null && jTags.length() > 0) {
            try {
                tags = transferTags(jTags);                
            } catch (InvalidFormatException e) {
                result.setErrorDef(ErrorDef.InvalidPostJsonFormat);
                return result;
            }
        }
        
        if(tags == null)
            return result;
        
        Set<Long> newReceiverId = tags.receiverTags;
        if(newReceiverId.size() <= 0)
            return result;
        
        Long [] commentIds = new Long[1];
        commentIds[0] = commentId;
        List<CommentTag> exCommentTags = commentTagDao.listCommentTag(commentIds);
        for(CommentTag exCt : exCommentTags) {
            Object target = exCt.getTagTarget();
            if (!(target instanceof User))
                continue;
            User receiver = (User)target;
            if(newReceiverId.contains(receiver.getId()))
                newReceiverId.remove(receiver.getId());
            else {
                exCt.setIsDeleted(true);
                commentTagDao.update(exCt);
            }
        }

        for(Long receiverId : newReceiverId) {
            CommentTag cTag = new CommentTag();
            cTag.setShardId(userId);
            cTag.setCommentId(resultComment.getId());
            User receiver = userDao.findById(receiverId);
            if(receiver != null) {
                cTag.setTagTarget(receiver);           
                commentTagDao.create(cTag);
            }
        }
        return result;
    }
    
    @Override
    public PostApiResult <Boolean> deleteComment(Long userId, Long commentId) {
        PostApiResult <Boolean> result = new PostApiResult <Boolean> ();
        result.setResult(false);
        
        if(!commentDao.exists(commentId)) {
            result.setErrorDef(ErrorDef.InvalidPostTargetId);
            return result;
        }
        
        if(!userDao.exists(userId)) {
            result.setErrorDef(ErrorDef.InvalidUserId);
            return result;
        }
        
        Comment toDelComment = commentDao.findById(commentId);
        Long commentCreatorId = toDelComment.getCreator().getId();
        String targetType = toDelComment.getRefType();
        Long targetId = toDelComment.getRefId();
        Post post = null;
        Long targetOwnerId = null;
        Comment parentComment = null;
        if(targetType.equals(PostTargetType.POST)) {
        	post = postDao.findById(targetId);
        }
        else if (targetType.equals(PostTargetType.COMMENT)) {
        	parentComment = commentDao.findById(targetId);
			post = postDao.findById(parentComment.getRefId()); 
        }
        
        if (post.getParentId() != null) {
        	Post parentPost = postDao.findById(post.getParentId());
        	targetOwnerId = parentPost.getCreator().getId();
        }
        else {
            targetOwnerId = post.getCreator().getId();
        }
        if(!userId.equals(commentCreatorId) && !userId.equals(targetOwnerId)) {
            result.setErrorDef(ErrorDef.InvalidPostNotAuth);
            return result;
        }
        
        toDelComment.setIsDeleted(true);
        commentDao.update(toDelComment);

		if (targetType.equals(PostTargetType.POST)) {
			publishDurableEvent(new CommentDeleteEvent(commentId, targetId, userId));
			updateCommentCount(PostTargetType.POST, targetId, null);
			commentDao.deleteAllSubComments(PostTargetType.COMMENT, commentId);
		} 
		else if (targetType.equals(PostTargetType.COMMENT)) {
			if (toDelComment.getId().equals(parentComment.getLatestSubCommentId())) {
				Long latestSubCommentId = commentDao.findLatestCommentId(PostTargetType.COMMENT, targetId);
				commentDao.updateCommentAttr(targetId, latestSubCommentId, -1);
			} else
				commentDao.updateCommentAttr(targetId, (long)-1, -1);
			publishDurableEvent(new CommentDeleteEvent(commentId, targetId, userId));
		}
		
		likeDao.updateByDeletePost(TargetType.Comment, commentId);
		result.setResult(true);
        return result;
    }
    
    @Override
    public void deleteComments(Long userId, String refType, List<Comment> comments) {        
        for(Comment toDelComment : comments) {
            Long targetId = toDelComment.getRefId();
            Comment parentComment = null;
            if (refType.equals(PostTargetType.COMMENT)) {
                parentComment = commentDao.findById(targetId); 
            }
            
            toDelComment.setIsDeleted(true);
            commentDao.update(toDelComment);

            Long commentId = toDelComment.getId();
            if (refType.equals(PostTargetType.POST)) {
                publishDurableEvent(new CommentDeleteEvent(commentId, targetId, userId));
                updateCommentCount(PostTargetType.POST, targetId, null);
                commentDao.deleteAllSubComments(PostTargetType.COMMENT, commentId);
            } 
            else if (refType.equals(PostTargetType.COMMENT)) {
                if (toDelComment.getId().equals(parentComment.getLatestSubCommentId())) {
                    Long latestSubCommentId = commentDao.findLatestCommentId(PostTargetType.COMMENT, targetId);
                    commentDao.updateCommentAttr(targetId, latestSubCommentId, -1);
                } else
                    commentDao.updateCommentAttr(targetId, (long)-1, -1);
            }
            
            likeDao.updateByDeletePost(TargetType.Comment, commentId);
        }
    }
    
    @Override
    public PostApiResult <PageResult<Comment>> listComment(String targetType, Long targetId, BlockLimit blockLimit) {
		PostApiResult<PageResult<Comment>> result = new PostApiResult<PageResult<Comment>>();
		PageResult<Comment> list = new PageResult<Comment>();
		if (targetType.equals(PostTargetType.POST)) {
			if (!postDao.exists(targetId)) {
				result.setErrorDef(ErrorDef.InvalidPostTargetId);
				return result;
			}
			list = commentDao.blockQuery(PostTargetType.POST, targetId, blockLimit);
		} 
		else if (targetType.equals(PostTargetType.COMMENT)) {
			if (!commentDao.exists(targetId)) {
				result.setErrorDef(ErrorDef.InvalidPostTargetId);
				return result;
			}
			list = commentDao.blockQuery(PostTargetType.COMMENT, targetId, blockLimit);
		} 
		else {
			result.setErrorDef(ErrorDef.InvalidPostTargetType);
			return result;
		}

		if (list == null) {
			result.setErrorDef(ErrorDef.UnknownPostError);
			return result;
		}
		result.setResult(list);
		return result;
    }
    
    @Override
    public Map<Long, List<CommentTag> > checkCommentReceiver(List<Long> commentIds) {
        Map<Long, List<CommentTag> > resultMap = new HashMap<Long, List<CommentTag> >();
        List<CommentTag> commentTags = commentTagDao.listCommentTag(commentIds.toArray(new Long[commentIds.size()]));
        for(CommentTag cT : commentTags) {
            if(resultMap.containsKey(cT.getCommentId())) {
                resultMap.get(cT.getCommentId()).add(cT);
            }
            else{
                List<CommentTag> receiverList = new ArrayList<CommentTag>(0);
                receiverList.add(cT);
                resultMap.put(cT.getCommentId(), receiverList);
            }
        }
        return resultMap;
    }
    
    @Override
    public Map<Long, Long> checkCommentCount(String refType, List<Long> targetIds) {
        Map<Long, Long> resultMap = new HashMap<Long, Long>();
        if(targetIds.size() > 0) {
            Long [] ids = targetIds.toArray(new Long[targetIds.size()]);
            resultMap = postAttributeDao.checkPostAttriButeByIds("Post", PostAttrType.PostCommentCount, ids);
        }
        return resultMap;
    }
    
    @Override
    public PostApiResult <Boolean> reportComment(Long reportedId, Long commentId, String reason) {
        PostApiResult <Boolean> result = new PostApiResult <Boolean> ();
        result.setResult(false);
        
        if(!userDao.exists(reportedId)) {
            result.setErrorDef(ErrorDef.InvalidUserId);
            return result;
        }
        if(!commentDao.exists(commentId)) {
            result.setErrorDef(ErrorDef.InvalidPostTargetId);
            return result;
        }
        if(reason == null || reason.length() <= 0) {
            result.setErrorDef(ErrorDef.InvalidPostReportReason);
            return result;
        }
        
        PostReported.PostReportedReason reasonE = null;
        switch(reason)
        {
        case "Inappropriate":
            reasonE = PostReported.PostReportedReason.Inappropriate;
            break;
        case "Copyright":
            reasonE = PostReported.PostReportedReason.Copyright;
            break;
        case "Other":
            reasonE = PostReported.PostReportedReason.Other;
            break;
        default:
            break;
        }
        
        if(reasonE == null) {
            result.setErrorDef(ErrorDef.InvalidPostReportReason);
            return result;
        }
        
        User reporter = userDao.findById(reportedId);
        Comment relComment = commentDao.findById(commentId);
        BlockLimit blockLimit = new BlockLimit(0, 1);
        PageResult<PostReported> exReportedComment = postReportedDao.findByTargetAndUser(relComment, reporter, blockLimit);
        if(exReportedComment.getTotalSize() > 0) {
            result.setResult(true);
            return result;
        }
        
        PostReported reportedPost = new PostReported();
        reportedPost.setReporter(reporter);
        reportedPost.setTarget(relComment);
        reportedPost.setReason(reasonE);
        reportedPost.setStatus(PostReportedStatus.NewReported);
        PostReported newReportedPost = postReportedDao.create(reportedPost);
        if(newReportedPost == null) {
            result.setErrorDef(ErrorDef.UnknownPostError);
            return result;
        }
        
        result.setResult(true);
        relComment.setCommentStatus(CommentStatus.Reviewing);
        commentDao.update(relComment);

        Long targetId = relComment.getRefId();
        Comment parentComment = commentDao.findById(targetId);
		if (relComment.getRefType().equals(PostTargetType.POST)) {
			updateCommentCount(relComment.getRefType(), relComment.getRefId(), null);
			publishDurableEvent(new CommentStatusChangeEvent(relComment.getId(), relComment.getRefId(),
					relComment.getCreatorId(), relComment.getCommentStatus().name()));
		} 
		else if (relComment.getRefType().equals(PostTargetType.COMMENT)) {
			if (relComment.getId().equals(parentComment.getLatestSubCommentId())) {
				Long latestSubCommentId = commentDao.findLatestCommentId(PostTargetType.COMMENT, targetId);
				commentDao.updateCommentAttr(targetId, latestSubCommentId, -1);
			} else
				commentDao.updateCommentAttr(targetId, (long)-1, -1);
		}
        return result;
    }
    
    @Override
    public Map<Long, List<PostReported>> getReportedCommentReason(PostReportedStatus status, List<Long> commentIds) {
        Map<Long, List<PostReported>> result = new HashMap<Long, List<PostReported>>();
        if(commentIds == null || commentIds.size() <= 0)
            return result;
        
        List<PostReported> reportedPosts = postReportedDao.getByTargets("Comment", status, commentIds.toArray(new Long[commentIds.size()]));
        for(PostReported c : reportedPosts) {
            Comment relComment = (Comment)c.getTarget();
            if(result.get(relComment.getId()) == null)
                result.put(relComment.getId(), new ArrayList<PostReported>());
            result.get(relComment.getId()).add(c);
        }
        return result;
    }
    
    @Override
    public Boolean handleReportComment(Long commentId, User reviewer, String result, String remark) {
        if(!commentDao.exists(commentId))
            return false;
        
        Comment relComment = commentDao.findById(commentId);
        PostReportedResult pResult = null;
        CommentStatus cStatus = null;
        switch(result)
        {
            case "Published" :
                pResult = PostReportedResult.Published;
                cStatus = CommentStatus.Published;
                break;
            case "Banned" :
                pResult = PostReportedResult.Banned;
                cStatus = CommentStatus.Banned;
                break;
            default:
                break;
        }
        if(pResult == null)
            return false;
        
        relComment.setCommentStatus(cStatus);
        commentDao.update(relComment);
        
        Long targetId = relComment.getRefId();
        int offset = 0;
        int limit = 10;
        do
        {
            BlockLimit blockLimit = new BlockLimit(offset, limit);
            PageResult<PostReported> prs = postReportedDao.findByTarget(relComment, blockLimit);
            for(PostReported pr : prs.getResults()) {
                if(remark != null && remark.length() > 0)
                    pr.setRemark(remark);
                pr.setResult(pResult);
                pr.setStatus(PostReportedStatus.Reviewed);
                pr.setReviewer(reviewer);
                postReportedDao.update(pr);
            }
            offset += limit;
            if(offset > prs.getTotalSize())
                break;
        } while(true);
		if (relComment.getRefType().equals(PostTargetType.POST)) {
			updateCommentCount(relComment.getRefType(), relComment.getRefId(), null);
			publishDurableEvent(new CommentStatusChangeEvent(relComment.getId(), relComment.getRefId(),
					relComment.getCreatorId(), relComment.getCommentStatus().name()));
		} 
		else if (relComment.getRefType().equals(PostTargetType.COMMENT)) {
			if(relComment.getCommentStatus().equals(CommentStatus.Banned))
				return true;
			
			Long latestSubCommentId = commentDao.findLatestCommentId(PostTargetType.COMMENT, targetId);
			if (relComment.getId().equals(latestSubCommentId))
				commentDao.updateCommentAttr(targetId, latestSubCommentId, 1);
			else
				commentDao.updateCommentAttr(targetId, (long)-1, 1);
		}
        return true;
    }
    
    @Override
    public Map<Long, Long> checkCommentCountWithDate(String refType, Date startTime, Date endTime, List<Long> targetIds) {
        Map<Long, Long> resultMap = new HashMap<Long, Long>();
        if(targetIds.size() > 0) {
            Long [] ids = targetIds.toArray(new Long[targetIds.size()]);
            resultMap= commentDao.getCommentCountByTargetsWithDate(refType, startTime, endTime, ids);
        }
        return resultMap;
    }
    
    @Override
    public Map<Long, Map<String, Long>> checkCommentRegionCountWithDate(String refType, Date startTime, Date endTime, List<Long> targetIds) {
        Map<Long, Map<String, Long>> resultMap = new HashMap<Long, Map<String, Long>>();
        if(targetIds.size() > 0) {
            Long [] ids = targetIds.toArray(new Long[targetIds.size()]);
            resultMap= commentDao.getCommentRegionCountByTargetsWithDate(refType, startTime, endTime, ids);
        }
        return resultMap;
    }
    
    @Override
    public List<Comment> findPostByIds(List<Long>ids) {
        return commentDao.findByIds(ids.toArray(new Long[ids.size()]));
    }
    
}
