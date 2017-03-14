package com.cyberlink.cosmetic.modules.feed.listener;

import java.util.LinkedHashMap;
import java.util.Map;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.modules.feed.model.PoolPost;
import com.cyberlink.cosmetic.modules.feed.model.PoolType;
import com.cyberlink.cosmetic.modules.feed.repository.PoolRepository;
import com.cyberlink.cosmetic.modules.user.event.UserFollowEvent;

public class UserFollowEventListener extends
        AbstractEventListener<UserFollowEvent> {

    private Integer numToFanout = 3;
    private PoolRepository poolRepository;

    public void setNumToFanout(Integer numToFanout) {
        this.numToFanout = numToFanout;
    }

    public void setPoolRepository(PoolRepository poolRepository) {
        this.poolRepository = poolRepository;
    }

    @Override
    public void onEvent(UserFollowEvent event) {
        final Map<PoolPost, Double> m = poolRepository.rangeWithScores(
                PoolType.PublicCreation, event.getFolloweeId().toString(), 1,
                numToFanout);
        Map<String, Double> scoreMap = new LinkedHashMap<String, Double>();
        for (final Map.Entry<PoolPost, Double> e : m.entrySet()) {
            scoreMap.put(e.getKey().getValueInPool(), e.getValue());
        }
        
        poolRepository.add(PoolType.Following, event.getFollowerId()
                .toString(), scoreMap);
    }

}
