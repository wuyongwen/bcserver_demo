package com.cyberlink.cosmetic.modules.post.dao;

import java.util.Date;
import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.model.PostRescue;
import com.cyberlink.cosmetic.modules.post.model.PostRescue.RescueType;

public interface PostRescueDao extends GenericDao<PostRescue, Long> {

    PageResult<PostRescue> getPostRescueBetween(Long handlerId, String postLocale, Date begin, Date end, Boolean isHandled, BlockLimit blockLimit);
    PostRescue getLastRecord(String postLocale, Boolean isHandled);
    Boolean batchCreate(List<PostRescue> list);
    List<Long> findExPostIds(List<Long> postIds, RescueType rescueType, Boolean isHandled);
    List<PostRescue> findByIds(List<Long> ids);
}
