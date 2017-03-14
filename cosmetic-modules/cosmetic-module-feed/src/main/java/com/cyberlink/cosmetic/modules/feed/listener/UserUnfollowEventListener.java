package com.cyberlink.cosmetic.modules.feed.listener;

import org.springframework.data.redis.core.Cursor;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.modules.circle.repository.CircleRepository;
import com.cyberlink.cosmetic.modules.feed.model.PoolType;
import com.cyberlink.cosmetic.modules.feed.repository.PoolRepository;
import com.cyberlink.cosmetic.modules.user.event.UserUnfollowEvent;
import com.cyberlink.cosmetic.redis.CursorCallback;

public class UserUnfollowEventListener extends
        AbstractEventListener<UserUnfollowEvent> {
    private PoolRepository poolRepository;
    private CircleRepository circleRepository;

    public void setPoolRepository(PoolRepository poolRepository) {
        this.poolRepository = poolRepository;
    }

    public void setCircleRepository(CircleRepository circleRepository) {
        this.circleRepository = circleRepository;
    }

    @Override
    public void onEvent(final UserUnfollowEvent event) {
        poolRepository.deleteByCreatorId(PoolType.Following,
                event.getFollowerId(), event.getFolloweeId());
        circleRepository.doWithCircleIds(event.getFolloweeId(),
                new CursorCallback<String>() {
                    @Override
                    public void doWithCursor(Cursor<String> cursor) {
                        while (cursor.hasNext()) {
                            final String circleId = cursor.next();
                            poolRepository.deleteByCircleId(PoolType.Following,
                                    event.getFollowerId(),
                                    Long.valueOf(circleId));
                        }
                    }
                });
    }
}
