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
import com.cyberlink.cosmetic.modules.search.model.PostView;
import com.cyberlink.cosmetic.modules.search.model.SearchPost;
import com.cyberlink.cosmetic.modules.search.service.SearchPostService;
import com.fasterxml.jackson.databind.ObjectMapper;

@UrlBinding("/api/search/search-post-by-tag.action")
public class SearchPostByTagAction extends AbstractAction{
	@SpringBean("search.SearchPostService")
	private SearchPostService searchPostService;

	private String locale;
	private String tag;
	private Integer offset = 0;
	private Integer limit = 10;
	private Long curUserId;
	
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

	public Long getCurUserId() {
		return curUserId;
	}

	public void setCurUserId(Long curUserId) {
		this.curUserId = curUserId;
	}

	@Validate(minvalue = 1, maxvalue = 20, required = false, on = "route")
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	
	@DefaultHandler
	public Resolution route() {
		try {
			PageResult<SearchPost> searchResult = searchPostService.searchPostByTag(locale, tag, offset, limit);
			List<SearchPost> searchPosts = searchResult.getResults();
			List<PostView> postViews = new ArrayList<PostView>();
			ObjectMapper om = new ObjectMapper();
			for (SearchPost searchPost : searchPosts) {
				String json = searchPost.getResultJson();
				PostView pv = om.readValue(json, PostView.class);
				postViews.add(pv);
			}
			
			PageResult<PostView> postViewResult = new PageResult<PostView>();
			postViewResult.setResults(postViews);
			postViewResult.setTotalSize(searchResult.getTotalSize());
			return json(postViewResult);
		} catch (Exception e) {
			return error();
		}
	}
}
