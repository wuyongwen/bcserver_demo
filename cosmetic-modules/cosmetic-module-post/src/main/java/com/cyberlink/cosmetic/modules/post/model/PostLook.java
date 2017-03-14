package com.cyberlink.cosmetic.modules.post.model;

import java.io.Serializable;

import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class PostLook implements Serializable {

    private static final long serialVersionUID = -3570743425908482372L;
    
    @JsonView(Views.Public.class)
    public Long lookId;
    
}
