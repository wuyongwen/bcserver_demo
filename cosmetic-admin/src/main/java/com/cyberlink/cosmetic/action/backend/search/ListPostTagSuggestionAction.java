package com.cyberlink.cosmetic.action.backend.search;

import java.util.List;
import java.util.Set;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.search.service.SearchPostService;

@UrlBinding("/search/listPostTagSuggestion.action")
public class ListPostTagSuggestionAction extends AbstractAction{
	
    @SpringBean("search.SearchPostService")
    private SearchPostService searchPostService;
    
    @SpringBean("common.localeDao")
    private LocaleDao localeDao;

	private String locale = "en_US";
	private String tag;
	private Set<String> userLocales;
	PageResult<String> suggestResult = new PageResult<String>();
	
    @DefaultHandler
    public Resolution route() {
		if (!getCurrentUserAdmin()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
		userLocales = localeDao.getAvailableLocaleByType(LocaleType.USER_LOCALE);
    	try{
    		if(tag != null){
		    	List<String> tags = searchPostService.autocompleteTag(locale, 20, tag);
		    	suggestResult.setResults(tags);
		    	suggestResult.setTotalSize(tags.size());
    		}
	    	return forward();
    	}catch(Exception e){
			logger.error("ListPostTagSuggestionAction route fail. message:" + e.getMessage());
			return new StreamingResolution("text/html", e.getMessage());
    	}
    }
    
	public PageResult<String> getSuggestResult() {
		return suggestResult;
	}

	public Set<String> getUserLocales() {
		return userLocales;
	}

	public String getLocale() {
		return locale;
	}
	
	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	public String getTag() {
		return tag;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}
}
