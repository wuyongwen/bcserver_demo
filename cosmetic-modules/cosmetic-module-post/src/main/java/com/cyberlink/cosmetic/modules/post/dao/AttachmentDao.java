package com.cyberlink.cosmetic.modules.post.dao;

import java.util.Set;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.model.Attachment;

public interface AttachmentDao extends GenericDao<Attachment, Long> {
    
    Attachment getBcAttachment(Long refId);
    PageResult<Attachment> getAttachmentByPostId(Set<Long> postIds, BlockLimit blockLimit);
    
}
