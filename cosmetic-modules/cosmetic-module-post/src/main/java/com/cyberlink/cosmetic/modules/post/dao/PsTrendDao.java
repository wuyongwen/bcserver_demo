package com.cyberlink.cosmetic.modules.post.dao;

import java.util.Collection;
import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.cosmetic.modules.post.model.PsTrend;
import com.cyberlink.cosmetic.modules.post.model.PsTrend.PsTrendKey;

public interface PsTrendDao extends GenericDao<PsTrend, PsTrend.PsTrendKey> {
    
    Integer listPostByGroup(String locale, Long groups, List<Long> result, BlockLimit blockLimit);
    
    List<PsTrend> findByIds(List<PsTrendKey> ids);
    
    void batchInsert(List<PsTrend> list);
    
    int deleteByPost(Long pId, String locale, Collection<Long> collection);
}
