package com.cyberlink.cosmetic.modules.search.service;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.search.model.SearchCircle;

public interface SearchCircleService {

	public PageResult<SearchCircle> searchCircle(String locale, Long curUserId, String keyword, int offset, int limit) throws Exception;
	public void saveUserKeyword(Long curUserId, String keyword) throws Exception;
}
