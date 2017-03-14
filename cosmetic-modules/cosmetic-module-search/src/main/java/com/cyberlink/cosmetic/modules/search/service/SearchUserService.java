package com.cyberlink.cosmetic.modules.search.service;

import java.util.List;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.search.model.SearchUser;

public interface SearchUserService {
	public PageResult<SearchUser> searchUser(String locale, Long curUserId, String keyword, int offset, int limit, List<String> type) throws Exception;
	public void saveUserKeyword(Long curUserId, String keyword) throws Exception;
}