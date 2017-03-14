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

@UrlBinding("/search/searchPeople.action")
public class SearchPeopleAction extends AbstractAction {

	@SpringBean("search.SearchUserService")
	private SearchUserService searchUserService;
	
    @SpringBean("common.localeDao")
    private LocaleDao localeDao;
	
	private String locale = "en_US";
	private String keyword;
	private Integer offset = 0;
	private Integer limit = 20;
	private Long curUserId;
	private Set<String> userLocales;
	private List<UserType> userTypeList;
	private Integer maxPageNumber;
	private Integer pageNumber = 1;
	private String type;
	private PageResult<UserView> userViewsResult = new PageResult<UserView>();

	@DefaultHandler
	public Resolution route() {
		if (!getCurrentUserAdmin()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
		userLocales = localeDao.getAvailableLocaleByType(LocaleType.USER_LOCALE);
		userTypeList = Arrays.asList(UserType.values());
		try {
			if(keyword != null){
				List<String> selectedType = new ArrayList<String>();
				if(!type.equals("none")){
					selectedType.add(type);
				}
				offset = (pageNumber - 1) * 20;
				PageResult<SearchUser> searchResult = searchUserService.searchUser(locale, curUserId, keyword, offset, limit, selectedType);
				List<SearchUser> searchUsers = searchResult.getResults();
				List<UserView> userViews = new ArrayList<UserView>();
				ObjectMapper om = new ObjectMapper();
				for (SearchUser searchUser : searchUsers) {
					String json = searchUser.getResultJson();
					UserView uv = om.readValue(json, UserView.class);
					userViews.add(uv);
				}
				int searchResultSize = searchResult.getTotalSize();
				if(searchResultSize != 0){
					maxPageNumber = (searchResult.getTotalSize()/20) + 1;
				}
				userViewsResult.setResults(userViews);
				userViewsResult.setTotalSize(userViews.size());
			}
			return forward();
		} catch (Exception e) {
			logger.error("SearchPeopleAction route fail. message:" + e.getMessage());
			return new StreamingResolution("text/html", e.getMessage());
		}
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
	
	public List<UserType> getUserTypeList() {
		return userTypeList;
	}
	
	public Set<String> getUserLocales() {
		return userLocales;
	}

	public PageResult<UserView> getUserViewsResult() {
		return userViewsResult;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public Long getCurUserId() {
		return curUserId;
	}

	public void setCurUserId(Long curUserId) {
		this.curUserId = curUserId;
	}
}
