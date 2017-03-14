package com.cyberlink.cosmetic.modules.post.dao;

import java.util.Date;
import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.model.PsTrendPool;
import com.cyberlink.cosmetic.modules.post.model.PsTrendPool.PsTrendPoolKey;

public interface PsTrendPoolDao extends GenericDao<PsTrendPool, PsTrendPool.PsTrendPoolKey> {
    
    PageResult<PsTrendPool> findByCircleType(Integer bucket, Long circleTypeId, Date from, BlockLimit blockLimit);
    List<PsTrendPool> findByIds(List<PsTrendPoolKey> ids);
    void batchInsert(List<PsTrendPool> list);
    Integer batchDelete(Integer bucket);
    int deleteByPost(Long pId, List<Long> circleTypeIds);
}
