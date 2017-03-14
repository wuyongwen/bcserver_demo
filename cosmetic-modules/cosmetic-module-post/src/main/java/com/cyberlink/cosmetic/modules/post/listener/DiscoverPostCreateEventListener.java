package com.cyberlink.cosmetic.modules.post.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.event.post.DiscoverPostCreateEvent;
import com.cyberlink.cosmetic.modules.post.repository.LikeRepository;
import com.cyberlink.cosmetic.modules.post.service.PsTrendService;

public class DiscoverPostCreateEventListener extends
    AbstractEventListener<DiscoverPostCreateEvent> {

    private LikeRepository likeRepository;
    private PsTrendService psTrendService;
    
    public void setLikeRepository(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }
    
    public void setPsTrendService(PsTrendService psTrendService) {
        this.psTrendService = psTrendService;
    }

    @Override
    public void onEvent(DiscoverPostCreateEvent e) {
        if(!"true".equalsIgnoreCase(Constants.getWebsiteIsWritable()))
            return;
        if(e.getPostId() == null || e.getRegion() == null || e.getCreated() == null)
            return;
        
        likeRepository.setPromotionalLikeTarget(e.getRegion(), e.getPostId(), e.getCreated());
        psTrendService.addTrendPost(e.getPostId(), e.getRegion(), e.getCircleTypeIds(), e.getpromoteScore(), e.getHideInAll(), e.getCreated());
    }

}
