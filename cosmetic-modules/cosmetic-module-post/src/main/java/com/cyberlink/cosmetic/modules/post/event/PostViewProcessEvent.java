package com.cyberlink.cosmetic.modules.post.event;

import java.util.List;

import com.cyberlink.core.event.DurableEvent;

public class PostViewProcessEvent extends DurableEvent {

    private static final long serialVersionUID = 4501665536611154272L;

    private List<Long> toDeletePostIds = null;

    private Long toCreatePostId = null;
    
    private String postView = null;

    private Long toLikePostId = null;
    
    private Long userId = null;
    
    private Boolean isLiked = null;
    
    public PostViewProcessEvent() {
        super(new Object());
    }
    
    public PostViewProcessEvent(List<Long> toDeletePostIds) {
        super(toDeletePostIds);
        this.toDeletePostIds = toDeletePostIds;
    }
    
    public PostViewProcessEvent(Long toCreatePostId, String postView) {
        super(toCreatePostId);
        this.toCreatePostId = toCreatePostId;
        this.postView = postView;
    }
    
    public PostViewProcessEvent(Long toLikePostId, Long userId, Boolean isLiked) {
        this.toLikePostId = toLikePostId;
        this.userId = userId;
        this.isLiked = isLiked;
    }
    
    public List<Long> getToDeletePostIds() {
        return toDeletePostIds;
    }

    public Long getToCreatePostId() {
        return toCreatePostId;
    }

    public String getPostView() {
        return postView;
    }
    
    public Long getToLikePostId() {
        return toLikePostId;
    }

    public Long getUserId() {
        return userId;
    }

    public Boolean getIsLiked() {
        return isLiked;
    }

    @Override
    public Boolean isGlobal() {
        return false;
    }
}
