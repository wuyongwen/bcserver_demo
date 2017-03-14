package com.cyberlink.cosmetic.action.api.post;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostCircleIn;
import com.cyberlink.cosmetic.modules.post.result.LikeDetailWrapper;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.service.LikeService;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;

@UrlBinding("/api/post/list-recircle-in.action")
public class ListRecircleInAction extends AbstractAction {
    @SpringBean("post.PostService")
    private PostService postService;
    
    private Long postId;
    private Integer offset = 0;
    private Integer limit = 10;
    private Long userId = null;

    @Validate(required = true, on = "route")
    public void setPostId(Long postId) {
        this.postId = postId;
    }
    
    public Long getPostId() {
        return this.postId;
    }
    
    @Validate(minvalue = 0, required = false, on = "route")
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    @Validate(minvalue = 1, maxvalue = 20, required = false, on = "route")
    public void setLimit(Integer limit) {
        this.limit = limit;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    @DefaultHandler
    public Resolution route() {
        BlockLimit blockLimit = new BlockLimit(offset, limit);
        final PageResult<Circle> result = postService.listExCircleInCircle(userId, postId, blockLimit);        
        return json(result);
    }

}
