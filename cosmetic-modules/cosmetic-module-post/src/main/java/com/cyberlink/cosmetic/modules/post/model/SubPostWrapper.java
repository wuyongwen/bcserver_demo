package com.cyberlink.cosmetic.modules.post.model;

import java.io.Serializable;

import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class SubPostWrapper implements Serializable {

    private static final long serialVersionUID = -7902438514824512280L;

    public Long subPostId = null;
    
    public String content = null;
    
    public PostAttachments attachments = null;
    
    public String extLookUrl = null;
    
    public PostTags tags = null;
    
    public Boolean valid() {
        return true;
    }
}
