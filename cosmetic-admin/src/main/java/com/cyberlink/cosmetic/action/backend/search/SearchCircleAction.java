package com.cyberlink.cosmetic.action.backend.search;

import java.util.ArrayList;
import java.util.LinkedHashSet;
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
import com.cyberlink.cosmetic.modules.search.service.SearchCircleService;
import com.fasterxml.jackson.databind.ObjectMapper;

@UrlBinding("/search/searchCircle.action")
public class SearchCircleAction extends AbstractAction {

	@SpringBean("search.SearchCircleService")
	private SearchCircleService searchCircleService;

    @SpringBean("common.localeDao")
    private LocaleDao localeDao;
	
	private Long curUserId;
	private String locale = "en_US";
	private String keyword;
	private Integer offset = 0;
	private Integer limit = 20;
	private Integer maxPageNumber;
	private Integer pageNumber = 1;
	private PageResult<CircleView> circleViewResult = new PageResult<CircleView>();
	private Set<String> userLocales = new LinkedHashSet<String>();
	
	@DefaultHandler
	public Resolution route() {
		if (!getCurrentUserAdmin()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
		userLocales = localeDao.getAvailableLocaleByType(LocaleType.USER_LOCALE);
		try {
			if(keyword != null){
				offset = (pageNumber - 1) * 20;
				PageResult<SearchCircle> searchResult = searchCircleService.searchCircle(locale, curUserId, keyword, offset, limit);
				List<SearchCircle> searchCircles = searchResult.getResults();
				List<CircleView> circleViews = new ArrayList<CircleView>();
				ObjectMapper om = new ObjectMapper();
				for (SearchCircle searchCircle : searchCircles) {
					String json = searchCircle.getResultJson();
					CircleView cv = om.readValue(json, CircleView.class);
					circleViews.add(cv);
				}
				int searchResultSize = searchResult.getTotalSize();
				if(searchResultSize != 0){
					maxPageNumber = (searchResult.getTotalSize()/20) + 1;
				}
				circleViewResult.setResults(circleViews);
				circleViewResult.setTotalSize(circleViews.size());
			}
			return forward();
		} catch (Exception e) {
			logger.error("SearchCircleAction route fail. message:" + e.getMessage());
			return new StreamingResolution("text/html", e.getMessage());
		}
	}
	
	public Set<String> getUserLocales() {
		return userLocales;
	}
	
	public PageResult<CircleView> getCircleViewResult() {
		return circleViewResult;
	}
	
	public Integer getMaxPageNumber() {
		return maxPageNumber;
	}
	
	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Long getCurUserId() {
		return curUserId;
	}

	public void setCurUserId(Long curUserId) {
		this.curUserId = curUserId;
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

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}
}
