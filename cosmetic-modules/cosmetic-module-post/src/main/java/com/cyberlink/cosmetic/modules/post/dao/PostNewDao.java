package com.cyberlink.cosmetic.modules.post.dao;

import java.util.Date;
import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.dao.hibernate.ScrollableResultsCallback;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.model.AppName;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute.PostAttrType;
import com.cyberlink.cosmetic.modules.post.model.PostNew;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.model.PostType;

public interface PostNewDao extends GenericDao<PostNew, Long> {
    
    PageResult<Post> findNewPost(Long circleTypeId, List<String> locales, List<PostStatus> status, Boolean withLook, BlockLimit blockLimit);
    PostNew getLastModifiedRecord();
    List<PostNew> getPostNewByPosts(List<Post> posts, Boolean isDeleted);
    PageResult<PostNew> getByPostCreatedDate(Date startTime, Date endTime, List<Long> circleTypeIds, Long minBonus, BlockLimit blockLimit);
    Integer findNewPostView(Long circleTypeId, List<String> locales, List<PostStatus> status, List<Long> result, Boolean withLook, BlockLimit blockLimit, boolean disableCache);
    Integer findNewPostViewByLook(Long lookTypeId, String locale, PostType postType, List<PostStatus> status, List<Long> result, BlockLimit blockLimit);
    Boolean batchCreate(List<PostNew> list);
    int batchCheck(List<Long> postIds, Boolean isDeleted);
    void doWithAllTrendPost(String locale, Long circleTypeId, Date begin, Date end, ScrollableResultsCallback callback);
    List<PostNew> findByPost(Long postId, Boolean isDeleted);
    
    void getLikeCirInCountPerPost(PostAttrType attrType, Date startTime,
            Date endTime, ScrollableResultsCallback callback);
    void getGenPerPost(String locale, Boolean promoted, Date startTime,
            Date endTime, ScrollableResultsCallback callback);
}
