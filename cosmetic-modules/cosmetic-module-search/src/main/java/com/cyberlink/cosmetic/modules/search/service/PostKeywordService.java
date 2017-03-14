package com.cyberlink.cosmetic.modules.search.service;

import java.util.List;

public interface PostKeywordService {
	public void saveKeyword(String keyword, String lang);
	public List<String> getTopKeywords(String lang, int topN);
}
