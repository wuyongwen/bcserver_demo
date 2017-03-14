package com.cyberlink.cosmetic.modules.post.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class CommentTags implements Serializable {

    private static final long serialVersionUID = -4926904165038787803L;
    
    @JsonView(Views.Public.class)
    public Set<Long> receiverTags = new HashSet<Long>(0);
    
}
