package com.cyberlink.cosmetic.modules.post.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.modules.post.event.PostCategoryUpdateEvent;
import com.cyberlink.cosmetic.modules.post.repository.TrendingRepository;

public class PostCategoryUpdateEventListener extends
		AbstractEventListener<PostCategoryUpdateEvent> {

	private TrendingRepository trendingRepository;
	
	public void setTrendingRepository(TrendingRepository trendingRepository) {
        this.trendingRepository = trendingRepository;
    }
	
	@Override
	public void onEvent(PostCategoryUpdateEvent event) {
	    if(!Constants.getPersonalTrendEnable())
	        return;
	    
		trendingRepository.addToUserList(event.getUserId());
		trendingRepository.addPostCategory(event.getUserId(), event.getCircleType());
	}

}