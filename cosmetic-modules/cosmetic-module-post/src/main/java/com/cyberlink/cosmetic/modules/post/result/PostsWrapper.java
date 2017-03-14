package com.cyberlink.cosmetic.modules.post.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class PostsWrapper implements Serializable {

    private static final long serialVersionUID = 4725181848051625090L;
    
    @JsonView(Views.Public.class)
    public MainPostBaseWrapper mainPost;
    
    @JsonView(Views.Public.class)
    public List<SubPostBaseWrapper> subPosts = new ArrayList<SubPostBaseWrapper>(0);

}
