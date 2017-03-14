package com.cyberlink.cosmetic.modules.post.result;

import java.util.ArrayList;
import java.util.List;

public class MainPostBasicWrapper extends MainPostSimpleWrapper {
    
    private static final long serialVersionUID = -6659486135852289841L;

    public MainPostBasicWrapper() {
        super();
    }
    
    public MainPostBasicWrapper(MainPostSimpleWrapper simpleMainPost) {
    	postId = simpleMainPost.getPostId();
    	lastModified = simpleMainPost.getLastModified();
    	title = simpleMainPost.getTitle();
    	content = simpleMainPost.getContent();
    	creator = simpleMainPost.getCreator();
    	attachments = simpleMainPost.getAttachments();
    	setFiles(attachments.getFiles());
    	attachments.setFiles(files);
    	isLiked = simpleMainPost.getisLiked();
    	likeCount = simpleMainPost.getLikeCount();
    	commentCount = simpleMainPost.getCommentCount();
    	circleInCount = simpleMainPost.getCircleInCount();
    	lookDownloadCount = simpleMainPost.getLookDownloadCount();
    	circles = simpleMainPost.getCircles();
    	gotProductTag = simpleMainPost.getGotProductTag();
    	postSource = simpleMainPost.getPostSource();
    	extLookUrl = simpleMainPost.getExtLookUrl();
    	postType = simpleMainPost.getPostType();
    	lookType = simpleMainPost.getLookType();
    }
    
    private List<File> files = new ArrayList<File>();
    
    public void setFiles(List<File> files) {
		for (File file : files) {
			if (file.getFileType().equals("Photo") || file.getFileType().equals("PostCover"))
				this.files.add(file);
		}
	}

}
