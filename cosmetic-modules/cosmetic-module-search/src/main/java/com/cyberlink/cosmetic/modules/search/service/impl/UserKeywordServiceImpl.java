package com.cyberlink.cosmetic.modules.search.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.cyberlink.cosmetic.modules.search.dao.UserKeywordDao;
import com.cyberlink.cosmetic.modules.search.model.TypeKeyword;
import com.cyberlink.cosmetic.modules.search.model.UserKeyword;
import com.cyberlink.cosmetic.modules.search.service.UserKeywordService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserKeywordServiceImpl implements UserKeywordService{

	private UserKeywordDao userKeywordDao;
	
	public UserKeywordDao getUserKeywordDao() {
		return userKeywordDao;
	}

	public void setUserKeywordDao(UserKeywordDao userKeywordDao) {
		this.userKeywordDao = userKeywordDao;
	}

	@SuppressWarnings("unchecked")
	public void saveUserKeyword(long userId, TypeKeyword type, String keyword) throws Exception {
		ObjectMapper om = new ObjectMapper();
		UserKeyword uk = userKeywordDao.findByUserIdAndType(userId, type);
		if(uk==null){
			uk = new UserKeyword();
			uk.setUserId(userId);
			uk.setType(type);
			Queue<String> keywords=new LinkedList<String>();
			keywords.add(keyword);
			String keywordStr = om.writeValueAsString(keywords);
			uk.setKeywords(keywordStr);
			userKeywordDao.create(uk);
		}else{
			String keywordStr = uk.getKeywords();
			Queue<String> keywords = om.readValue(keywordStr, Queue.class);
			keywords.removeAll(Arrays.asList(keyword));
			if(keywords.size()==10)
				keywords.poll();
			
			keywords.add(keyword);
			keywordStr = om.writeValueAsString(keywords);
			uk.setKeywords(keywordStr);
			userKeywordDao.update(uk);
		}
		
	}

	public void deleteUserKeyword(long userId, TypeKeyword type) {
		userKeywordDao.removeKeyword(userId, type);
	}

	@SuppressWarnings("unchecked")
	public List<String> getKeywords(long userId, TypeKeyword type) throws Exception{
		ObjectMapper om = new ObjectMapper();
		UserKeyword userKeyword = userKeywordDao.findByUserIdAndType(userId, type);
		if (userKeyword == null){
			return new ArrayList<String>();
		}
		String keywords = userKeyword.getKeywords();
		List<String> result = om.readValue(keywords, List.class);
		List<String> reverseResult = new ArrayList<String>();
		for(int i=result.size()-1;i>=0;i--){
			reverseResult.add(result.get(i));
		}
		return reverseResult;
	}
}
