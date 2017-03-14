package com.cyberlink.cosmetic.modules.user.model;

import com.cyberlink.core.web.jackson.Views.Public;
import com.cyberlink.cosmetic.core.model.AbstractESEntity;
import com.cyberlink.cosmetic.modules.user.model.UserBadge.BadgeType;
import com.fasterxml.jackson.annotation.JsonView;

public class UserHeat extends AbstractESEntity {

    private String loc;
    
    private Integer posts;

    private Integer likes;
    
    private Integer cirIns;
    
    private Integer followers;
    
    private BadgeType badge;

    private Integer minPosts = 25, diamondScore = 30000, platinumScore = 15000, goldScore = 5000, silverScore = 2000;
    private Integer likeWeight = 1, circleInWeight = 3, followerWeigth = 10;
    
    @JsonView(Public.class)
    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    @JsonView(Public.class)
    public Integer getPosts() {
        return posts;
    }

    public Integer getValidPosts() {
        if(posts == null)
            return 0;
        return posts;
    }
    
    public void setPosts(Integer posts) {
        this.posts = posts;
    }
    
    @JsonView(Public.class)
    public Integer getLikes() {
        return likes;
    }

    public Integer getValidLikes() {
        if(likes == null)
            return 0;
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

    @JsonView(Public.class)
    public Integer getFollowers() {
        return followers;
    }

    public Integer getValidFollowers() {
        if(followers == null)
            return 0;
        return followers;
    }
    
    public void setFollowers(Integer followers) {
        this.followers = followers;
    }
    
    @JsonView(Public.class)
    public BadgeType getBadge() {
        return badge;
    }
    
    public BadgeType getValidBadge() {
        if(badge == null)
            return BadgeType.Normal;
        return badge;
    }
    
    public void setBadge(BadgeType badge) {
        this.badge = badge;
    }
    
    public Integer getScore() {
        return likeWeight * getValidLikes() + circleInWeight * getValidCirIns() + followerWeigth * getValidFollowers();
    }
    
    public BadgeType calculateNewBadge() {
        BadgeType badge = BadgeType.Normal;
        if(getPosts() < minPosts)
            return badge;
        
        Integer score = getScore();
        if(score >= diamondScore)
            badge = BadgeType.Diamond;
        else if(score >= platinumScore)
            badge = BadgeType.Platinum;
        else if(score >= goldScore)
            badge = BadgeType.Gold;
        else if(score >= silverScore)
            badge = BadgeType.Silver;
        return badge;
    }
}