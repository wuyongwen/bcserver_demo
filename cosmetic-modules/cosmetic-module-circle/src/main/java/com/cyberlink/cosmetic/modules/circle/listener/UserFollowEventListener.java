package com.cyberlink.cosmetic.modules.circle.listener;

import org.springframework.data.redis.core.Cursor;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.modules.circle.repository.CircleFollowRepository;
import com.cyberlink.cosmetic.modules.circle.repository.CircleRepository;
import com.cyberlink.cosmetic.modules.user.event.UserFollowEvent;
import com.cyberlink.cosmetic.redis.CursorCallback;

public class UserFollowEventListener extends
        AbstractEventListener<UserFollowEvent> {
    private CircleFollowRepository circleFollowRepository;
    private CircleRepository circleRepository;

    public void setCircleRepository(CircleRepository circleRepository) {
        this.circleRepository = circleRepository;
    }

    public void setCircleFollowRepository(
            CircleFollowRepository circleFollowRepository) {
        this.circleFollowRepository = circleFollowRepository;
    }

    @Override
    public void onEvent(final UserFollowEvent e) {
        circleRepository.doWithCircleIds(e.getFolloweeId(), new CursorCallback<String>() {

            @Override
            public void doWithCursor(Cursor<String> cursor) {
                while(cursor.hasNext()) {
                    Long circleId = Long.valueOf(cursor.next());
                    circleFollowRepository.removeCircleFollowing(e.getFollowerId(), circleId);
                    circleFollowRepository.removeExplicitFollower(e.getFollowerId(), circleId);
                }     
            }
            
        });
    }
}
