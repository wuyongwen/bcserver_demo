package com.cyberlink.cosmetic.action.api.notify;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.feed.repository.FeedNotifyRepository;
import com.cyberlink.cosmetic.modules.notify.dao.NotifyDao;
import com.cyberlink.cosmetic.modules.notify.model.Notify;
import com.cyberlink.cosmetic.modules.notify.model.NotifyType;
import com.cyberlink.cosmetic.modules.post.result.AttachmentWrapper;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;

@UrlBinding("/api/notify/check-new-notify.action")
public class CheckNewNotify extends AbstractAction {
    @SpringBean("notify.NotifyDao")
    private NotifyDao notifyDao;
    
    @SpringBean("feed.feedNotifyRepository")
    private FeedNotifyRepository feedNotifyRepository;
    
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    @DefaultHandler
    public Resolution route() {
    	final Map<String, Object> results = new HashMap<String, Object>();

    	Long countYou = Long.valueOf(0);
    	if (notifyDao.checkUnReadNotifyWithType(userId, NotifyType.getYouType()))
    		countYou = Long.valueOf(1);
    	results.put("countYou", countYou);
    	
    	Long countFriend = Long.valueOf(0);
    	if (notifyDao.checkUnReadNotifyWithType(userId, NotifyType.getFriendType()))
    		countFriend = Long.valueOf(1);
    	results.put("countFriend", countFriend);
    	
    	//if(countYou > 0 || countFriend > 0)
    		// TODO: publishDurableEvent() to update notify sendTarget;
    	Notify notify = notifyDao.findNewNotifyByType(userId, NotifyType.getPostType());
    	if (notify != null && notify.getRefContent() != null) {
    		try {
    			JsonArray list = new JsonArray(notify.getRefContent());
    			JsonObject obj = list.getJsonObject(0);//new JsonObject(notify.getRefContent());
    			results.put("file", new AttachmentWrapper(obj.getLong("fileId"), obj.getString("fileType"), 
    				obj.getLong("downloadCount"), obj.getString("metadata")));
    		} catch (Exception e) {
    			results.put("file", null);
    		}
    	} else {
    		results.put("file", null);
    	}
    	
    	if(userId != null)
    	    results.put("newFeed", feedNotifyRepository.checkNewFeedNotify(userId));
    	else
    	    results.put("newFeed", null);
    	return json(results);
    }
}
