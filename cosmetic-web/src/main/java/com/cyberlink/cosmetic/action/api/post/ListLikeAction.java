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
import com.cyberlink.cosmetic.modules.post.model.Like.TargetType;
import com.cyberlink.cosmetic.modules.post.result.LikeDetailWrapper;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.service.LikeService;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;

@UrlBinding("/api/post/list-like.action")
public class ListLikeAction extends AbstractAction {
    @SpringBean("post.PostService")
    private PostService postService;
    
    @SpringBean("post.LikeService")
    private LikeService likeService;
    
    @SpringBean("user.SubscribeDao")
    private SubscribeDao subscribeDao;
    
    private TargetType targetType;
    private Long targetId;
    private Integer offset = 0;
    private Integer limit = 10;
    private Long curUserId = null;
    
    @Validate(required = true, on = "route")
    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }
    
    public TargetType getTargetType() {
        return this.targetType;
    }

    @Validate(required = true, on = "route")
    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }
    
    public Long getTargetId() {
        return this.targetId;
    }
    
    @Validate(minvalue = 0, required = false, on = "route")
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    @Validate(minvalue = 1, maxvalue = 20, required = false, on = "route")
    public void setLimit(Integer limit) {
        this.limit = limit;
    }
    
    public void setCurUserId(Long curUserId) {
        this.curUserId = curUserId;
    }
    
    @DefaultHandler
    public Resolution route() {
        BlockLimit blockLimit = new BlockLimit(offset, limit);
        final PostApiResult <PageResult<User>> result = likeService.listLikeUsrByTarget(targetType, targetId, blockLimit);
        if(!result.success())
            return new ErrorResolution(result.getErrorDef());
        
        final PageResult<User> likeUsers = result.getResult();
        final PageResult<LikeDetailWrapper> r = new PageResult<LikeDetailWrapper>();
        r.setTotalSize(likeUsers.getTotalSize());
        
        if(curUserId != null) {
            Set<Long> creatorIds = new HashSet<Long>();
            for(User u : likeUsers.getResults()) {
                creatorIds.add(u.getId());
            }
            Set<Long> subcribeeIds = subscribeDao.findIdBySubscriberAndSubscribees(curUserId, SubscribeType.User, creatorIds.toArray(new Long[creatorIds.size()]));
            for(int idx = 0; idx < likeUsers.getResults().size(); idx++) {
                User likeUser = likeUsers.getResults().get(idx);
                likeUser.setCurUserId(curUserId);
                LikeDetailWrapper ldw = new LikeDetailWrapper(likeUser);
                if(subcribeeIds.contains(likeUser.getId()))
                    ldw.setIsFollowed(true);
                r.add(ldw);
            }
        }
        else{
            for(int idx = 0; idx < likeUsers.getResults().size(); idx++) {
                r.add(new LikeDetailWrapper(likeUsers.getResults().get(idx)));
            }
        }
        
        return json(r);
    }

}
