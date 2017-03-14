package com.cyberlink.cosmetic.modules.user.listener;

import java.util.List;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.event.UserNameUpdateEvent;
import com.cyberlink.cosmetic.modules.user.model.Subscribe;

public class UserNameUpdateEventListener extends
        AbstractEventListener<UserNameUpdateEvent> {
	
	private SubscribeDao subscribeDao;
	
    public SubscribeDao getSubscribeDao() {
		return subscribeDao;
	}

	public void setSubscribeDao(SubscribeDao subscribeDao) {
		this.subscribeDao = subscribeDao;
	}

	@Override
    public void onEvent(UserNameUpdateEvent event) {
		int offset = 0;
		int limit = 100;
		do { // update Subscribee Name
			PageLimit pagelimit = new PageLimit(offset, limit);
			List<Subscribe> list = subscribeDao.findBySubscribeeId(event.getUserId(), pagelimit);
			for(Subscribe sub : list) {
				sub.setSubscribeeName(event.getDisplayName());
				subscribeDao.update(sub);
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				
			}
			if (list.size() < limit)
				break;
			offset += limit;
		} while(true);
		
		offset = 0;
		limit = 100;
		do { // update Subscriber Name
			PageLimit pagelimit = new PageLimit(offset, limit);
			List<Subscribe> list = subscribeDao.findBySubscriberId(event.getUserId(), pagelimit);
			for(Subscribe sub : list) {
				sub.setSubscriberName(event.getDisplayName());
				subscribeDao.update(sub);
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				
			}
			if (list.size() < limit)
				break;
			offset += limit;
		} while(true);
    }

}
