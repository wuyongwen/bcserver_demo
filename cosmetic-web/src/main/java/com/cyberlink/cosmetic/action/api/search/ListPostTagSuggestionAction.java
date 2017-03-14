package com.cyberlink.cosmetic.action.api.search;

import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.search.service.SearchPostService;

@UrlBinding("/api/search/list-post-tag-suggestion.action")
public class ListPostTagSuggestionAction extends AbstractAction{
    @SpringBean("search.SearchPostService")
    private SearchPostService searchPostService;
    
	private String locale;
	private String tag;
	
	public String getLocale() {
		return locale;
	}
	
	@Validate(required = true, on = "route")
	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	public String getTag() {
		return tag;
	}
	
	@Validate(required = true, on = "route")
	public void setTag(String tag) {
		this.tag = tag;
	}
	
    @DefaultHandler
    public Resolution route() {
    	try{
	    	PageResult<String> result = new PageResult<String>();
	    	List<String> tags = searchPostService.autocompleteTag(locale, 10, tag);
	    	result.setResults(tags);
	    	result.setTotalSize(tags.size());
	    	return json(result);    	
    	}catch(Exception e){
    		return error();
    	}
    }
}
