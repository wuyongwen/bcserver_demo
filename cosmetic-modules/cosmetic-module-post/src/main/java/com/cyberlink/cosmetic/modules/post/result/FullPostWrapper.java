package com.cyberlink.cosmetic.modules.post.result;

import java.util.ArrayList;
import java.util.List;

import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class FullPostWrapper {
    public FullPostWrapper()
    {
    }
    
    public MainPostDetailWrapper mainPost;
    public List<SubPostSimpleWrapper> subPosts = new ArrayList<SubPostSimpleWrapper>();
    
    @JsonView(Views.Simple.class)
    public MainPostDetailWrapper getMainPost() {
        return mainPost;
    }
    
    @JsonView(Views.Simple.class)
    public List<SubPostSimpleWrapper> getSubPosts() {
        return subPosts;
    }
}
