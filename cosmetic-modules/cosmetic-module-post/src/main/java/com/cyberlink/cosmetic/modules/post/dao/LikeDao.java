package com.cyberlink.cosmetic.modules.post.dao;

import java.util.List;
import java.util.Map;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.dao.hibernate.ScrollableResultsCallback;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.model.Like;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetSubType;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetType;
import com.cyberlink.cosmetic.modules.user.model.User;

public interface LikeDao extends GenericDao<Like, Long>{
    
    List<Long> listLikedTarget(Long userId, TargetType refType, List<Long> targetIds);
    Like getLike(User user, TargetType refType, Long targetId);
    PageResult<Like> blockQueryWithoutSize(TargetType refType, Long refId, BlockLimit blockLimit);
    Map<Long, Long> hardGetLikeCountByTargetsWithoutEmpty(TargetType refType, List<Long> refIds);
    Long hardGetLikedPostCount(Long userId, TargetType refType, TargetSubType refSubType);
    Integer hardGetLikedTargetId(Long userId, TargetType refType, TargetSubType refSubType, List<Long> result, BlockLimit blockLimit);
    void getLikedTargetIdWithoutSize(Long userId, TargetType refType, TargetSubType refSubType, List<Long> result, BlockLimit blockLimit);
    void updateByDeletePost(TargetType refType, Long refId);
    PageResult<Like> findByUserId(Long userId, TargetType refType, Boolean withSize, BlockLimit blockLimit);
    PageResult<Like> getAllLikeUserIds(List<Long> postIds, Boolean withSize, BlockLimit blockLimit);
    int bacthDeleteByTargets(TargetType refType, List<Long> refIds);
    void doWithAllLikeBetween(TargetType refType, Long next, Long count, ScrollableResultsCallback callback);
    void doWithAllLike(TargetType refType, List<Long> refId, ScrollableResultsCallback callback);
    
}
