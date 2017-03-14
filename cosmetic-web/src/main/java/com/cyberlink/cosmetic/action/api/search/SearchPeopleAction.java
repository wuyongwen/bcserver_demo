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
import com.cyberlink.cosmetic.modules.search.model.SearchUser;
import com.cyberlink.cosmetic.modules.search.model.UserView;
import com.cyberlink.cosmetic.modules.search.service.SearchUserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@UrlBinding("/api/search/search-people.action")
public class SearchPeopleAction extends AbstractAction {

	@SpringBean("search.SearchUserService")
	private SearchUserService searchUserService;

	private String locale;
	private String keyword;
	private Integer offset = 0;
	private Integer limit = 10;
	private Long curUserId;
	private List<String> type = new ArrayList<String>();

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

	public Long getCurUserId() {
		return curUserId;
	}

	public void setCurUserId(Long curUserId) {
		this.curUserId = curUserId;
	}

	@DefaultHandler
	public Resolution route() {
		try {
			searchUserService.saveUserKeyword(curUserId, keyword);

			PageResult<UserView> r = new PageResult<UserView>();
			PageResult<SearchUser> searchResult = searchUserService.searchUser(
					locale, curUserId, keyword, offset, limit, type);
			List<SearchUser> searchUsers = searchResult.getResults();
			List<UserView> userViews = new ArrayList<UserView>();
			ObjectMapper om = new ObjectMapper();
			for (SearchUser searchUser : searchUsers) {
				String json = searchUser.getResultJson();
				UserView uv = om.readValue(json, UserView.class);
				userViews.add(uv);
			}

			r.setResults(userViews);
			r.setTotalSize(searchResult.getTotalSize());
			return json(r);
		} catch (Exception e) {
			return error();
		}
	}

	public List<String> getType() {
		return type;
	}

	public void setType(List<String> type) {
		this.type = type;
	}
}
