package com.cyberlink.cosmetic.modules.post.service;

import java.util.List;
import java.util.Set;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.model.Post;

public interface FeedService {
    
    PageResult<Post> listCLFeed(List<String> locale, BlockLimit blockLimit);
    PageResult<Post> listMyFeed(Long userId, List<String> locale, BlockLimit blockLimit);
    Integer listMyFeedView(Long userId, List<String> locale, List<Long> result, BlockLimit blockLimit);
    Integer listCLFeedView(List<String> locale, List<Long> result, BlockLimit blockLimit);

}