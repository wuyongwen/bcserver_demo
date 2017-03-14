package com.cyberlink.cosmetic.event.post;

import com.cyberlink.core.event.DurableEvent;

public class CommentLikeEvent extends DurableEvent {

    private static final long serialVersionUID = -7007061113782983909L;
    private Long userId;
    private Long postId;
    private Long commentId;

    public CommentLikeEvent() {

    }

    public CommentLikeEvent(Long userId, Long postId, Long commentId) {
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
