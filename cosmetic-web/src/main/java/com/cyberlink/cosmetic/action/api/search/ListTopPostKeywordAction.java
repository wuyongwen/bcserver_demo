package com.cyberlink.cosmetic.action.api.search;

import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.search.service.PostKeywordService;
import com.cyberlink.cosmetic.modules.search.service.SearchPostService;

@UrlBinding("/api/search/list-top-post-keyword.action")
public class ListTopPostKeywordAction extends AbstractAction{
    @SpringBean("search.PostKeywordService")
    private PostKeywordService postKeywordService;
    
    @SpringBean("search.SearchPostService")
    private SearchPostService searchPostService;
    
    private String locale;
    
	public String getLocale() {
		return locale;
	}

	@Validate(required = true, on = "route")
	public void setLocale(String locale) {
		this.locale = locale;
	}
    
    @DefaultHandler
    public Resolution route() {
    	PageResult<String> result = new PageResult<String>();
    	List<String> keywords = postKeywordService.getTopKeywords(searchPostService.getLang(locale), 10);
    	result.setResults(keywords);
    	result.setTotalSize(keywords.size());
    	return json(result);    	
    }
}
