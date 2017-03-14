package com.cyberlink.cosmetic.modules.post.result;

import java.util.List;

import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.look.model.LookType;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostProductTag;
import com.cyberlink.cosmetic.modules.post.model.PostExProductTag;
import com.cyberlink.cosmetic.modules.post.model.PostTags;

public class MainPostDetailWrapper extends MainPostSimpleWrapper {
    
    private static final long serialVersionUID = -6659486135852289841L;

    public MainPostDetailWrapper() {
        super();
    }
    
    public MainPostDetailWrapper(Post post, List<FileItem> userItems, List<Object> fileItems, List<Circle> circles, LookType lookType, List<PostProductTag> postProductTags, List<PostExProductTag> postExProductTags) {
        super(post, userItems, fileItems, circles, lookType);
        if(postProductTags != null && postProductTags.size() > 0) {
            if(tags == null)
                tags = new PostTags();
            tags.setProductTags(postProductTags);
        }
		if (postExProductTags != null && postExProductTags.size() > 0) {
			if (tags == null)
				tags = new PostTags();
			tags.setExProductTags(postExProductTags);
		}
    }
    
}
