package com.cyberlink.cosmetic.modules.post.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.model.PostNewPool;
import com.cyberlink.cosmetic.modules.post.model.PostNewPool.NewPoolGroup;

public interface PostNewPoolDao extends GenericDao<PostNewPool, Long> {
    
    Long getPostCountInPool(String locale, List<NewPoolGroup> groups);
    Map<NewPoolGroup, Long> getPostCountInPoolPerGroup(String locale,
            List<NewPoolGroup> groups);
    PageResult<PostNewPool> getPostFromPool(String locale, List<NewPoolGroup> groups, BlockLimit blockLimit);
    Boolean batchCreate(List<PostNewPool> list);
    int batchSetDelete(List<Long> toDeleteIds);
    int batchRealDelete(Date begin, Date end);
    
}
