package com.cyberlink.cosmetic.modules.feed.listener;

import com.cyberlink.cosmetic.event.post.PostFanOutEvent;
import com.cyberlink.cosmetic.modules.feed.model.PoolPost;

public class PostFanOutEventListener extends
        AbstractFeedEventListener<PostFanOutEvent> {

    @Override
    public void onEvent(PostFanOutEvent e) {
        final PoolPost pp = new PoolPost(e);
        if(!pp.isValid())
            return;
        
        fanoutToAllFollower(e.getCreatorId(), e.getCircleId(), pp, e
                .getCreated().getTime());
    }

}
