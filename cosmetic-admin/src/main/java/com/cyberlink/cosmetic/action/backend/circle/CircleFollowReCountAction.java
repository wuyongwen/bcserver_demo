package com.cyberlink.cosmetic.action.backend.circle;

import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.circle.dao.CircleAttributeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleSubscribeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleAttribute;
import com.cyberlink.cosmetic.modules.circle.model.CircleAttribute.CircleAttrType;
import com.cyberlink.cosmetic.modules.circle.service.CircleFollowReCountService;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;

@UrlBinding("/circle/circle-follow-recount.action")
public class CircleFollowReCountAction extends AbstractAction{
	@SpringBean("user.SubscribeDao")
    private SubscribeDao subscribeDao;
	
	@SpringBean("circle.circleAttributeDao")
    private CircleAttributeDao circleAttributeDao;
	
	@SpringBean("circle.circleSubscribeDao")
    private CircleSubscribeDao circleSubscribeDao;
	
	@SpringBean("circle.circleDao")
    private CircleDao circleDao;
	
	@SpringBean("circle.circleFollowReCountService")
    private CircleFollowReCountService circleFollowReCountService;
	
	private int sleep = 100;
	private int offset = 0;
	private int limit = 100;
	
	public int getSleep() {
		return sleep;
	}

	public void setSleep(int sleep) {
		this.sleep = sleep;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}


	@DefaultHandler
	public Resolution route() {

		return json(circleFollowReCountService.getStatus());
	}
	
	public Resolution start() {
		if (!getCurrentUserAdmin()) {
            return new ErrorResolution(403, "Need to login");
        }
		
		circleFollowReCountService.setSleep(sleep);
		circleFollowReCountService.setOffset(offset);
		circleFollowReCountService.setLimit(limit);
		circleFollowReCountService.startReCountThread();
		
		return new StreamingResolution("text/html", "Circle Follow recount Success");
	}
	
	public Resolution stop() {
		if (!getCurrentUserAdmin()) {
            return new ErrorResolution(403, "Need to login");
        }
		
		circleFollowReCountService.stopReCountThread();
		return json(circleFollowReCountService.getStatus());
	}
	
	public Resolution setParam() {
		if (!getCurrentUserAdmin()) {
            return new ErrorResolution(403, "Need to login");
        }
		
		circleFollowReCountService.setSleep(sleep);
		circleFollowReCountService.setOffset(offset);
		circleFollowReCountService.setLimit(limit);
		return json(circleFollowReCountService.getParam());
	}
	
	public Resolution getParam() {
		if (!getCurrentUserAdmin()) {
            return new ErrorResolution(403, "Need to login");
        }
		
		return json(circleFollowReCountService.getParam());
	}
}