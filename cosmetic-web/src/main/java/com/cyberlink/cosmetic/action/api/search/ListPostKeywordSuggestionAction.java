package com.cyberlink.cosmetic.action.api.search;

import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.search.service.SuggestPostService;

@UrlBinding("/api/search/list-post-keyword-suggestion.action")
public class ListPostKeywordSuggestionAction extends AbstractAction {
	@SpringBean("search.SuggestPostService")
	private SuggestPostService suggestPostService;

	@Validate(required = true, on = "route")
	private String locale;
	
	private String keyword;

	public String getKeyword() {
		return keyword;
	}

	@Validate(required = true, on = "route")
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	@DefaultHandler
	public Resolution route() {
		try {
			List<String> suggestion = suggestPostService.getSuggestion(keyword, locale);
			PageResult<String> result = new PageResult<String>();
			result.setResults(suggestion);
			result.setTotalSize(suggestion.size());
			return json(result);
		} catch (Exception e) {
			e.printStackTrace();
			return error();
		}
	}
}
