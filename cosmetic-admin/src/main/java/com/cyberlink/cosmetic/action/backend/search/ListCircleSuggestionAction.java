package com.cyberlink.cosmetic.action.backend.search;

import java.util.ArrayList;
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
import com.cyberlink.cosmetic.modules.search.model.CircleView;
import com.cyberlink.cosmetic.modules.search.model.SearchCircle;
import com.cyberlink.cosmetic.modules.search.model.SuggestCircle;
import com.cyberlink.cosmetic.modules.search.service.SearchCircleService;
import com.fasterxml.jackson.databind.ObjectMapper;

@UrlBinding("/search/listCircleSuggestion.action")
public class ListCircleSuggestionAction extends AbstractAction {
	
	@SpringBean("search.SearchCircleService")
	private SearchCircleService searchCircleService;

    @SpringBean("common.localeDao")
    private LocaleDao localeDao;
	
    private PageResult<SuggestCircle> suggestCircleViewResult = new PageResult<SuggestCircle>();
    private Set<String> userLocales;
	private String locale = "en_US";
	private String keyword;
	
	@DefaultHandler
	public Resolution route() {
		if (!getCurrentUserAdmin()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
		userLocales = localeDao.getAvailableLocaleByType(LocaleType.USER_LOCALE);
		try {
			if(keyword != null){
				PageResult<SearchCircle> searchResult = searchCircleService.searchCircle(locale, null, keyword, 0, 20);
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
				suggestCircleViewResult.setResults(suggestCircles);
				suggestCircleViewResult.setTotalSize(suggestCircles.size());
			}
			return forward();
		} catch (Exception e) {
			logger.error("ListCircleSuggestionAction route fail. message:" + e.getMessage());
			return new StreamingResolution("text/html", e.getMessage());
		}
	}
	
	public PageResult<SuggestCircle> getSuggestCircleViewResult() {
		return suggestCircleViewResult;
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

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
}
