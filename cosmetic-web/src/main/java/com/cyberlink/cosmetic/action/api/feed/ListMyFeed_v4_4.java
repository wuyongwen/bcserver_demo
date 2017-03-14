package com.cyberlink.cosmetic.action.api.feed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.api.post.AbstractPostAction;
import com.cyberlink.cosmetic.modules.feed.event.FeedVisitEvent;
import com.cyberlink.cosmetic.modules.feed.model.FeedPost;
import com.cyberlink.cosmetic.modules.feed.repository.FeedNotifyRepository;
import com.cyberlink.cosmetic.modules.feed.service.FeedService;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.result.PostWrapperUtil;

@UrlBinding("/api/v4.4/feed/list-my-feed.action")
public class ListMyFeed_v4_4 extends AbstractPostAction {
    
    @SpringBean("feed.feedService")
    private FeedService feedService;

    @SpringBean("feed.feedNotifyRepository")
    private FeedNotifyRepository feedNotifyRepository;
    
    private Long userId;
    private List<String> locale = new ArrayList<String>();
    private String init = "0";
    private Integer offset = 0;
    private Long next;
    private Integer limit = 10;
    
    public void setInit(String init) {
        this.init = init;
    }
    
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public List<String> getLocale() {
        return locale;
    }
    
    public void setLocale(List<String> locale) {
        this.locale = locale;
    }
    
    public Integer getOffset() {
        return offset;
    }
    
    @Validate(minvalue = 0, required = false, on = "route")
    public void setOffset(Integer offset) {
        this.offset = offset;
    }
    
    public Integer getLimit() {
        return limit;
    }
    
    @Validate(minvalue = 1, maxvalue = 20, required = false, on = "route")
    public void setLimit(Integer limit) {
        this.limit = limit;
    }
    
    public void setNext(Long next) {
        this.next = next;
    }
    
    @DefaultHandler
    public Resolution route() {
        if(!Constants.getIsRedisFeedEnable()) {
            RedirectResolution redirectResolution = new RedirectResolution("/api/feed/list-my-feed.action");
            Map<String, Object> params = new HashMap<String, Object>();
            if(userId != null)
                params.put("userId", userId);
            if(offset != null)
                params.put("offset", offset);
            if(offset != null)
                params.put("offset", offset);
            if(limit != null)
                params.put("limit", limit);
            if(locale != null)
                params.put("locale", locale);
            redirectResolution.addParameters(params);
            return redirectResolution;
        }
        
        List<FeedPost> feedPosts = null;
        
        if(userId != null)
            feedNotifyRepository.removeNewFeedNotify(userId);
        
        if(next != null)
            feedPosts = feedService.retrieveByNext(userId, locale, init,
                    next, limit);
        else
            feedPosts = feedService.retrieve(userId, locale, init,
                    offset, limit);
        triggerFeedVisitEventIfNecessary();
        List<Long> postIds = new ArrayList<Long>(); 
        for(FeedPost fp : feedPosts) {
            postIds.add(fp.getPostId());
        }
        Map<String, Object> result = new HashMap<String, Object>();
        PageResult<MainPostSimpleWrapper> pgResult = PostWrapperUtil.feedPostsToSimplePostResult(postIds, Integer.MAX_VALUE, userId, null, null);
        result.put("results", pgResult.getResults());
        result.put("totalSize", pgResult.getTotalSize());
        Long next = 0L;
        if(feedPosts.size() > 0) {
            next = (long) feedPosts.get(feedPosts.size() - 1).getScore();
        }
        result.put("next", next);
        return json(result);
    }

    private void triggerFeedVisitEventIfNecessary() {
        if (userId == null) {
            return;
        }
        if (StringUtils.isNotBlank(init)) {
            return;
        }
        if (offset == null) {
            return;
        }
        if (offset.intValue() != 0) {
            return;
        }
        publishDurableEvent(new FeedVisitEvent(userId));        
    }

}
