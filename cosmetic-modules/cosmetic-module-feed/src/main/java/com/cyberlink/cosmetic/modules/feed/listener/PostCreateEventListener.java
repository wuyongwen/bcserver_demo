package com.cyberlink.cosmetic.modules.feed.listener;

import com.cyberlink.cosmetic.event.post.PostCreateEvent;
import com.cyberlink.cosmetic.modules.feed.model.PoolPost;
import com.cyberlink.cosmetic.modules.feed.model.PoolType;
import com.cyberlink.cosmetic.modules.feed.service.SmartFeedContentGenerator;

public class PostCreateEventListener extends
        AbstractFeedEventListener<PostCreateEvent> {

    private SmartFeedContentGenerator smartFeedContentGenerator;
    
    public void setSmartFeedContentGenerator(SmartFeedContentGenerator smartFeedContentGenerator) {
        this.smartFeedContentGenerator = smartFeedContentGenerator;
    }

    @Override
    public void onEvent(PostCreateEvent e) {
        final PoolPost pp = new PoolPost(e);
        if(!pp.isValid())
            return;
        
        if(pp.getRootId() == null)
            addToCreated(pp, e.getCreated().getTime());
    }

}
