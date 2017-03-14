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
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.model.AppName;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.model.PostType;
import com.cyberlink.cosmetic.modules.post.service.PostService;

@UrlBinding("/api/v4.4/post/list-post-by-look-type.action")
public class ListPostByLookTypeAction extends AbstractPostAction {
    
    @SpringBean("post.PostService")
    private PostService postService;
    
    private Long lookTypeId = null;
    private List<PostStatus> postStatus = null;
    private Long curUserId = null;
    private Integer offset = 0;
    private Integer limit = 10;
    private String locale = "en_US";
    private PostType postType = PostType.YCL_LOOK;
    
    public void setCurUserId(Long curUserId) {
        this.curUserId = curUserId;
    }
    
    public void setLookTypeId(Long lookTypeId) {
        this.lookTypeId = lookTypeId;
    }
    
    public void setPostType(PostType postType) {
        this.postType = postType;
    }
    
    @Validate(minvalue = 0, required = false, on = "route")
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    @Validate(minvalue = 1, maxvalue = 20, required = false, on = "route")
    public void setLimit(Integer limit) {
        this.limit = limit;
    }
    
    public void setLocale(String locale) {
        this.locale = locale;
    }
    
    public void setPostStatus(List<PostStatus> postStatus){
        this.postStatus = postStatus;
    }
    
    @DefaultHandler
    public Resolution route() {
        List<String> locales = new ArrayList<String>();
        if(locale != null)
            locales.add(locale);
        BlockLimit blockLimit = new BlockLimit(offset, limit);
        blockLimit.addOrderBy("promoteScore", false);
        blockLimit.addOrderBy("createdTime", false);    
        
        List<Long> resultList = new ArrayList<Long>();
        PostApiResult <Integer> viewResult = postService.listPostByLookType(lookTypeId, postType, locale, postStatus, resultList, blockLimit);
        if(!viewResult.success())
            return new ErrorResolution(viewResult.getErrorDef());
        
        PageResult<MainPostSimpleWrapper> r = postIdToPostView(viewResult.getResult(), resultList, curUserId, null, null);
        return mainPostJson(r);
    }

}
