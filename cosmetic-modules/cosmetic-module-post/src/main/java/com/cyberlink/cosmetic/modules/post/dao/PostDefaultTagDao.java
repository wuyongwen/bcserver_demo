package com.cyberlink.cosmetic.modules.post.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.cosmetic.modules.post.model.PostDefaultTag;

public interface PostDefaultTagDao extends GenericDao<PostDefaultTag, Long>{
    
    List<PostDefaultTag> listByLocale(String locale);

}
