package com.cyberlink.cosmetic.modules.post.model;

public enum PostStatus {
    Published(true), 
    Unpublished(true), 
    Drafted(false), 
    Hidden(false), 
    Banned(false), 
    Review(true); 

    private final Boolean viewable;

    PostStatus(Boolean viewable) {
        this.viewable = viewable;
    }
    
    public Boolean getViewable() {
        return viewable;
    }
    public boolean isPublished() {
        return Published == this;
    }

}
