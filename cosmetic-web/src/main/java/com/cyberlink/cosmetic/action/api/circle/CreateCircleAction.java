package com.cyberlink.cosmetic.action.api.circle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.event.circle.CircleCreateEvent;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeGroupDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;
import com.cyberlink.cosmetic.modules.notify.model.NotifyType;
import com.cyberlink.cosmetic.modules.notify.service.NotifyService;

@UrlBinding("/api/circle/create-circle.action")
public class CreateCircleAction extends AbstractCircleAction {
	
    @SpringBean("notify.NotifyService")
    private NotifyService notifyService;
    
    @SpringBean("circle.circleTypeDao")
    private CircleTypeDao circleTypeDao;

    private Long OTHER_CIRCLE_TYPE_GROUP_ID = 8L;
    
	private String circleName;
	private String description;
	private Long circleTypeId;
	private Boolean isSecret = false;
	private String locale;
	
	@Validate(required = true, on = "route")
    public void setToken(String token) {
        super.setToken(token);
    }
    
	public void setCircleName(String circleName) {
        this.circleName = circleName;
    }

	public void setDescription(String description) {
        this.description = description;
    }
	
	public void setCircleTypeId(Long circleTypeId) {
        this.circleTypeId = circleTypeId;
    }
	
	public void setIsSecret(Boolean isSecret) {
        this.isSecret = isSecret;
    }
	
	public void setLocale(String locale) {
        this.locale = locale;
    }
	
	@DefaultHandler
	public Resolution route() {
	    RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
	    if(!authenticate())
            return new ErrorResolution(authError);
	    if(circleTypeId == null) {
	        List<CircleType> circleTypes = circleTypeDao.listTypesByTypeGroup(OTHER_CIRCLE_TYPE_GROUP_ID, locale);
	        if(circleTypes.size() <= 0)
	            return new ErrorResolution(ErrorDef.UnknownCircleError);
	        circleTypeId = circleTypes.get(0).getId();
	    }
	    else if(!circleTypeDao.exists(circleTypeId))
	        return new ErrorResolution(ErrorDef.InvalidCircleTypeId);
		
	    Circle newCircle = new Circle();
	    newCircle.setCreatorId(getCurrentUserId());
	    newCircle.setCircleName(circleName);
	    newCircle.setCricleTypeId(circleTypeId);
	    newCircle.setDescription(description);
	    newCircle.setIsSecret(isSecret);
	    Circle newCreatedCircle = circleDao.create(newCircle);
	    if(newCreatedCircle == null)
	        return new ErrorResolution(ErrorDef.UnknownCircleError);
	    if (!isSecret) {
	    	notifyService.addFriendNotifyByType(NotifyType.CreateCircle.toString(), getCurrentUserId(), 
	    		newCreatedCircle.getId(), newCreatedCircle.getCircleName(), newCreatedCircle.getIconUrl());
	    }
	    final Map<String, Object> results = new HashMap<String, Object>();
	    results.put("circleId", newCreatedCircle.getId());
	    publishDurableEvent(new CircleCreateEvent(newCreatedCircle.getId(), getCurrentUserId(), isSecret));
		return json(results);
	}

}
