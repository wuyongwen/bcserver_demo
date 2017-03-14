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
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.model.AppName;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.model.PostType;
import com.cyberlink.cosmetic.modules.user.dao.UserAttrDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.UserAttr;
import com.cyberlink.cosmetic.modules.user.model.UserType;

@UrlBinding("/api/post/list-look-post-by-user.action")
public class ListLookPostByUserAction extends AbstractPostAction {
    
    @SpringBean("user.UserDao")
    private UserDao userDao;
    
    @SpringBean("user.userAttrDao")
    private UserAttrDao userAttrDao;
    
    private Long curUserId = null;
    private Long userId = null;
    private UserType userType = null;
    private List<String> locale;
    private Integer offset = 0;
    private Integer limit = 10;
    private List<PostStatus> postStatus = null;
    private PostType postType = PostType.YCL_LOOK;
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public void setUserType(UserType userType) {
        this.userType = userType;
    }
    
    public void setLocale(List<String> locale) {
        this.locale = locale;
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

    public void setPostType(PostType postType) {
        this.postType = postType;
    }
    
    @DefaultHandler
    public Resolution route() {
        if(locale == null || locale.size() <= 0) {
            locale = new ArrayList<String>();
            locale.add("en_US");
        }
        BlockLimit blockLimit = new BlockLimit(offset, limit);
        blockLimit.addOrderBy("promoteScore", false);
        blockLimit.addOrderBy("createdTime", false);
        
        List<Long> resultList = new ArrayList<Long>();
        PostApiResult <Integer> viewResult = null;
        if(userId != null) {
            Boolean withSecret = false;
            if(curUserId != null) {
                withSecret = userId.equals(curUserId);
            }
            UserAttr userAttr = userAttrDao.findByUserId(userId);
            viewResult = postService.listLookPostByUser(userId, userAttr, postType, postStatus, withSecret, resultList, blockLimit);
        }
        else if(userType != null) {
            viewResult = postService.listLookPostByUserType(userType, locale, postType, postStatus, resultList, blockLimit);
        }
        else {
            viewResult = new PostApiResult<Integer>();
            viewResult.setErrorDef(ErrorDef.UnknownPostError);
        }
        
        if(!viewResult.success())
            return new ErrorResolution(viewResult.getErrorDef());
        
        PageResult<MainPostSimpleWrapper> r = postIdToPostView(viewResult.getResult(), resultList, curUserId, null, null);
        return mainPostJson(r);
    }
}
