package com.cyberlink.cosmetic.modules.post.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.modules.post.event.TrendUserGroupUpdateEvent;
import com.cyberlink.cosmetic.modules.post.repository.TrendingRepository;

public class TrendUserGroupUpdateEventListener extends
		AbstractEventListener<TrendUserGroupUpdateEvent> {

	private TrendingRepository trendingRepository;

	public void setTrendingRepository(TrendingRepository trendingRepository) {
		this.trendingRepository = trendingRepository;
	}

	@Override
	public void onEvent(TrendUserGroupUpdateEvent event) {
	    if(!Constants.getPersonalTrendEnable())
	        return;
	    
		trendingRepository.updateUserGroup(event.getUserId(), event.getUserGroup());
	}

}