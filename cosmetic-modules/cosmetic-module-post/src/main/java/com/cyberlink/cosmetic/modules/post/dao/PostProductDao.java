package com.cyberlink.cosmetic.modules.post.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostProduct;

public interface PostProductDao extends GenericDao<PostProduct, Long> {
    
    List<PostProduct> listByPost(Post post);
    List<PostProduct> listByPost(Post post, Boolean isExternal);
    Long getPostProductCount(Post post);
    
}
