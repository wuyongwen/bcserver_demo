package com.cyberlink.cosmetic.action.api.post;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetSubType;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetType;
import com.cyberlink.cosmetic.modules.post.model.PostType;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;

@UrlBinding("/api/post/list-liked-target.action")
public class ListLikedTargetAction extends AbstractPostAction {
    
    private TargetType targetType;
    private TargetSubType targetSubType = TargetSubType.YCL_LOOK;
    private Integer offset = 0;
    private Integer limit = 10;
    private Long userId = null;
    private Long curUserId = null;
    
    @Validate(required = true, on = "route")
    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }
    
    public TargetType getTargetType() {
        return this.targetType;
    }
    
    public void setTargetSubType(TargetSubType targetSubType) {
        this.targetSubType = targetSubType;
    }
    
    public TargetSubType getTargetSubType() {
        return this.targetSubType;
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
    
    public void setCurUserId(Long curUserId) {
        this.curUserId = curUserId;
    }
    
    @DefaultHandler
    public Resolution route() {
        BlockLimit blockLimit = new BlockLimit(offset, limit);
        List<Long> resultList = new ArrayList<Long>();
        PostApiResult <Integer> viewResult = likeService.listLikedTargetId(targetType, targetSubType, userId, resultList, blockLimit);
        if(!viewResult.success())
            return new ErrorResolution(viewResult.getErrorDef());
        
        Boolean defaultIsLiked = null;
        if (curUserId == userId)
            defaultIsLiked = true;
		PageResult<MainPostSimpleWrapper> r = postIdToPostView(viewResult.getResult(), resultList, curUserId, defaultIsLiked, null);
		return mainPostJson(r);
    }
    
}
