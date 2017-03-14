package com.cyberlink.cosmetic.modules.circle.listener;

import java.util.Date;

import org.springframework.data.redis.core.Cursor;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.event.circle.CircleUnfollowEvent;
import com.cyberlink.cosmetic.modules.circle.repository.CircleFollowRepository;
import com.cyberlink.cosmetic.modules.circle.repository.CircleRepository;
import com.cyberlink.cosmetic.modules.user.repository.FollowRepository;
import com.cyberlink.cosmetic.redis.CursorCallback;

public class CircleUnfollowEventListener extends
        AbstractEventListener<CircleUnfollowEvent> {
    private CircleFollowRepository circleFollowRepository;
    private CircleRepository circleRepository;
    private FollowRepository followRepository;
    
    public void setCircleFollowRepository(
            CircleFollowRepository circleFollowRepository) {
        this.circleFollowRepository = circleFollowRepository;
    }
    
    public void setCircleRepository(
            CircleRepository circleRepository) {
        this.circleRepository = circleRepository;
    }
    
    public void setFollowRepository(
            FollowRepository followRepository) {
        this.followRepository = followRepository;
    }
    
    @Override
    public void onEvent(CircleUnfollowEvent e) {
        final Long followerId = e.getFollowerId();
        circleFollowRepository.removeExplicitFollower(followerId,
                e.getCircleId());
        circleFollowRepository.removeCircleFollowing(followerId,
                e.getCircleId());
        if(!followRepository.getIsFollowing(followerId, e.getCircleCreatorId()))
            return;
        
        final Long unfollowCircleId = e.getCircleId();
        circleRepository.doWithCircleIds(e.getCircleCreatorId(), new CursorCallback<String>() {
            @Override
            public void doWithCursor(Cursor<String> cursor) {
                while (cursor.hasNext()) {
                    Long currentCircleId = Long.valueOf(cursor.next());
                    if(unfollowCircleId.equals(currentCircleId))
                        continue;
                    circleFollowRepository.addExplicitFolower(followerId, currentCircleId, new Date());
                    circleFollowRepository.addCircleFollowing(followerId, currentCircleId, new Date());
                }
            }
        });
        
    }
}
