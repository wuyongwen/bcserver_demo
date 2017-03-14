package com.cyberlink.cosmetic.action.backend.search;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.cyberlink.cosmetic.modules.search.model.SearchUser;
import com.cyberlink.cosmetic.modules.search.model.UserView;
import com.cyberlink.cosmetic.modules.search.service.SearchUserService;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.fasterxml.jackson.databind.ObjectMapper;

@UrlBinding("/search/listPeopleSuggestion.action")
public class ListPeopleSuggestionAction extends AbstractAction {
	
	@SpringBean("search.SearchUserService")
	private SearchUserService searchUserService;

    @SpringBean("common.localeDao")
    private LocaleDao localeDao;
	
	private String locale = "en_US";
	private String keyword;
	private Long curUserId = 0L;
	private Set<String> userLocales;
	private List<UserType> userTypeList;
	private List<String> type = new ArrayList<String>();
	private PageResult<UserView> suggestUserViewResult = new PageResult<UserView>();

	@DefaultHandler
	public Resolution route() {
		if (!getCurrentUserAdmin()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
		try {
			userLocales = localeDao.getAvailableLocaleByType(LocaleType.USER_LOCALE);
			userTypeList = Arrays.asList(UserType.values());
			if(keyword != null){
				if(type.get(0).equals("none")){
					type = new ArrayList<String>();
				}
				PageResult<SearchUser> searchResult = searchUserService.searchUser(locale, curUserId, keyword, 0, 20, type);
				List<SearchUser> searchUsers = searchResult.getResults();
				List<UserView> userViews = new ArrayList<UserView>();
				ObjectMapper om = new ObjectMapper();
				for (SearchUser searchUser : searchUsers) {
					String json = searchUser.getResultJson();
					UserView uv = om.readValue(json, UserView.class);
					userViews.add(uv);
				}
	
				suggestUserViewResult.setResults(userViews);
				suggestUserViewResult.setTotalSize(userViews.size());
			}
			return forward();
		} catch (Exception e) {
			logger.error("ListPeopleSuggestionAction route fail. message:" + e.getMessage());
			return new StreamingResolution("text/html", e.getMessage());
		}
	}
	
	public PageResult<UserView> getSuggestUserViewResult() {
		return suggestUserViewResult;
	}

	public Set<String> getUserLocales() {
		return userLocales;
	}
	
	public List<UserType> getUserTypeList() {
		return userTypeList;
	}

	public String getLocale() {
		return locale;
	}

	public List<String> getType() {
		return type;
	}

	public void setType(List<String> type) {
		this.type = type;
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

	public Long getCurUserId() {
		return curUserId;
	}

	public void setCurUserId(Long curUserId) {
		this.curUserId = curUserId;
	}
}