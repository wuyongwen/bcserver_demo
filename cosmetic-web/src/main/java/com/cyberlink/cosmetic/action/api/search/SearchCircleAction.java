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
import com.cyberlink.cosmetic.modules.search.service.SearchCircleService;
import com.fasterxml.jackson.databind.ObjectMapper;

@UrlBinding("/api/search/search-circle.action")
public class SearchCircleAction extends AbstractAction {

	@SpringBean("search.SearchCircleService")
	private SearchCircleService searchCircleService;

	private Long curUserId;
	private String locale;
	private String keyword;
	private Integer offset = 0;
	private Integer limit = 10;

	public Long getCurUserId() {
		return curUserId;
	}

	public void setCurUserId(Long curUserId) {
		this.curUserId = curUserId;
	}

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

	public Integer getOffset() {
		return offset;
	}

	@Validate(minvalue = 0, required = false, on = "route")
	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getLimit() {
		return limit;
	}

	@Validate(minvalue = 1, maxvalue = 20, required = false, on = "route")
	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	@DefaultHandler
	public Resolution route() {
		try {
			searchCircleService.saveUserKeyword(curUserId, keyword);

			PageResult<SearchCircle> searchResult = searchCircleService.searchCircle(locale, curUserId, keyword, offset, limit);
			List<SearchCircle> searchCircles = searchResult.getResults();
			List<CircleView> circleViews = new ArrayList<CircleView>();
			ObjectMapper om = new ObjectMapper();
			for (SearchCircle searchCircle : searchCircles) {
				String json = searchCircle.getResultJson();
				CircleView cv = om.readValue(json, CircleView.class);
				circleViews.add(cv);
			}

			PageResult<CircleView> circleViewResult = new PageResult<CircleView>();
			circleViewResult.setResults(circleViews);
			circleViewResult.setTotalSize(searchResult.getTotalSize());
			return json(circleViewResult);
		} catch (Exception e) {
			e.printStackTrace();
			return error();
		}
	}
}
