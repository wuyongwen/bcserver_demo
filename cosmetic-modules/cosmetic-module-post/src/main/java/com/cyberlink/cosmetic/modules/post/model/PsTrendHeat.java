package com.cyberlink.cosmetic.modules.post.model;

import java.util.List;

import com.cyberlink.core.web.jackson.Views.Public;
import com.cyberlink.cosmetic.core.model.AbstractESEntity;
import com.fasterxml.jackson.annotation.JsonView;

public class PsTrendHeat extends AbstractESEntity {

    private List<String> cirTypes;
    private String loc;
    private Integer likes;
    private Integer cirIns;

    @JsonView(Public.class)
    public List<String> getCirTypes() {
        return cirTypes;
    }

    public void setCirTypes(List<String> cirTypes) {
        this.cirTypes = cirTypes;
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

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    @JsonView(Public.class)
    public Integer getCirIns() {
        return cirIns;
    }

    public void setCirIns(Integer cirIns) {
        this.cirIns = cirIns;
    }

}