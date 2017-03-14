package com.cyberlink.cosmetic.modules.search.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.cosmetic.modules.search.model.PostKeyword;

public interface PostKeywordDao extends GenericDao<PostKeyword, Long>{
	public PostKeyword findByKeyword(String keyword, String lang);
	public List<String> getKeywords(String lang, int topN);
	public Long getCircleTypeId(String local, String circleTypeName);
}
