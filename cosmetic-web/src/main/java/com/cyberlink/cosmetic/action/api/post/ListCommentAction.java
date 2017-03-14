package com.cyberlink.cosmetic.action.api.post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.post.dao.CommentDao;
import com.cyberlink.cosmetic.modules.post.model.Comment;
import com.cyberlink.cosmetic.modules.post.model.CommentTag;
import com.cyberlink.cosmetic.modules.post.model.PostTargetType;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetType;
import com.cyberlink.cosmetic.modules.post.result.CommentDetailWrapper;
import com.cyberlink.cosmetic.modules.post.result.SubCommentDetailWrapper;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.service.CommentService;
import com.cyberlink.cosmetic.modules.post.service.LikeService;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;

@UrlBinding("/api/post/list-comment.action")
public class ListCommentAction extends AbstractAction {
    @SpringBean("post.PostService")
    private PostService postService;

    @SpringBean("post.CommentService")
    private CommentService commentService;
    
    @SpringBean("post.CommentDao")
    private CommentDao commentDao;
    
    @SpringBean("post.LikeService")
    private LikeService likeService;
    
    @SpringBean("user.SessionDao")
    private SessionDao sessionDao;
    
    @SpringBean("user.SubscribeDao")
    private SubscribeDao subscribeDao;
    
    private String token = "";
    private Long curUserId = null;
    private Long targetId;
    private String targetType;
    private Integer offset = 0;
    private Integer limit = 10;
    
    @Validate(required = true, on = "route")
    public void setTargetId(Long refId) {
        this.targetId = refId;
    }
    
    @Validate(required = true, on = "route")
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
    
    @Validate(minvalue = 0, required = false, on = "route")
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    @Validate(minvalue = 1, maxvalue = 20, required = false, on = "route")
    public void setLimit(Integer limit) {
        this.limit = limit;
    }
    
    public void setToken(String token) {
        super.setToken(token);
        this.token = token;
    }
    
    public void setCurUserId(Long curUserId) {
        this.curUserId = curUserId;
    }
    
    private CommentDetailWrapper listCommentsWithUserId(String targetType, Comment c, Map<Long, Long> commentLikedCount, Map<Long, Comment> subCommentsMap, Map<Long, List<CommentTag>> receiverTags, List<Long> likedComment, Set<Long> subcribeeIds){
    	if (targetType.equals(PostTargetType.POST)) {
			CommentDetailWrapper cfw = new CommentDetailWrapper(c, receiverTags.get(c.getId()));
			if (likedComment.contains(c.getId()))
				cfw.setIsLiked(true);
			if (commentLikedCount.containsKey(c.getId()))
				cfw.setLikeCount(commentLikedCount.get(c.getId()));
			if (subcribeeIds.contains(c.getCreatorId()))
				cfw.getCreator().setIsFollowed(true);
			if (subCommentsMap.containsKey(c.getId())) { // with sub comment
				Comment latestSubComment = subCommentsMap.get(c.getId());
				SubCommentDetailWrapper subCfw = new SubCommentDetailWrapper(latestSubComment);
				if (likedComment.contains(latestSubComment.getId()))
					subCfw.setIsLiked(true);
				if (commentLikedCount.containsKey(latestSubComment.getId()))
					subCfw.setLikeCount(commentLikedCount.get(latestSubComment.getId()));
				if (subcribeeIds.contains(latestSubComment.getCreatorId()))
					subCfw.getCreator().setIsFollowed(true);
				cfw.setLatestSubComment(subCfw);
			}
			return cfw;
		} else if (targetType.equals(PostTargetType.COMMENT)) {
			SubCommentDetailWrapper subCfw = new SubCommentDetailWrapper(c);
			if (likedComment.contains(c.getId()))
				subCfw.setIsLiked(true);
			if (commentLikedCount.containsKey(c.getId()))
				subCfw.setLikeCount(commentLikedCount.get(c.getId()));
			if (subcribeeIds.contains(c.getCreatorId()))
				subCfw.getCreator().setIsFollowed(true);
			return subCfw;
		}
    	return null;
    }
    
    private CommentDetailWrapper listCommentsWithoutUserId(String targetType, Comment c, Map<Long, Long> commentLikedCount, Map<Long, Comment> subCommentsMap, Map<Long, List<CommentTag>> receiverTags){
    	if (targetType.equals(PostTargetType.POST)) {
			CommentDetailWrapper cfw = new CommentDetailWrapper(c, receiverTags.get(c.getId()));
			if (commentLikedCount.containsKey(c.getId()))
				cfw.setLikeCount(commentLikedCount.get(c.getId()));
			if (subCommentsMap.containsKey(c.getId())) { // with sub comment
				Comment latestSubComment = subCommentsMap.get(c.getId());
				SubCommentDetailWrapper subCfw = new SubCommentDetailWrapper(latestSubComment);
				cfw.setLatestSubComment(subCfw);
			}
			return cfw;
		} else if (targetType.equals(PostTargetType.COMMENT)) {
			SubCommentDetailWrapper subCfw = new SubCommentDetailWrapper(c);
			if (commentLikedCount.containsKey(c.getId()))
				subCfw.setLikeCount(commentLikedCount.get(c.getId()));
			return subCfw;
		}
		return null;
    }
    
	private Map<String, Object> listComments(Long userId, Comment mainComment, List<Long> commentIds,
			Set<Long> creatorIds, PageResult<Comment> comments, Map<Long, List<CommentTag>> receiverTags,
			Map<Long, Long> commentLikedCount, Map<Long, Comment> subCommentsMap) {
		
		final Map<String, Object> result = new HashMap<String, Object>();
		List<CommentDetailWrapper> r = new ArrayList<CommentDetailWrapper>();
		result.put("totalSize", comments.getTotalSize());
		
		if (userId == null) {
			for (int idx = 0; idx < comments.getResults().size(); idx++) {
				Comment c = comments.getResults().get(idx);
				r.add(listCommentsWithoutUserId(targetType, c, commentLikedCount, subCommentsMap, receiverTags));
			}
			if (mainComment != null) {
				CommentDetailWrapper rMain = listCommentsWithoutUserId("Post", mainComment, commentLikedCount, subCommentsMap, receiverTags);
				result.put("mainComment", rMain);
			}
			result.put("results", r);
			return result;
		}
		
		List<Long> likedComment = likeService.getLikeTarget(userId, TargetType.Comment, commentIds);
		Set<Long> subcribeeIds = subscribeDao.findIdBySubscriberAndSubscribees(userId, SubscribeType.User, creatorIds.toArray(new Long[creatorIds.size()]));
		for (int idx = 0; idx < comments.getResults().size(); idx++) {
			Comment c = comments.getResults().get(idx);
			c.getCreator().setCurUserId(userId);
			r.add(listCommentsWithUserId(targetType, c, commentLikedCount, subCommentsMap, receiverTags, likedComment, subcribeeIds));
		}
		if (mainComment != null) {
			CommentDetailWrapper rMain = listCommentsWithUserId("Post", mainComment, commentLikedCount, subCommentsMap, receiverTags, likedComment, subcribeeIds);
			result.put("mainComment", rMain);
		}
		result.put("results", r);
		return result;
	}
    
    @DefaultHandler
    public Resolution route() {
        BlockLimit blockLimit = new BlockLimit(offset, limit);
        blockLimit.addOrderBy("createdTime", false);
        final PostApiResult <PageResult<Comment>> result = commentService.listComment(targetType, targetId, blockLimit);
        if(!result.success())
            return new ErrorResolution(result.getErrorDef());
        
        PageResult<Comment> comments = result.getResult();
        final PageResult<CommentDetailWrapper> r = new PageResult<CommentDetailWrapper>();
        r.setTotalSize(comments.getTotalSize());
        
        if(comments.getTotalSize() <= 0)
            return json(r);
        
        List<Long> commentIds = new ArrayList<Long>(0);
        Set<Long> creatorIds = new HashSet<Long>();
        List<Long> subCommentIds = new ArrayList<Long>();
        for(Comment c : comments.getResults()) {
            commentIds.add(c.getId());
            creatorIds.add(c.getCreator().getId());
			if (c.getLatestSubCommentId() != null)
				subCommentIds.add(c.getLatestSubCommentId());
        }
        Comment mainComment = null;
        if(targetType.equals(PostTargetType.COMMENT)){
        	mainComment = commentDao.findById(targetId);
        	commentIds.add(mainComment.getId());
        	creatorIds.add(mainComment.getCreatorId());
        	subCommentIds.add(mainComment.getLatestSubCommentId());
        }
		List<Comment> subComments = commentDao.findByIds(subCommentIds.toArray(new Long[subCommentIds.size()]));
		Map<Long, Comment> subCommentsMap = new HashMap<Long, Comment>();
		for (Comment subc : subComments) {
			commentIds.add(subc.getId());
			creatorIds.add(subc.getCreatorId());
			subCommentsMap.put(subc.getRefId(), subc);
		}

        Map<Long, Long> commentLikedCount = likeService.checkLikeCount(PostTargetType.COMMENT, commentIds);
        //Map<Long, List<CommentTag> > receiverTags = commentService.checkCommentReceiver(commentIds); //receiverTags is not used now
        Map<Long, List<CommentTag> > receiverTags = new HashMap<Long, List<CommentTag> >();
        if(curUserId != null) {
        	return json(listComments(curUserId, mainComment, commentIds, creatorIds , comments, receiverTags, commentLikedCount, subCommentsMap));
        }
        else if(token.length() > 0) {
            if(!authenticate())
                return new ErrorResolution(authError); 
            Long userId = getCurrentUserId();
            return json(listComments(userId, mainComment, commentIds, creatorIds , comments, receiverTags, commentLikedCount, subCommentsMap));
        }
        else {
        	return json(listComments(null, mainComment, null, null , comments, receiverTags, commentLikedCount, subCommentsMap));
        }
    }
}
