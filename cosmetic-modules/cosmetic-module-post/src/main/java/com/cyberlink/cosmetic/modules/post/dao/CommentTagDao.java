package com.cyberlink.cosmetic.modules.post.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.cosmetic.modules.post.model.CommentTag;

public interface CommentTagDao extends GenericDao<CommentTag, Long> {
    
    List<CommentTag> listCommentTag(Long... commentIds);
    
}
