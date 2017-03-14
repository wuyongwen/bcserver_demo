package com.cyberlink.cosmetic.modules.post.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MainPostWrapper implements Serializable {

    private static final long serialVersionUID = -6868824439263779157L;

    public Long postId = null;
    
    public List<Long> circleIds = null;
    
    public String title = null;
    
    public String content = null;
    
    public PostAttachments attachments = null;
    
    public PostTags tags = null;
    
    public PostStatus postStatus = null;
    
    public Long lookTypeId = null;
    
    public String extLookUrl = null;
    
    public PostType postType = null;
    
    public Date createdTime = null;
    
    public Boolean valid() {
        if(title == null || title.length() <= 0)
            return false;

        return true;
    }
    
}
