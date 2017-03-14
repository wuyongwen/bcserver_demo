package com.cyberlink.cosmetic.action.api.post;

import java.io.IOException;
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
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.service.CircleService;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.model.PostView;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.service.RelatedPostService;

@UrlBinding("/api/post/list-related-post.action")
public class ListRelatedPostAction_v4_5 extends AbstractPostAction {
    
    @SpringBean("post.relatedPostService")
    private RelatedPostService relatedPostService;
    
    @SpringBean("circle.circleService")
    protected CircleService circleService;
    
    @SpringBean("post.PostDao")
    protected PostDao postDao;
    
    protected Long postId = null;
    protected Long curUserId = null;
    protected Integer offset = 0;
    protected Integer limit = 10;
    protected String locale = null;
    private List<PostStatus> postStatus = null;

    public void setPostId(Long postId) {
        this.postId = postId;
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
    
    public void setLocale(String locale) {
        this.locale = locale;
    }
    
    public void setPostStatus(List<PostStatus> postStatus){
        this.postStatus = postStatus;
    }
    
    @DefaultHandler
    public Resolution route() {
        if(postId == null)
            return new ErrorResolution(ErrorDef.InvalidPostTargetId);
        
        List<Long> relatedPostIds = new ArrayList<Long>();
        Long totalSize = relatedPostService.getRelatedPostIds(postId, locale, offset, limit, relatedPostIds);
        if(totalSize == 0 && offset == 0)
            return use_v4_4();
        PageResult<MainPostSimpleWrapper> r = postIdToPostView(totalSize.intValue(), relatedPostIds, curUserId, null, null);
        return mainPostJson(r);
    }

    private Resolution use_v4_4() {
        if(postId == null)
            return new ErrorResolution(ErrorDef.InvalidPostTargetId);
        PostView pv = postViewDao.findByPostId(postId);
        String defaultType = null;
        Long circleTypeId = null;
        
        if(pv != null) {
            MainPostSimpleWrapper tmp;
            try {
                tmp = objectMapper.readValue(pv.getMainPost(), MainPostSimpleWrapper.class);
            } catch (IOException e) {
                return new ErrorResolution(ErrorDef.UnknownPostError); 
            }
            List<MainPostSimpleWrapper.DPWCircle> circles = tmp.getCircles();
            if(circles.size() <= 0)
                return json(new PageResult<MainPostSimpleWrapper>());
            
            defaultType = circles.get(0).getDefaultType();
            circleTypeId = circles.get(0).getCircleTypeId();
        }
        else if(postDao.exists(postId)){
            Post p = postDao.findById(postId);
            Circle c = p.getCircle();
            if(c == null)
                return new ErrorResolution(ErrorDef.UnknownPostError); 
            defaultType = c.getDefaultType();
            circleTypeId = c.getCircleCreatorId();
        }
        else
            return new ErrorResolution(ErrorDef.InvalidPostTargetId);
                    
        if(defaultType != null && defaultType.length() > 0) {
            circleTypeId = circleService.getCircleTypeByDefaultType(defaultType, locale);
            if(circleTypeId == null)
                return new ErrorResolution(ErrorDef.InvalidCircleDefaultType);
        }

        List<String> locales = new ArrayList<String>();
        if(locale != null)
            locales.add(locale);
        BlockLimit blockLimit = new BlockLimit(offset, limit);
        blockLimit.addOrderBy("promoteScore", false);
        blockLimit.addOrderBy("createdTime", false);    
        
        List<Long> resultList = new ArrayList<Long>();
        PostApiResult <Integer> viewResult = postService.listPostByCircle_v3_1(null, circleTypeId, locales, postStatus, "Date", resultList, null, blockLimit, false);
        if(!viewResult.success())
            return new ErrorResolution(viewResult.getErrorDef());
        
        PageResult<MainPostSimpleWrapper> r = postIdToPostView(viewResult.getResult(), resultList, curUserId, null, null);
        return mainPostJson(r);
    }
}
