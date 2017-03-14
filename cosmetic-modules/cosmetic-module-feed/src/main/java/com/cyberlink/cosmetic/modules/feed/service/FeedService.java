package com.cyberlink.cosmetic.modules.feed.service;

import java.util.List;

import com.cyberlink.cosmetic.modules.feed.model.FeedPost;

public interface FeedService {
    List<FeedPost> retrieve(Long userId, List<String> locales, String init,
            int offset, int limit);

    List<FeedPost> retrieve(Long userId, String locale, String init,
            int offset, int limit);
    
    List<FeedPost> retrieveByNext(Long userId, List<String> locales, String init,
            Long next, int limit);

    List<FeedPost> retrieveByNext(Long userId, String locale, String init,
            Long next, int limit);
}
