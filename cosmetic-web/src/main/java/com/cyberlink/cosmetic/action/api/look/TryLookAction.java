package com.cyberlink.cosmetic.action.api.look;


import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.post.dao.PostAttributeDao;
import com.cyberlink.cosmetic.modules.post.dao.PostViewDao;
import com.cyberlink.cosmetic.modules.post.event.PostViewUpdateEvent;
import com.cyberlink.cosmetic.modules.post.model.PostView;
import com.cyberlink.cosmetic.modules.post.model.PostViewAttr;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute.PostAttrType;

@UrlBinding("/api/v4.4/look/try-look.action")
public class TryLookAction extends AbstractAction {
    
    @SpringBean("post.PostAttributeDao")
    private PostAttributeDao postAttributeDao;

    @SpringBean("post.PostViewDao")
    private PostViewDao postViewDao;
    
    private Long postId;
    
    @Validate(required = true, on = "route")
    public void setPostId(Long postId) {
        this.postId = postId;
    }
    
    @DefaultHandler
    public Resolution route() {
        RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
        Long newLookDownloadCount = updateLookDownloadCount(postId, 1);
        if(newLookDownloadCount != null && newLookDownloadCount > 0)
            updatePostViewLookDownloadCount(postId, newLookDownloadCount);
        return success();
    }
    
    private Long updateLookDownloadCount(Long postId, Integer diff) {
        if(diff == null)
            return null;

        return postAttributeDao.createOrUpdateAttrValue("Post", postId, PostAttrType.LookDownloadCount, diff);
    }
    
    private void updatePostViewLookDownloadCount(Long postId, Long newLookDownloadCount) {
        if(!Constants.getIsPostCacheView())
            return;
        PostView postView = postViewDao.findByPostId(postId);
        if(postView == null)
            return;
        PostViewAttr postAttr = postView.getAttribute();
        if(postAttr == null)
            postAttr = new PostViewAttr();
        if(postAttr.getLookDownloadCount() != null && postAttr.getLookDownloadCount().equals(newLookDownloadCount))
            return;
        postAttr.setLookDownloadCount(newLookDownloadCount);
        postView.setAttribute(postAttr);
        try {
            postViewDao.update(postView);
        }
        catch(Exception e) {
        }
        publishDurableEvent(new PostViewUpdateEvent(postId, postAttr));
    }
}
