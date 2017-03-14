package com.cyberlink.cosmetic.modules.feed.repository.redis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cyberlink.cosmetic.modules.feed.repository.FeedNotifyRepository;
import com.cyberlink.cosmetic.redis.AbstractRedisRepository;
import com.cyberlink.cosmetic.redis.KeyUtils;

public class FeedNotifyRepositoryRedis extends AbstractRedisRepository implements
    FeedNotifyRepository {

    @Override
    public void addNewFeedNotify(Long userId) {
        final String key = KeyUtils.newFeedNotify(userId);
        opsForSet().add(key, userId.toString());
    }

    @Override
    public void batchAddNewFeedNotify(List<Long> userIds) {
        Map<String, Set<String>> values = new HashMap<String, Set<String>>();
        for(Long userId : userIds) {
            final String key = KeyUtils.newFeedNotify(userId);
            if(!values.containsKey(key))
                values.put(key, new HashSet<String>());
            values.get(key).add(userId.toString());
        }
        for(String key : values.keySet()) {
            Set<String> userSet = values.get(key); 
            opsForSet().add(key, userSet.toArray(new String[userSet.size()]));
        }
    }
    
    @Override
    public void removeNewFeedNotify(Long userId) {
        final String key = KeyUtils.newFeedNotify(userId);
        opsForSet().remove(key, userId.toString());
    }
    
    @Override
    public Boolean checkNewFeedNotify(Long userId) {
        final String key = KeyUtils.newFeedNotify(userId);
        return opsForSet().isMember(key, userId.toString());
    }
}
