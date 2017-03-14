package com.cyberlink.cosmetic.modules.post.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.model.Comment;
import com.cyberlink.cosmetic.modules.post.model.CommentTag;
import com.cyberlink.cosmetic.modules.post.model.PostReported;
import com.cyberlink.cosmetic.modules.post.model.PostReported.PostReportedStatus;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.user.model.User;

public interface CommentService {
    
    PostApiResult <Comment> createComment(Long creatorId, String targetType, Long targetId, String comment, String jTags);
    PostApiResult <PageResult<Comment>> listComment(String targetType, Long targetId, BlockLimit blockLimit);
    Map<Long, Long> checkCommentCount(String refType, List<Long> targetIds);
    Map<Long, List<CommentTag>> checkCommentReceiver(List<Long> commentIds);
    PostApiResult <Boolean> deleteComment(Long userId, Long commentId);
    void deleteComments(Long userId, String refType, List<Comment> comments);
    PostApiResult <Comment> updateComment(Long userId, Long commentId, String comment, String jTags);
    PostApiResult <Boolean> reportComment(Long reportedId, Long commentId, String reason);
    Boolean handleReportComment(Long commentId, User reviewer, String result, String remark);
    Map<Long, List<PostReported>> getReportedCommentReason(PostReportedStatus status, List<Long> commentIds);
    Map<Long, Long> checkCommentCountWithDate(String refType, Date startTime, Date endTime, List<Long> targetIds);
    Map<Long, Map<String, Long>> checkCommentRegionCountWithDate(String refType, Date startTime, Date endTime, List<Long> targetIds);
    List<Comment> findPostByIds(List<Long> ids);
}
