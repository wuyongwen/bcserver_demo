package com.cyberlink.cosmetic.event.post;

import com.cyberlink.core.event.DurableEvent;

public class CommentStatusChangeEvent extends DurableEvent {

    private static final long serialVersionUID = 5630211309159726162L;
    private Long commentId;
    private Long postId;
    private Long userId;
    private String commentStatus;

    public CommentStatusChangeEvent() {

    }

    public CommentStatusChangeEvent(Long id, Long postId, Long userId,
            String commentStatus) {
        super(id);
        this.commentId = id;
        this.postId = postId;
        this.userId = userId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getCommentStatus() {
        return commentStatus;
    }

}
