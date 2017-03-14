package com.cyberlink.cosmetic.modules.post.event;

import com.cyberlink.core.event.DurableEvent;
import com.cyberlink.cosmetic.modules.post.model.PostViewAttr;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.Creator;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.DPWCircle;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class PostViewUpdateEvent extends DurableEvent {

    private static final long serialVersionUID = -502446756207785786L;

    public enum UpdateType {
        MainPost, PostAttr, Creator, Circle, Like;
    };

    private Long keyId = null;
    
    private MainPostSimpleWrapper post = null;

    private PostViewAttr postViewAttr = null;
    
    private Creator creator = null;
    
    private DPWCircle circle = null;
    
    private Long userId = null;
    
    private Boolean liked = null;

    private UpdateType updateType = null;
    
    public PostViewUpdateEvent() {
        super(new Object());
    }

    public PostViewUpdateEvent(MainPostSimpleWrapper post) {
        super(post.getPostId());
        this.keyId = post.getPostId(); 
        this.post = post;
        updateType = UpdateType.MainPost;
    }
    
    public PostViewUpdateEvent(Long postId, PostViewAttr postViewAttr) {
        super(postId);
        this.keyId = postId; 
        this.postViewAttr = postViewAttr;
        updateType = UpdateType.PostAttr;
    }
    
    public PostViewUpdateEvent(Long userId, String avatar, UserType userType, String cover, String description, String displayName) {
        super(userId);
        this.keyId = userId;
        Creator c = new Creator();
        c.avatar = avatar;
        c.userType = userType;
        c.cover = cover;
        c.description = description;
        c.displayName = displayName;
        this.creator = c;
        updateType = UpdateType.Creator;
    }
    
    public PostViewUpdateEvent(Long circleId, String circleName, Boolean isSecret) {
        super(circleId);
        this.keyId = circleId; 
        DPWCircle c = new DPWCircle();
        c.circleName = circleName;
        c.setIsSecret(isSecret);
        this.circle = c;
        updateType = UpdateType.Circle;
    }
    
    public PostViewUpdateEvent(Long postId, Long userId, Boolean liked) {
        super(postId);
        this.keyId = postId; 
        this.userId = userId;
        this.liked = liked;
        updateType = UpdateType.Like;
    }
    
    public void setKeyId(Long keyId) {
        this.keyId = keyId;
    }
    
    public void setPost(MainPostSimpleWrapper post) {
        this.post = post;
    }
    
    public void setPostViewAttr(PostViewAttr postViewAttr) {
        this.postViewAttr = postViewAttr;
    }
    
    public void setCreator(Creator creator) {
        this.creator = creator;
    }

    public void setCircle(DPWCircle circle) {
        this.circle = circle;
    }
    
    public void setUpdateType(UpdateType updateType) {
        this.updateType = updateType;
    }
    
    public Long getKeyId() {
        return keyId;
    }
    
    public MainPostSimpleWrapper getPost() {
        return post;
    }

    public PostViewAttr getPostViewAttr() {
        return postViewAttr;
    }
    
    public Creator getCreator() {
        return creator;
    }

    public DPWCircle getCircle() {
        return circle;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }
    
    public UpdateType getUpdateType() {
        return updateType;
    }
    
}
