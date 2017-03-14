package com.cyberlink.cosmetic.modules.search.service;

import java.util.List;

import com.cyberlink.cosmetic.modules.search.model.TypeKeyword;

public interface UserKeywordService {
	public void saveUserKeyword(long userId, TypeKeyword type, String keyword) throws Exception;
	public void deleteUserKeyword(long userId, TypeKeyword type);
	public List<String> getKeywords(long userId, TypeKeyword type) throws Exception;
}
