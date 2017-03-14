package com.cyberlink.cosmetic.action.api.post;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.file.dao.FileDao;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.result.PostWrapperUtil;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.service.CommentService;
import com.cyberlink.cosmetic.modules.post.service.LikeService;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;

@UrlBinding("/api/post/list-post-by-user.action")
public class ListPostByUserAction extends AbstractPostAction {
    @SpringBean("post.PostService")
    private PostService postService;

    @SpringBean("post.LikeService")
    private LikeService likeService;
    
    @SpringBean("post.CommentService")
    private CommentService commentService;
    
    @SpringBean("user.SessionDao")
    private SessionDao sessionDao;
    
    @SpringBean("user.UserDao")
    private UserDao userDao;
    
    @SpringBean("file.fileDao")
    private FileDao fileDao;
    
    @SpringBean("user.SubscribeDao")
    private SubscribeDao subscribeDao;
    
    private String token = "";
    private Long curUserId = null;
    private List<Long> userIds = null;
    private Integer offset = 0;
    private Integer limit = 10;
    private List<PostStatus> postStatus = null;
    private Boolean withSecret = false;
    
    @Validate(required = true, on = "route")
    public void setUserId(List<Long> userIds) {
        this.userIds = userIds;
    }
    
    public void setToken(String token) {
        super.setToken(token);
        this.token = token;
    }
    
    public void setCurUserId(Long curUserId) {
        this.curUserId = curUserId;
    }
    
    @Validate(minvalue = 0, required = false, on = "route")
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    @Validate(minvalue = 1, maxvalue = 20, required = false, on = "route")
    public void setLimit(Integer limit) {
        this.limit = limit;
    }
    
    public void setPostStatus(List<PostStatus> postStatus){
        this.postStatus = postStatus;
    }

    public Resolution route_postView() {
        if(token != null && token.length() > 0 && curUserId == null) {
            if(!authenticate())
                return new ErrorResolution(authError); 
            
            curUserId = getCurrentUserId();
        }
        
        BlockLimit blockLimit = new BlockLimit(offset, limit);
        blockLimit.addOrderBy("promoteScore", false);
        blockLimit.addOrderBy("createdTime", false);
        
        if(userIds.size() == 1 && curUserId != null) {
            withSecret = userIds.get(0).equals(curUserId);
        }
        List<Long> resultList = new ArrayList<Long>();
        final PostApiResult <Integer> viewResult = postService.listPostByUsers_v3_1(userIds, postStatus, withSecret, resultList, blockLimit);
        if(!viewResult.success())
            return new ErrorResolution(viewResult.getErrorDef());
        
        PageResult<MainPostSimpleWrapper> r = postIdToPostView(viewResult.getResult(), resultList, curUserId, null, null);
        return mainPostJson(r);
    }
    
    @DefaultHandler
    public Resolution route() {
        if(Constants.getIsPostCacheView())
            return route_postView();
        
        if(token != null && token.length() > 0 && curUserId == null) {
            if(!authenticate())
                return new ErrorResolution(authError); 
            
            curUserId = getCurrentUserId();
        }
        
        BlockLimit blockLimit = new BlockLimit(offset, limit);
        blockLimit.addOrderBy("promoteScore", false);
        blockLimit.addOrderBy("createdTime", false);
        if(userIds.size() == 1 && curUserId != null) {
            withSecret = userIds.get(0).equals(curUserId);
        }
        final PostApiResult <PageResult<Post>> result = postService.listPostByUsers(userIds, postStatus, withSecret, blockLimit);
        if(!result.success())
            return new ErrorResolution(result.getErrorDef());
        
        final PageResult<Post> posts = result.getResult(); 
        PageResult<MainPostSimpleWrapper> r = PostWrapperUtil.wrapSimplePostResult(posts, curUserId, null, null);
        return mainPostJson(r);
    }
}
