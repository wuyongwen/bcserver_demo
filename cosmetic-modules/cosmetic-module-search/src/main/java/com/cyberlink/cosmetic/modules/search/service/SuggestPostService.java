package com.cyberlink.cosmetic.modules.search.service;

import java.util.List;

public interface SuggestPostService {

	public List<String> getSuggestion(String keyword, String locale) throws Exception;
}
