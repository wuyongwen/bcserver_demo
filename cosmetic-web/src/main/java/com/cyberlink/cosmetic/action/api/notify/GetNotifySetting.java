package com.cyberlink.cosmetic.action.api.notify;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.notify.model.NotifyType;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;

@UrlBinding("/api/notify/get-notify-setting.action")
public class GetNotifySetting extends AbstractAction{
    @SpringBean("user.UserDao")
    private UserDao userDao;

    private Long userId;
	
    
    @DefaultHandler
    public Resolution route() {
    	if (!userDao.exists(userId)) {
    		return new ErrorResolution(ErrorDef.InvalidUserId);
    	}
    	final Map<String, Object> results = new HashMap<String, Object>();
    	User user = userDao.findById(userId);
    	List<String> nonNotifyType = user.getListInAttr("notifyList");
    	removeReplyType(nonNotifyType);
    	results.put("nonNotifyType", nonNotifyType);
    	return json(results);
    }
    
    public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	private void removeReplyType(List<String> nonNotifyType) {
		nonNotifyType.removeAll(Arrays.asList(NotifyType.ReplyToCommentOwner.toString(), 
				NotifyType.ReplyToPostOwner.toString()));
    }
}