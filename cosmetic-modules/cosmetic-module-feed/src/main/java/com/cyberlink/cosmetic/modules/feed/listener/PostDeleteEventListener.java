package com.cyberlink.cosmetic.modules.feed.listener;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.event.post.PostDeleteEvent;
import com.cyberlink.cosmetic.modules.feed.model.PoolPost;
import com.cyberlink.cosmetic.modules.feed.model.PoolType;
import com.cyberlink.cosmetic.modules.feed.repository.PoolRepository;
import com.cyberlink.cosmetic.modules.user.repository.FollowRepository;
import com.cyberlink.cosmetic.redis.CursorCallback;

public class PostDeleteEventListener extends
        AbstractEventListener<PostDeleteEvent> {
    private FollowRepository followRepository;
    private PoolRepository poolRepository;

    public void setPoolRepository(PoolRepository poolRepository) {
        this.poolRepository = poolRepository;
    }

    public void setFollowRepository(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    @Override
    public void onEvent(PostDeleteEvent event) {
        final PoolPost pp = new PoolPost(event);
        final String value = pp.getValueInPool();
        deleteFromPublicCreation(event.getCreatorId(), value);
        followRepository.doWithAllFollowers(event.getCreatorId(),
                event.getCircleId(), new CursorCallback<TypedTuple<String>>() {
                    @Override
                    public void doWithCursor(Cursor<TypedTuple<String>> cursor) {
                        while (cursor.hasNext()) {
                            final String followerId = cursor.next().getValue();
                            poolRepository.deleteByValue(PoolType.Following,
                                    Long.valueOf(followerId), value);
                        }
                    }
                });
    }

    private void deleteFromPublicCreation(Long creatorId, final String value) {
        poolRepository.deleteByValue(PoolType.PublicCreation, creatorId, value);
    }
}
