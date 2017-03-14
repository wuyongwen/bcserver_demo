package com.cyberlink.cosmetic.modules.post.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailType;
import com.cyberlink.cosmetic.modules.post.model.AppName;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostType;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute.PostAttrType;
import com.cyberlink.cosmetic.modules.post.model.PostReported;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.model.PostReported.PostReportedStatus;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserAttr;
import com.cyberlink.cosmetic.modules.user.model.UserType;

public interface PostService {
    
    enum PostOption {
        ForceHideInAll;
    };
    
    PostApiResult <Post> createPost(Long creatorId, String locale, String countryCode, String title, String content, List<Long> circleIds, String jAttachments, String jTags, PostStatus postStatus, String source, AppName appName, PostType postType, Long promoteScore, Long lookTypeId, String extLookUrl, Map<PostOption, String> opts);
    PostApiResult <Post> createPost(Long creatorId, String locale, String countryCode, String title, String content, List<Long> circleIds, String jAttachments, String jTags, PostStatus postStatus, String source, AppName appName, PostType postType, Long promoteScore, Long lookTypeId, String extLookUrl, Date createdTime, Map<PostOption, String> opts);
    PostApiResult <Post> createSubPost(Long userId, Long postId, String content, String jAttachments, String jTags, String extLookUrl, PostStatus postStatus, PostType postType);
    PostApiResult <List<Post>> createPosts(Long creatorId, String locale, String countryCode, String mainPost, String source, AppName appName,Long promoteScore, List<String> subPosts, Map<PostOption, String> opts);
    PostApiResult <List<Post>> createPosts_v2(Long creatorId, String locale, String countryCode, String mainPost, String source, AppName appName, Long promoteScore, List<String> subPosts, Map<PostOption, String> opts);
    PostApiResult <Post> queryPostById(Long postId);
    PostApiResult <PageResult<Post>> listPostByCircle(Long circleId, Long circleTagId, List<String> locales, List<PostStatus> postStatus, String sortBy, Boolean withLook, BlockLimit blockLimit);
    PostApiResult <PageResult<Post>> listPostByCircle_v3(Long circleId, Long circleTypeId, List<String> locales, List<PostStatus> postStatus, String sortBy, Boolean withLook, BlockLimit blockLimit);
    PostApiResult<PageResult<Post>> listNewPostByCircle(Long circleId, Long circleTagId, List<String> locales, List<PostStatus> postStatus, Boolean withLook, BlockLimit blockLimit);
    PostApiResult<PageResult<Post>> listNewPostByCircle_v3(Long circleId, Long circleTypeId, List<String> locales, List<PostStatus> postStatus, Boolean withLook, BlockLimit blockLimit);
    PostApiResult <PageResult<Post>> listPostByUsers(List<Long> userIds, List<PostStatus> postStatus, Boolean withSecret, BlockLimit blockLimit);
    PostApiResult <PageResult<Post>> listSubPost(Long postId, BlockLimit blockLimit);
    PostApiResult <PageResult<Post>> listAllRelatedPost(Long postId, BlockLimit blockLimit);
    Post updatePost(Post post);
    PostApiResult <Post> updatePost(Long creatorId, String locale, Long postId, AppName appName, PostType postType, String title, String content, List<Long> circleIds, String jAttachments, String jTags, PostStatus postStatus, Long promoteScore, String extLookUrl, Long lookTypeId, Date createdTime);
    PostApiResult<Post> updateSubPost(Long creatorId, Long mainPostId, Long subPostId, String content, String jAttachments, String extLookUrl, String jTags);
    PostApiResult <List<Post>> updatePosts(Long creatorId, String locale, AppName appName, String mainPost, String source, Long promoteScore, List<String> updateSubPosts, List<String> deleteSubPosts, List<String> newSubPosts);
    String getPostQRCode(Long postId);
    Map<Long, List<Object>> listFileItemByPosts(List<Post> posts, ThumbnailType thumbnailType);
    PageResult<Post> listPostBetweenDate(Date start, Date end, PostStatus postStatus, BlockLimit blockLimit);
    PostApiResult <Boolean> reportPost(Long reportedId, Long postId, String reason);
    Throwable handleReportPost(Long postId, User reviewer, String result, String remark);
    Boolean reportContestPost(Long postId, String locale);
    Map<Long, List<Circle>> listCircleByPosts(List<Post> posts);
    PostApiResult <Boolean> deletePost(Long userId, Long postId);
    PostApiResult<Boolean> deleteSubPost(Long userId, Long postId);
    Map<Long, List<PostReported>> getReportedPostReason(PostReportedStatus status, List<Long> postIds);
    Map<Long, List<Post>> listSubPostByPosts(List<Post> posts);
    List<Post> findPostByIds(List<Long>ids);
    Map<Long, List<FileItem>> listUserItem(List<User> users);
    Post duplicatePost(Post src, Long userId, String countryCode, Long circleIdOffset);
    Post createMainPost(Post post, Boolean isSecret, Date createdTime);
    Post updateMainPost(Post post, Boolean descPostCount, PostStatus originalStatus, Date createdTime, Boolean updatePost);
    PostApiResult <Post> circleInPost(Long creatorId, String countryCode, Long postId, Long circleId, String newTitle, PostType podstType);
    PageResult<User> listCircleInUser(Long postId, BlockLimit blockLimit);
    PageResult<Circle> listCircleInCircle(Long postId, BlockLimit blockLimit);
    Map<Long, Long> listCircleInCount(List<Long> postIds);
    Map<Long, Map<PostAttrType, Long>> listPostsAttr(List<Long> postIds);
    Map<Long, User> listCircleInSourceUserByPosts(List<Long> postIds);
    void deletePostByCircle(Long userId, Boolean isSecret, Long circleId);
    void checkPostNewByCircle(Long creatorId, Long circleId, Boolean isSecret);
    List<Throwable> handleRescueTask(Long handlerId, Map<String, Object> rescueTask);
    List<Throwable> handleTrendRescueTask(Long handlerId, Map<String, Object> rescueTask, Long selCircleTypeId);
    PageResult<Circle> listExCircleInCircle(Long userId, Long postId, BlockLimit blockLimit);
    PostApiResult<Integer> listPostByCircle_v3_1(Long circleId, Long circleTypeId, List<String> locales, List<PostStatus> postStatus, String sortBy, List<Long> result, Boolean withLook, BlockLimit blockLimit, boolean disableCache);
    PostApiResult<Integer> listNewPostByCircle_v3_1(Long circleId, Long circleTypeId, List<String> locales, List<PostStatus> postStatus, List<Long> result, Boolean withLook, BlockLimit blockLimit, boolean disablecache);
    PostApiResult <Integer> listPostByUsers_v3_1(List<Long> userIds, List<PostStatus> postStatus, Boolean withSecret, List<Long> result, BlockLimit blockLimit);
    PostApiResult<Integer> listLookPostByUser(Long userId, UserAttr userAttr, PostType postType, List<PostStatus> postStatus, Boolean withSecret, List<Long> result, BlockLimit blockLimit);
    PostApiResult<Integer> listLookPostByUserType(UserType userType, List<String> locale, PostType postType, List<PostStatus> postStatus, List<Long> result, BlockLimit blockLimit);
    PostApiResult<Long> listPostCountByUsers_v3_1(List<Long> userIds, List<PostStatus> postStatus, Boolean withSecret);
    PostApiResult<Integer> listPostByLookType(Long lookTypeId, PostType postType, String locale, List<PostStatus> postStatus, List<Long> result, BlockLimit blockLimit);
    PageResult<User> getTopPostCountUserByUserIds(List<Long> idList,Long offset, Long limit);
    
    Set<String> extractHashtagsFromText(String text);
}
