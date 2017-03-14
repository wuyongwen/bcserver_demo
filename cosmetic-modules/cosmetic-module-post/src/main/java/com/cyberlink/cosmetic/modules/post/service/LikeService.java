package com.cyberlink.cosmetic.modules.post.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.model.Like;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetSubType;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetType;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.service.LikeService.LikeServiceResult;
import com.cyberlink.cosmetic.modules.user.model.User;

public interface LikeService {

    public enum LikeServiceResult {
        LIKE_REL_NOT_AFFECTED,
        LIKE_REL_OK,
        LIKE_REL_FAILED
    }
    
    PostApiResult <LikeServiceResult> unlikeTarget(Long userId, TargetType refType, Long refId);
    void processUnlikeTarget(Long userId, String refType, Long refId);
    void unlikeTargets(Long userId, TargetType refType, List<Like> likes);
    void processUnlikeTargets(Long userId, String refType, List<Long> refIds);
    PostApiResult <LikeServiceResult> likeTarget(Long userId, TargetType refType, TargetSubType refSubType, Long refId);
    void processLikeTarget(Long userId, String refType, Long refId, Long createdTime);
    void processLikeTargets(List<Like> likes);
    List<Long> getLikeTarget(Long userId, TargetType refType, List<Long> targetIds);
    Map<Long, Long> checkLikeCount(String refType, List<Long> targetIds);
    PostApiResult <PageResult<User>> listLikeUsrByTarget(TargetType targetType, Long targetId, BlockLimit blockLimit);
    PostApiResult<Integer> listLikedTargetId(TargetType targetType, TargetSubType targetSubType, Long userId, List<Long> result, BlockLimit blockLimit);
    PostApiResult<Long> listLikedTargetCount(TargetType targetType, TargetSubType targetSubType, Long userId);
    Map<Long, Long> checkLikeCountWithDate(String refType, Date startTime, Date endTime, List<Long> targetIds);
    Map<Long, Map<String, Long>> checkLikeRegionCountWithDate(String refType, Date startTime, Date endTime, List<Long> targetIds);
    PageResult<User> getTopLikedUserByUserIds(List<Long> idList, Long offset, Long limit);
    PageResult<User> getTopLikedUserByListIds(List<List<Long>> idList1, Long offset, Long limit);
    Long initRedisLikeBetween(Long next, Long count);
    
}
