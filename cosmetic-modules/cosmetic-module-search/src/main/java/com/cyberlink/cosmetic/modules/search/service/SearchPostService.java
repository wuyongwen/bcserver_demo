package com.cyberlink.cosmetic.modules.search.service;

import java.util.List;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.search.model.SearchPost;

public interface SearchPostService {
	public PageResult<SearchPost> searchPost(String locale, String keyword, int offset, int limit) throws Exception;
	public String getLang(String locale);
	public List<String> getTopTags(String locale, int topN) throws Exception;
	public PageResult<SearchPost> searchPostByTag(String locale, String tag, int offset, int limit) throws Exception;
	public List<String> autocompleteTag(String locale, int topN, String prefixTag) throws Exception;
	public void saveUserKeyword(Long curUserId, String keyword) throws Exception;
	public void savePostKeyword(String keyword, String locale) throws Exception;
}
