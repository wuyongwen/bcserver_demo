package com.cyberlink.cosmetic.modules.post.dao;

import java.util.List;
import java.util.Map;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.cosmetic.modules.post.model.PostAutoArticle;

public interface PostAutoArticleDao extends GenericDao<PostAutoArticle, Long> {
	PostAutoArticle findByLink(String link);
	PostAutoArticle findByLocaleAndLink(String locale, String link);
	PostAutoArticle findByPostId(Long postId);
	List<String> findLinkByLocaleAndLinks(String locale, List<String> links);
	Map<String, List<String>> findFileNameByPostIds(List<Long> postIds);
}