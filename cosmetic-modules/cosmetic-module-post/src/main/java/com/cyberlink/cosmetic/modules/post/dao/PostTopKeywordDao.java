package com.cyberlink.cosmetic.modules.post.dao;

import java.util.List;
import java.util.Set;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.model.PostTopKeyword;

public interface PostTopKeywordDao extends GenericDao<PostTopKeyword, Long>{
	void updateKeywordsFreq(String locale, Set<String> keywords, Integer kwBucketId);
	PageResult<PostTopKeyword> getPopularKeywords(String locale, BlockLimit blockLimit, Boolean isTop, Integer kwBucketId, List<String> exCircleName);
	void deleteOldRecord(Integer kwBucketId);
	void batchInsert(List<PostTopKeyword> list);
}
