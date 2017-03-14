package com.cyberlink.cosmetic.action.api.notify;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.notify.model.NotifyType;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.event.NotificationMessageUpdateEvent;
import com.cyberlink.cosmetic.modules.user.model.User;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/notify/set-notify-setting.action")
public class SetNotifySetting extends AbstractAction{
    
    // FIXME
    private static final String param_notification_message = "FollowUser";

    @SpringBean("user.UserDao")
    private UserDao userDao;
    
    private List<String> nonNotifyType = new ArrayList<String>();
    
    private String uuid;
    private String app;

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setApp(String app) {
        this.app = app;
    }

    @DefaultHandler
    public Resolution route() {
        RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
    	if (!authenticate())
    		return new ErrorResolution(authError); 
    	checkCommnetType(nonNotifyType);
    	User user = getSession().getUser();
    	user.setListInAttr("notifyList", nonNotifyType);
    	userDao.update(user);
    	publishNotificationMessageUpdateEventIfNecessary();
    	return success();
    }
    
    private void publishNotificationMessageUpdateEventIfNecessary() {
        Boolean enabled = Boolean.TRUE;
        for (final String t : nonNotifyType) {
            if (StringUtils.equalsIgnoreCase(t, param_notification_message)) {
                enabled = Boolean.FALSE;
                break;
            }
        }
        publishDurableEvent(new NotificationMessageUpdateEvent(getSession()
                .getUserId(), app, uuid, enabled));
    }
    
    private void checkCommnetType(List<String> nonNotifyType) {
    	if (nonNotifyType != null && nonNotifyType.contains(NotifyType.CommentPost.toString())) {
    		nonNotifyType.add(NotifyType.ReplyToCommentOwner.toString());
    		nonNotifyType.add(NotifyType.ReplyToPostOwner.toString());
    		
    	}
    }

    public List<String> getNonNotifyType() {
		return nonNotifyType;
	}
	public void setNonNotifyType(List<String> nonNotifyType) {
		this.nonNotifyType = nonNotifyType;
	}

}
