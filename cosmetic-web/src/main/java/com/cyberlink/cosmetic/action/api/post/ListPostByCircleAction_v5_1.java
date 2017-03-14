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
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.service.PsTrendService;

@UrlBinding("/apis/post/list-post-by-circle.action")
public class ListPostByCircleAction_v5_1 extends AbstractPostAction {

    @SpringBean("post.psTrendService")
    private PsTrendService psTrendService;
    
    private String uuid;
    private String groupId;
    
    protected Long circleId = null;
    protected Long circleTypeId = null;
    protected String defaultType = null;
    protected List<PostStatus> postStatus = null;
    protected String sortBy = "Date";//{Date, Popularity}
    protected Long curUserId = null;
    protected Integer offset = 0;
    protected Integer limit = 10;
    protected String locale = "en_US";
    protected List<Long> loadPostViewIds;
    protected Boolean withLook = null;
    
    public void setCircleId(Long circleId) {
        this.circleId = circleId;
    }
    
    public void setCurUserId(Long curUserId) {
        this.curUserId = curUserId;
    }
    
    public void setCircleTypeId(Long circleTypeId) {
        this.circleTypeId = circleTypeId;
    }
    
    public void setDefaultType(String defaultType) {
        this.defaultType = defaultType;
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
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    public void setPostStatus(List<PostStatus> postStatus){
        this.postStatus = postStatus;
    }
    
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    
    public Resolution loadPostView() {
        if(!Constants.getIsPostCacheView() || !Constants.getWebsiteIsWritable().equals("true"))
            return json("error");
        ArrayList<Long> postIds = new ArrayList<Long>();
        postIds.addAll(loadPostViewIds);
        asyncPostUpdateService.runLoadPostView(postIds);
        return json("OK");
    }
    
    public Resolution route_postView() {
        List<String> locales = new ArrayList<String>();
        if(locale != null)
            locales.add(locale);
        BlockLimit blockLimit = new BlockLimit(offset, limit);
        blockLimit.addOrderBy("promoteScore", false);
        blockLimit.addOrderBy("createdTime", false);    
        
        List<Long> resultList = new ArrayList<Long>();
        PostApiResult <Integer> viewResult = postService.listPostByCircle_v3_1(circleId, circleTypeId, locales, postStatus, sortBy, resultList, withLook, blockLimit, false);
        if(!viewResult.success())
            return new ErrorResolution(viewResult.getErrorDef());
        
        PageResult<MainPostSimpleWrapper> r = postIdToPostView(viewResult.getResult(), resultList, curUserId, null, null);
        return mainPostJson("NOT_DEFINED", r);
    }
    
    @DefaultHandler
    public Resolution route() {
        if(uuid != null && uuid.length() > 0 && circleTypeId == null && circleId == null) {
            List<Long> resultList = new ArrayList<Long>();
            groupId = psTrendService.listPsTrend(uuid, groupId, locale, resultList, new BlockLimit(offset, limit));
            if(groupId == null)
                return route_postView();
            
            PageResult<MainPostSimpleWrapper> r = postIdToPostView(Integer.MAX_VALUE, resultList, curUserId, null, null);
            return mainPostJson(groupId, r);
        }
        return route_postView();
    }

}
