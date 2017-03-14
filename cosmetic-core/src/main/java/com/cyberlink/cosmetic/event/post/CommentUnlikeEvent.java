package com.cyberlink.cosmetic.event.post;

import com.cyberlink.core.event.DurableEvent;

public class CommentUnlikeEvent extends DurableEvent {

    private static final long serialVersionUID = -4603086945823900166L;
    private Long userId;
    private Long postId;
    private Long commentId;

    public CommentUnlikeEvent() {

    }

    public CommentUnlikeEvent(Long userId, Long postId, Long commentId) {
        this.userId = userId;
        this.postId = postId;
        this.commentId = commentId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getCommentId() {
        return commentId;
    }
}
