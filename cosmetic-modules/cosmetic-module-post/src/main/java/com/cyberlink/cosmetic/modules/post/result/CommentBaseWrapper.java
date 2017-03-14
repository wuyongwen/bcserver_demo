package com.cyberlink.cosmetic.modules.post.result;

import java.util.Date;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.post.model.Comment;
import com.fasterxml.jackson.annotation.JsonView;

public class CommentBaseWrapper {
    public CommentBaseWrapper(Comment comment)
    {
        this.comment = comment;
    }
    
    final protected Comment comment;
    
    @JsonView(Views.Simple.class)
    public Long getCommentId() {
        return comment.getId();
    }
    
    @JsonView(Views.Simple.class)
    public Date getLastModified() {
        return comment.getLastModified();
    }
    
    @JsonView(Views.Simple.class)
    public Date getCreatedTime() {
        return comment.getCreatedTime();
    }
}
