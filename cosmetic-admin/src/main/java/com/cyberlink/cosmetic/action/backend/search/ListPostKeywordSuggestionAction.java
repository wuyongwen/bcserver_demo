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
import com.cyberlink.cosmetic.modules.search.service.SuggestPostService;

@UrlBinding("/search/listPostKeywordSuggestion.action")
public class ListPostKeywordSuggestionAction extends AbstractAction {
	
	@SpringBean("search.SuggestPostService")
	private SuggestPostService suggestPostService;
	
    @SpringBean("common.localeDao")
    private LocaleDao localeDao;

	private String locale = "en_US";
	private String keyword;
	private Set<String> userLocales;
	private PageResult<String> suggestResult = new PageResult<String>();

	@DefaultHandler
	public Resolution route() {
		if (!getCurrentUserAdmin()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
		try {
			userLocales = localeDao.getAvailableLocaleByType(LocaleType.USER_LOCALE);
			if(keyword != null){
				List<String> suggestion = suggestPostService.getSuggestion(keyword, locale);
				suggestResult.setResults(suggestion);
				suggestResult.setTotalSize(suggestion.size());
			}
			return forward();
		} catch (Exception e) {
			logger.error("ListPostKeywordSuggestionAction route fail. message:" + e.getMessage());
			return new StreamingResolution("text/html", e.getMessage());
		}
	}
	
	public Set<String> getUserLocales() {
		return userLocales;
	}
	
	public PageResult<String> getSuggestResult() {
		return suggestResult;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}
}
