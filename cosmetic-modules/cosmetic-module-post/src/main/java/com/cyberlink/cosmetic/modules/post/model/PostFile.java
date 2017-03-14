package com.cyberlink.cosmetic.modules.post.model;

import java.io.Serializable;

import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class PostFile implements Serializable {

    private static final long serialVersionUID = 2844452701706993361L;

    @JsonView(Views.Public.class)
    public Long fileId;
    
    @JsonView(Views.Public.class)
    public String fileType;
    
    @JsonView(Views.Public.class)
    public Long downloadCount;
    
    @JsonView(Views.Public.class)
    private Object metadata;
    
    public void setMetadata(Object metadata)
    {
        this.metadata = metadata;
    }
    
    public Object getMetadata()
    {
        return this.metadata;
    }
}
