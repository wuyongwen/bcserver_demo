package com.cyberlink.cosmetic.modules.post.result;

import java.util.Date;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.fasterxml.jackson.annotation.JsonView;

public class SubPostBaseWrapper {
    public SubPostBaseWrapper(Post subPost)
    {
        subPostId = subPost.getId();
        lastModified = subPost.getLastModified();
    }
    
    public SubPostBaseWrapper() {
        
    }
    
    protected Long subPostId;
    protected Date lastModified;
    
    @JsonView(Views.Simple.class)
    public Long getSubPostId() {
        return subPostId;
    }
    
    @JsonView(Views.Simple.class)
    public Date getLastModified() {
        return lastModified;
    }
}
