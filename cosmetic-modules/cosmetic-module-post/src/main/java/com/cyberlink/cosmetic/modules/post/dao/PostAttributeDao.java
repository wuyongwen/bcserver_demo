package com.cyberlink.cosmetic.modules.post.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.dao.hibernate.ScrollableResultsCallback;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute.PostAttrType;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;

public interface PostAttributeDao extends GenericDao<PostAttribute, Long> {
    
    Map<Long, Long> checkPostAttriButeByIds(String refType, PostAttrType attrType, Long... refIds);
    Map<Long, Map<PostAttrType, Long>> listPostAttriButeByIds(String refType, Long... refIds);
    PostAttribute findByTarget(String refType, Long refId, PostAttrType attrType);
    Map<Long, Long> getLikeCountByUserIds(List<Long> userIds);
    Map<Long, Long> getPromoteByUserIds(List<Long> userIds);
    List<Long> listUserIdsByPromote(List<Long> userIds);
    List<Long> listUserIdsByPostCount(List<Long> userIds);
    PageResult<User> getTopLikedUserByUserType(List<UserType> userType, List<String> locale, Long offset, Long limit);
    PostAttribute getPromoteScoreByUserId(Long userId);
    PostAttribute createOrUpdatePostAttr(String refType, Long refId, PostAttrType attrType, Long value, Boolean createIfNotExist, Boolean updateOnly);
    Long createOrUpdateAttrValue(String refType, Long refId, PostAttrType attrType, Integer diff);
    
    // backend use only
    int updatePostLikeCount();
    int updatePostCommentCount();
    int updateCommentLikeCount();
    Map<Long, Map<String, Object>> getAllPostAttributeAfter(Date beginDate, Date endDate);
    void doOnTopLikedPostAfter(Date beginDate, BlockLimit blockLimit, ScrollableResultsCallback callback);
    
    void getLikeCirInCountPerUser(PostAttrType attrType, Date startDate,
            Date endTime, ScrollableResultsCallback callback);
}
