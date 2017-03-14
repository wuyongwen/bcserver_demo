package com.cyberlink.cosmetic.modules.post.result;

import java.io.Serializable;
import java.util.Date;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.fasterxml.jackson.annotation.JsonView;

public class MainPostBaseWrapper implements Serializable {
    private static final long serialVersionUID = 1308674345095148514L;

    public MainPostBaseWrapper() {
        
    }
    
    public MainPostBaseWrapper(Post post)
    {
        postId = post.getId();
        createdTime = post.getCreatedTime();
        /* Temporary return createdTiema as lastModified time,
         * to avoid showing wrong date information in AP.
         */
        //lastModified = post.getLastModified();
        lastModified = post.getCreatedTime();
        title = post.getTitle();
        content = post.getContent();
    }
    
    protected Long postId = null;
    protected Date lastModified = null;
    protected Date createdTime = null;
    protected String title;
    protected String content;
    
    @JsonView(Views.Basic.class)
    public Long getPostId() {
        return postId;
    }
    
    @JsonView(Views.Basic.class)
    public Date getLastModified() {
        /* Temporary return createdTiema as lastModified time,
         * to avoid showing wrong date information in AP.
         */
        //return post.getLastModified();
        return lastModified;
    }
    
    @JsonView(Views.Simple.class)
    public Date getCreatedTime() {
        return createdTime;
    }

}
