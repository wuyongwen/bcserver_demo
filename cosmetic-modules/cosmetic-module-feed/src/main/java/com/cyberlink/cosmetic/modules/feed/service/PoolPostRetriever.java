package com.cyberlink.cosmetic.modules.feed.service;

import java.util.List;

import com.cyberlink.cosmetic.modules.feed.model.PoolPost;
import com.cyberlink.cosmetic.modules.feed.model.PoolType;

public interface PoolPostRetriever {
    List<PoolPost> retrieve(PoolType poolType, String locale,
            String sourceHashKey, String targetHashKey);
}
