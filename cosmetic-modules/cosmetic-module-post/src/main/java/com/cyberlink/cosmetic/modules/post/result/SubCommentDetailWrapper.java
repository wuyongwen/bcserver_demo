package com.cyberlink.cosmetic.modules.post.result;

import com.cyberlink.cosmetic.modules.post.model.Comment;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({ "tags", "latestSubComment", "subCommentCount" })
public class SubCommentDetailWrapper extends CommentDetailWrapper {
	
	public SubCommentDetailWrapper(Comment comment) {
        super(comment);
    }

}
