package com.cyberlink.cosmetic.modules.post.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.model.PostReported;
import com.cyberlink.cosmetic.modules.post.model.PostReported.PostReportedStatus;
import com.cyberlink.cosmetic.modules.post.result.PostReportedWrapper;
import com.cyberlink.cosmetic.modules.user.model.User;

public interface PostReportedDao extends GenericDao<PostReported, Long> {
    
    PageResult<PostReported> findByTarget(Object target, BlockLimit blockLimit);
    
    PageResult<PostReported> findByTargetAndUser(Object target, User reporter, BlockLimit blockLimit);

    List<PostReported> getByTargets(String refType, PostReportedStatus status, Long... targetIds);
    
    // For backend Use
    PageResult<PostReportedWrapper> getReportedPostCount(Long searchAuthorId, Long searchReportedId, String refType, PostReportedStatus status, String region, BlockLimit blockLimit);

    PageResult<PostReportedWrapper> getRelatedPostComment(Long searchAuthorId, PostReportedStatus status, String region, BlockLimit blockLimit);

}
