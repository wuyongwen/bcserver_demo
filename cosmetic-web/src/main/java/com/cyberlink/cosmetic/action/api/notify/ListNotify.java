package com.cyberlink.cosmetic.action.api.notify;

import java.util.HashMap;
import java.util.Map;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.notify.dao.NotifyDao;
import com.cyberlink.cosmetic.modules.notify.model.Notify;
import com.cyberlink.cosmetic.modules.notify.model.NotifyType;
import com.cyberlink.cosmetic.modules.user.model.SessionStatus;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/notify/list-notify.action")
public class ListNotify extends AbstractAction {
    @SpringBean("notify.NotifyDao")
    private NotifyDao notifyDao;

	Long offset = Long.valueOf(0);
    Long limit = Long.valueOf(20);
    String type = "You";
    

	@DefaultHandler
    public Resolution route() {
    	final Map<String, Object> results = new HashMap<String, Object>();
    	if (!authenticate())
    		return new ErrorResolution(authError); 
        else if (getSession().getStatus() == SessionStatus.Invalied)
        	return new ErrorResolution(ErrorDef.AccountEmailDeleted);
    	PageResult<Notify> page;
    	if (type.equalsIgnoreCase("You")) {
    		page = notifyDao.findNotifyByType(getSession().getUserId(), NotifyType.getYouType(), offset, limit);
    	} else {
    		page = notifyDao.findNotifyByType(getSession().getUserId(), NotifyType.getFriendType(), offset, limit);
    	}
    	results.put("results", page.getResults());
    	results.put("totalSize", page.getTotalSize());
    	return json(results);
    }

	public Long getOffset() {
		return offset;
	}

	public void setOffset(Long offset) {
		this.offset = offset;
	}

	public Long getLimit() {
		return limit;
	}

	public void setLimit(Long limit) {
		this.limit = limit;
	}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
