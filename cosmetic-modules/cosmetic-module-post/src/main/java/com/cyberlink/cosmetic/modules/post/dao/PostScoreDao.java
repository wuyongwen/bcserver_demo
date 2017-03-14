package com.cyberlink.cosmetic.modules.post.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.model.PostScore;
import com.cyberlink.cosmetic.modules.post.model.PostScore.CreatorType;
import com.cyberlink.cosmetic.modules.post.model.PostScore.PoolType;
import com.cyberlink.cosmetic.modules.post.model.PostScore.ResultType;

public interface PostScoreDao extends GenericDao<PostScore, Long> {

    PageResult<PostScore> getPostScoreBetween(Long reviewerId, String postLocale, Date begin, Date end, Boolean isHandled, BlockLimit blockLimit);
    PageResult<PostScore> getHandledPostScoreBetween(Long reviewerId, String postLocale, Date begin, Date end, BlockLimit blockLimit);
	Long getPostScoreCountBetween(Long reviewerId, String postLocale, Date begin, Date end);
	Map<ResultType, Long> getHandledPostScoreCountBetween(String postLocale, List<ResultType> resultTypes, Date begin, Date end);
    PostScore getLastHandledRecord(String postLocale, PoolType poolType, List<ResultType> resultTypes);
    Integer markToHandle(Long reviewerId, ResultType resultType, List<Long> postIds);
    Integer removeRepeatId(List<Long> postIds);
    Boolean batchCreate(List<PostScore> list);
    int batchDelete(Date startDate, Date endDate);
    int batchDeleteByPostIds(List<Long> postIds);
    PageResult<Object[]> getDisputePostIds(String locale, Long circleTypeId, PoolType poolType, Date startTime, Date endTime, CreatorType creatorType, Boolean withTotalSize, BlockLimit blockLimit);
    PageResult<Object[]> getDisputePostIdsOrderByScore(String locale, Long circleTypeId, PoolType poolType, Date startTime, Date endTime, CreatorType creatorType, Boolean withTotalSize, BlockLimit blockLimit);
    PageResult<Object[]> getRevivedDisputePostIds(String locale, Long circleTypeId, PoolType poolType, Date startTime, Date endTime, Boolean withTotalSize, BlockLimit blockLimit);
    List<Long> findExPostIds(List<Long> postIds, ResultType resultType, Boolean isHandled, Boolean isDeleted);
    List<PostScore> findByIds(List<Long> ids);
    List<PostScore> findByPostIds(List<Long> postIds, Boolean isDeleted);
    List<Object> getHandledPostScoreCountByDate(String postLocale, PoolType poolType, List<ResultType> resultTypes, Date begin, Date end);
    List<Object> getHandledPostScoreCounts(String postLocale, PoolType poolType, List<ResultType> resultTypes, Date begin, Date end);
	List<Object> listUnCuratedPostCounts(List<String> postLocale, Long circleTypeId, PoolType poolType);
}
