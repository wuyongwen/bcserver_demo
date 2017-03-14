package com.cyberlink.cosmetic.modules.post.dao;

import java.util.Date;
import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.model.PostScoreTrend;
import com.cyberlink.cosmetic.modules.post.model.PostScore.PoolType;
import com.cyberlink.cosmetic.modules.post.model.PostScore.ResultType;

public interface PostScoreTrendDao extends GenericDao<PostScoreTrend, Long> {

	List<PostScoreTrend> findByPostIds(List<Long> postIds, Boolean isDeleted);
	
	Integer markToHandle(Long reviewerId, ResultType resultType, List<Long> postIds);
	
	PageResult<Object[]> getRevivedDisputePostIds(String locale, Long circleTypeId, PoolType poolType, Date startTime, Date endTime, Boolean withTotalSize, BlockLimit blockLimit);
	
	PostScoreTrend getLastHandledRecord(String postLocale, PoolType poolType, List<ResultType> resultTypes);
	
	List<Object> getHandledPostScoreCountByDate(String postLocale, PoolType poolType, List<ResultType> resultTypes, Date begin, Date end);
}
