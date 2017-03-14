package com.cyberlink.cosmetic.action.api.search;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.search.model.CircleView;
import com.cyberlink.cosmetic.modules.search.model.SearchCircle;
import com.cyberlink.cosmetic.modules.search.model.SuggestCircle;
import com.cyberlink.cosmetic.modules.search.service.SearchCircleService;
import com.fasterxml.jackson.databind.ObjectMapper;

@UrlBinding("/api/search/list-circle-suggestion.action")
public class ListCircleSuggestionAction extends AbstractAction {
	
	@SpringBean("search.SearchCircleService")
	private SearchCircleService searchCircleService;

	private String locale;
	private String keyword;
	
	public String getLocale() {
		return locale;
	}

	@Validate(required = true, on = "route")
	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getKeyword() {
		return keyword;
	}

	@Validate(required = true, on = "route")
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	@DefaultHandler
	public Resolution route() {
		try {
			PageResult<SearchCircle> searchResult = searchCircleService.searchCircle(locale, null, keyword, 0, 10);
			List<SearchCircle> searchCircles = searchResult.getResults();
			List<SuggestCircle> suggestCircles = new ArrayList<SuggestCircle>();
			ObjectMapper om = new ObjectMapper();
			for (SearchCircle searchCircle : searchCircles) {
				String json = searchCircle.getResultJson();
				CircleView cv = om.readValue(json, CircleView.class);
				SuggestCircle suggestCircle = new SuggestCircle();
				suggestCircle.setId(cv.getId());
				suggestCircle.setCircleName(cv.getCircleName());
				suggestCircle.setCreatorName(cv.getCreatorName());
				suggestCircle.setIconUrl(cv.getIconUrl());
				suggestCircle.setPostCount(cv.getPostCount().intValue());
				suggestCircles.add(suggestCircle);
			}
			
			PageResult<SuggestCircle> suggestCircleViewResult = new PageResult<SuggestCircle>();
			suggestCircleViewResult.setResults(suggestCircles);
			suggestCircleViewResult.setTotalSize(suggestCircles.size());
			return json(suggestCircleViewResult);
		} catch (Exception e) {
			e.printStackTrace();
			return error();
		}
	}	
}
