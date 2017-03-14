package com.cyberlink.cosmetic.modules.post.model;

import com.cyberlink.core.web.jackson.Views.Public;
import com.cyberlink.cosmetic.core.model.AbstractESEntity;
import com.fasterxml.jackson.annotation.JsonView;

public class PostHeat extends AbstractESEntity {

    private String uid;
    
    private String loc;
    
    private Integer likes;
    
    private Integer cirIns;
    
    static public Integer likeWeight = 1, circleInWeight = 3, followerWeigth = 10, min7dPostCount = 5;
    static public Long minPostCount = 20L;
    
    @JsonView(Public.class)
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @JsonView(Public.class)
    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    @JsonView(Public.class)
    public Integer getLikes() {
        return likes;
    }

    public Integer getValidLikes() {
        if(likes == null)
            return likes;
        return likes;
    }
    
    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    @JsonView(Public.class)
    public Integer getCirIns() {
        return cirIns;
    }

    public Integer getValidCirIns() {
        if(cirIns == null)
            return 0;
        return cirIns;
    }
    
    public void setCirIns(Integer cirIns) {
        this.cirIns = cirIns;
    }

}