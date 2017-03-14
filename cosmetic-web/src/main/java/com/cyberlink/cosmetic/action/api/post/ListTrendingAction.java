package com.cyberlink.cosmetic.action.api.post;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.result.PostWrapperUtil;
import com.cyberlink.cosmetic.modules.post.service.TrendingService;

@UrlBinding("/api/v4.9/post/list-trending.action")
public class ListTrendingAction extends AbstractPostAction {
    
    @SpringBean("post.trendingService")
    private TrendingService trendingService;

    protected Long circleTypeId = null;
    protected Long curUserId = null;
    protected Integer dNext = 0;
    protected Integer pNext = 0;
    protected Integer dLimit = 10;
    protected Integer pLimit = 10;
    protected String locale = "en_US";
    protected String group = null;
    
    public TrendingService getTrendingService() {
        return trendingService;
    }

    public void setTrendingService(TrendingService trendingService) {
        this.trendingService = trendingService;
    }

    public void setCircleTypeId(Long circleTypeId) {
        this.circleTypeId = circleTypeId;
    }

    public void setCurUserId(Long curUserId) {
        this.curUserId = curUserId;
    }

    public void setdNext(Integer dNext) {
        this.dNext = dNext;
    }

    public void setpNext(Integer pNext) {
        this.pNext = pNext;
    }

    @Validate(minvalue = 1, maxvalue = 20, required = false, on = "listTrending")
    public void setdLimit(Integer dLimit) {
        this.dLimit = dLimit;
    }

    @Validate(minvalue = 1, maxvalue = 20, required = false, on = "listTrending")
    public void setpLimit(Integer pLimit) {
        this.pLimit = pLimit;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setGroup(String group) {
        this.group = group;
    }
    
    @DefaultHandler
    public Resolution listTrending() {
        Map<String, PageResultWithNext<MainPostSimpleWrapper>> resultMaps = new LinkedHashMap<String, PageResultWithNext<MainPostSimpleWrapper>>();
        List<Long> dPostIds = new ArrayList<Long>(); 
        trendingService.listTrending("Date", locale, circleTypeId, group, dNext.longValue(), dLimit.longValue(), dPostIds);
        PageResult<MainPostSimpleWrapper> dPgResult = PostWrapperUtil.feedPostsToSimplePostResult(dPostIds, Integer.MAX_VALUE, curUserId, null, null);
        PageResultWithNext<MainPostSimpleWrapper> pNDResult = new PageResultWithNext<MainPostSimpleWrapper>(Long.valueOf(dNext + dLimit), dPgResult);
        resultMaps.put("Date", pNDResult);
        
        List<Long> pPostIds = new ArrayList<Long>(); 
        trendingService.listTrending("Popularity", locale, circleTypeId, group, pNext.longValue(), pLimit.longValue(), pPostIds);
        PageResult<MainPostSimpleWrapper> pPgResult = PostWrapperUtil.feedPostsToSimplePostResult(pPostIds, Integer.MAX_VALUE, curUserId, null, null);
        PageResultWithNext<MainPostSimpleWrapper> pNPResult = new PageResultWithNext<MainPostSimpleWrapper>(Long.valueOf(pNext + pLimit), pPgResult);
        resultMaps.put("Pop", pNPResult);
        
        return json(resultMaps);
    }
    
}
