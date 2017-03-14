package com.cyberlink.cosmetic.modules.post.dao;

import java.util.List;
import java.util.Map;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.post.model.PostCircleIn;
import com.cyberlink.cosmetic.modules.user.model.User;

public interface PostCircleInDao extends GenericDao<PostCircleIn, Long>{
    
    Map<Long, User> getSourceUser(List<Long> postIds);
    Map<Long, Long> getCircleInCounts(List<Long> postIds);
    PageResult<User> listCircleInUser(Long postId, BlockLimit blockLimit);
    PageResult<Circle> listCircleInCircle(Long postId, BlockLimit blockLimit);
    List<Long> listPostIdBySource(Long userId, Long postId);
    PageResult<Circle> listCircleInCircle(Long userId, Long postId, BlockLimit blockLimit);
    PostCircleIn findByPostId(Long postId);
}
