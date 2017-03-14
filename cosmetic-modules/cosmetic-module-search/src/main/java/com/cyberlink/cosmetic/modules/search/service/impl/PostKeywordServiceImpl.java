package com.cyberlink.cosmetic.modules.search.service.impl;

import java.util.List;

import com.cyberlink.cosmetic.modules.search.dao.PostKeywordDao;
import com.cyberlink.cosmetic.modules.search.model.PostKeyword;
import com.cyberlink.cosmetic.modules.search.service.PostKeywordService;

public class PostKeywordServiceImpl implements PostKeywordService{

	private PostKeywordDao postKeywordDao;
	
	public PostKeywordDao getPostKeywordDao() {
		return postKeywordDao;
	}

	public void setPostKeywordDao(PostKeywordDao postKeywordDao) {
		this.postKeywordDao = postKeywordDao;
	}

	public void saveKeyword(String keyword, String lang) {
		PostKeyword postKeyword = postKeywordDao.findByKeyword(keyword, lang);
		if(postKeyword!=null){
			postKeyword.setFreq(postKeyword.getFreq()+1);
			postKeywordDao.update(postKeyword);
		}else{
			postKeyword = new PostKeyword();
			postKeyword.setKeyword(keyword);
			postKeyword.setFreq(1L);
			postKeyword.setLang(lang);
			postKeywordDao.create(postKeyword);
		}
	}

	public List<String> getTopKeywords(String lang, int topN){
		return postKeywordDao.getKeywords(lang, topN);
	}
}
