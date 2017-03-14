package com.cyberlink.cosmetic.modules.feed.repository;

import java.util.List;

import com.cyberlink.cosmetic.redis.Repository;

public interface FeedNotifyRepository extends Repository {

    void addNewFeedNotify(Long userId);

    void batchAddNewFeedNotify(List<Long> userIds);
    
    void removeNewFeedNotify(Long userId);

    Boolean checkNewFeedNotify(Long userId);
    
}
