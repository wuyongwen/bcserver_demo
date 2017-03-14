package com.cyberlink.cosmetic.modules.post.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.cosmetic.modules.post.model.PostView;
import com.cyberlink.cosmetic.modules.post.model.PostViewAttr;

public interface PostViewDao extends GenericDao<PostView, Long> {
    
    Map<Long, PostView> getViewMapByPostIds(List<Long> postIds);
    PostView findByPostId(Long postId);
    PostView createOrUpdate(Long postId, Long creatorId, String mainPost, String subPosts, PostViewAttr attribute);
    Map<Long, String> getViewMapByCLPostIds(List<Long> postIds);
    int bacthDeleteByPostId(List<Long> postIds);

}
