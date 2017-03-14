package com.cyberlink.cosmetic.modules.post.service;

import java.util.Date;
import java.util.List;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.model.PostTopKeyword;
import com.cyberlink.cosmetic.modules.post.result.FullPostWrapper;

public interface RelatedPostService {
    
    public Long getRelatedPostIds(Long postId, String locale, Integer offset, Integer limit, List<Long> relatedPostIds);
    public void insertRelatedPostIds(String locale, FullPostWrapper fpw);
    public void generatePostIdsByKeyword(Date deleteTime);
    public void deleteOldRecord(Date deleteTime);
    public Integer getKeywordBucketId(Date currentDate);
    public PageResult<PostTopKeyword> listPostByTopKeyword(String locale, Integer kLimit, Date dateTime);
}
