package com.cyberlink.cosmetic.modules.post.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.dao.hibernate.ScrollableResultsCallback;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.model.AppName;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute.PostAttrType;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.model.PostType;
import com.cyberlink.cosmetic.modules.user.model.UserType;

public interface PostDao extends GenericDao<Post, Long> {
    
    /*Post findById(Long postId);
     PageResult<Post> findPostByUser(User creator, BlockLimit blockLimit);*/
    
    Post existAndNonDeleted(Long postId);
    PageResult<Post> findPostByLocale(List<String> locales, List<PostStatus> postStatus, BlockLimit blockLimit);
    PageResult<Post> findPostByUsers(List<Long> creatorIds, List<PostStatus> postStatus, Boolean withSecret, Boolean isDeleted, BlockLimit blockLimit);
    Integer findPostIdsByUsers(List<Long> creatorIds, List<PostStatus> postStatus, Boolean withSecret, Boolean withSize, List<Long> result, BlockLimit blockLimit);
    Integer findLookPostIdsByUser(Long userId, PostType postType, List<PostStatus> postStatus, Boolean withSecret, Boolean withSize, List<Long> result, BlockLimit blockLimit);
    Integer findLookPostIdsByUsers(List<Long> creatorIds, PostType postType, List<PostStatus> postStatus, List<Long> result, BlockLimit blockLimit);
    Map<Long, AppName> findLookPostSource(List<Long> postIds);
    PageResult<Post> findPostByCLUsers(List<Long> creatorIds, List<PostStatus> postStatus, BlockLimit blockLimit);
    Integer findPostIdsByCLUsers(List<Long> creatorIds, List<PostStatus> postStatus, List<Long> result, BlockLimit blockLimit);
    Long findPostIdsCountByUsers(List<Long> creatorIds, List<PostStatus> postStatus, Boolean withSecret);
    PageResult<Post> findSubPostByPost(Long postId, BlockLimit blockLimit);
    PageResult<Post> findAllRelatedPostByPost(Long postId, BlockLimit blockLimit);
    PageResult<Post> findMainPostByCreatedDateAndStatus(Date start, Date end, PostStatus status, BlockLimit blockLimit);
    PageResult<Post> findMainPostByCreatedDateAndStatus(Date start, Date end, PostStatus status, String locale, BlockLimit blockLimit);
    PageResult<Post> findPostByUsersType(UserType userType, BlockLimit blockLimit);
    void doWithAllPost(List<UserType> userTypes, String locale, BlockLimit blockLimit, ScrollableResultsCallback callback);
    void publishUnpublished(Long userId);
    List<Long> findByCreatorOrCircleAndStatus(Set<Long> userIds, Set<Long> circleIds, List<PostStatus> postStatus);
    List<Long> findAllPostByDateAndCircleAndStatus(Long circleId, Date start, Date end, PostStatus status);
    PageResult<Post> findByCreatorOrCircle(Set<Long> userIds, Set<Long> circleIds, BlockLimit blockLimit);
    Integer findPostViewByCreatorOrCircle(Set<Long> userIds, Set<Long> circleIds, List<PostStatus> postStatuses, List<Long> result, BlockLimit blockLimit);
    Integer findPostViewByCircle(Long circleId, List<String> locales, List<PostStatus> postStatus, List<Long> result, BlockLimit blockLimit);
    int bacthDeletePostByCircle(Long circleId);
    PageResult<Post> findByCircle(Long circleId, List<String> locales, List<PostStatus> postStatus, BlockLimit blockLimit);
    
    // backEndUse
    PageResult<Post> findAllPost(BlockLimit blockLimit);
    PageResult<Post> findAllActivePost(String start, String end, String activeType, BlockLimit blockLimit);
    PageResult<Post> findMainPostByDateAndStatus(Date start, Date end, PostStatus status, BlockLimit blockLimit);
    List<Long> findAllMainPostByDateAndStatus(Long creatorId, Date start, Date end, PostStatus status);
    List<Long> findAllPostIdByUsers(List<Long> creatorIds);
    List<Object> countPostByCreateTimeAndLocale(Date startTime, Date endTime, PostStatus status, String locale);
    List<Long> findMainPostIdsByCreatedDateAndStatus(Date start, Date end, PostStatus status, String locale);
    void doWithPotentialDiscoverPosts(Date startDate, Date endTime, Long minBasicBonus, BlockLimit blockLimit, ScrollableResultsCallback callback);
    Map<Long, Long> countByUserIds(List<Long> userIds);
    
    Map<Long, List<Post>> findSubPostByPostIds(Long... postIds);
    Map<Long, Long> findSubPostCountByPostIds(Long... postIds);
    Map<Long, String> findPostLocaleByPostIds(Long... postIds);

    List<Post> findByIds(Long... ids);
    void bacthDeleteByPostCreator(Long userId);
    //for solr updater usage
    Long countUndeleted();
    List<Post> findUndeleted(Integer pageIndex, Integer pageSize);
    
    void getPostCountPerUser(Date startDate, Date endTime,
            ScrollableResultsCallback callback);
    
}
