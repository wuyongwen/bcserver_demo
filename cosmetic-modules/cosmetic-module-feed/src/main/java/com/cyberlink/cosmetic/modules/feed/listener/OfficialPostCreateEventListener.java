package com.cyberlink.cosmetic.modules.feed.listener;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.event.post.OfficialPostCreateEvent;
import com.cyberlink.cosmetic.modules.feed.model.FeedPost;
import com.cyberlink.cosmetic.modules.feed.model.PoolPost;
import com.cyberlink.cosmetic.modules.feed.model.PoolType;
import com.cyberlink.cosmetic.modules.feed.repository.FeedRepository;
import com.cyberlink.cosmetic.modules.feed.repository.PoolRepository;

public class OfficialPostCreateEventListener extends
        AbstractEventListener<OfficialPostCreateEvent> {

    private PoolRepository poolRepository;
    private FeedRepository feedRepository;

    public void setFeedRepository(FeedRepository feedRepository) {
        this.feedRepository = feedRepository;
    }

    public void setPoolRepository(PoolRepository poolRepository) {
        this.poolRepository = poolRepository;
    }

    @Override
    public void onEvent(OfficialPostCreateEvent e) {
        if(e.getPostId() == null)
            return;
        
        addToPool(e);
        addToAnonymousFeed(e);
    }

    private void addToAnonymousFeed(OfficialPostCreateEvent e) {
        if (StringUtils.isBlank(e.getRegion())) {
            return;
        }
        if (e.getPostId() == null) {
            return;
        }

        final FeedPost fp = new FeedPost(PoolType.Official, e.getPostId(),
                Boolean.FALSE);
        feedRepository.add(StringUtils.lowerCase(e.getRegion()),
                Arrays.asList(fp));
    }

    private void addToPool(OfficialPostCreateEvent e) {
        String region = e.getRegion();
        Long userId = e.getCreatorId();
        Long postId = e.getPostId();
        Long circleId = e.getCircleId();
        Long createdTime = e.getCreated().getTime();
        if (region == null || userId == null || postId == null
                || createdTime == null || circleId == null) {
            return;
        }

        PoolPost pp = new PoolPost(postId, userId, circleId);
        poolRepository.add(PoolType.Official, region.toLowerCase(),
                pp.getValueInPool(), createdTime);
    }

}
