package com.cyberlink.cosmetic.modules.post.repository;

import java.util.List;
import java.util.Map;

import com.cyberlink.cosmetic.modules.post.model.PostViewAttr;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.Creator;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.DPWCircle;
import com.cyberlink.cosmetic.modules.post.result.PostWrapperUtil.WrappingPostCallBack;
import com.cyberlink.cosmetic.modules.user.model.UserType;

public interface PostViewRepository {

    void createOrUpdatePostView(Long postId, String postJson);
    
    void createOrUpdatePostView(Long postId, MainPostSimpleWrapper post) throws Exception;

    void updatePostAttr(Long postId, PostViewAttr postViewAttr) throws Exception;
    
    void createOrUpdatePostViewUser(Long userId, String avatar, UserType userType, String cover, String description, String displayName);
    
    void createOrUpdatePostViewCircle(Long circleId, String circleName, Boolean display);
    
    void createOrUpdatePostLikes(Long postId, Long userId, Boolean liked);
    
    Map<Long, MainPostSimpleWrapper> getPosts(List<Long> postIds);
    
    Map<Long, MainPostSimpleWrapper> getPostsByFeed(List<Long> feedPosts, List<Long> missPostId, WrappingPostCallBack callback);
    
    Map<Long, Creator> getCreators(List<Long> userIds);
    
    Map<Long, DPWCircle> getCircles(List<Long> circleIds);
    
    Map<Long, Boolean> getLikes(List<Long> postIds, Long userId);

    int batchDeleteByPostIds(List<Long> postIds);
}
