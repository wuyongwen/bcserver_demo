package com.cyberlink.cosmetic.action.api.post;

import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.notify.model.NotifyType;
import com.cyberlink.cosmetic.modules.notify.service.NotifyService;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.result.MainPostBaseWrapper;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.result.PostsWrapper;
import com.cyberlink.cosmetic.modules.post.result.SubPostBaseWrapper;
import com.cyberlink.cosmetic.modules.post.service.PostService;

@UrlBinding("/api/post/update-posts.action")
public class UpdatePostsAction extends AbstractAction {
    @SpringBean("post.PostService")
    private PostService postService;

    private String mainPost;
    private List<String> updateSubPosts;
    private List<String> deleteSubPosts;
    private List<String> newSubPosts;
    private String locale;
    
    @Validate(required = true, on = "route")
    public void setToken(String token) {
        super.setToken(token);
    }
    
    public void setMainPost(String mainPost) {
        this.mainPost = mainPost;
    }
    
    public void setUpdateSubPosts(List<String> updateSubPosts) {
        this.updateSubPosts = updateSubPosts;
    }
    
    public void setDeleteSubPosts(List<String> deleteSubPosts) {
        this.deleteSubPosts = deleteSubPosts;
    }
    
    public void setNewSubPosts(List<String> newSubPosts) {
        this.newSubPosts = newSubPosts;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
    
    @DefaultHandler
    public Resolution route() {
        if(!authenticate())
            return new ErrorResolution(authError); 
        
        Long userId = getCurrentUserId();
        PostApiResult <List<Post>> result = postService.updatePosts(userId, locale, null, mainPost, null, null, updateSubPosts, deleteSubPosts, newSubPosts);
        if(!result.success())
            return new ErrorResolution(result.getErrorDef());
        
        List<Post> resultList = result.getResult();        
        PostsWrapper r = new PostsWrapper();
        for(int idx = 0; idx < resultList.size(); idx++) {
            Post p = resultList.get(idx);
            if(p.getParentId() == null)
                r.mainPost = new MainPostBaseWrapper(p);
            else
                r.subPosts.add(new SubPostBaseWrapper(p));
        }
        
        return json(r);
    }

}
