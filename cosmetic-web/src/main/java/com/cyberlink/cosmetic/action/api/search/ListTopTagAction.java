package com.cyberlink.cosmetic.action.api.search;

import java.util.List;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.search.service.SearchPostService;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

@UrlBinding("/api/search/list-top-tag.action")
public class ListTopTagAction extends AbstractAction{
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
    	try{
	    	PageResult<String> result = new PageResult<String>();
	    	List<String> tags = searchPostService.getTopTags(locale, 10);
	    	result.setResults(tags);
	    	result.setTotalSize(tags.size());
	    	return json(result);    	
    	}catch(Exception e){
    		return error();
    	}
    }
}
