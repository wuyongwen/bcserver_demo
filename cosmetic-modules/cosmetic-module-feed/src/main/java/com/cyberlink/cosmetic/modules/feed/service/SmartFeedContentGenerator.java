package com.cyberlink.cosmetic.modules.feed.service;

import com.cyberlink.cosmetic.modules.feed.model.PoolPost;
import com.cyberlink.cosmetic.modules.feed.model.PoolType;

public interface SmartFeedContentGenerator {
    void generate(String locale, String sourceHashKey, String targetHashKey,
            int numToRetrieve);
    void directToFeed(String targetHashKey, PoolType poolType, PoolPost post);
    
}
