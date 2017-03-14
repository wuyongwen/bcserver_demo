package com.cyberlink.cosmetic.action.api.circle;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.event.circle.CircleCloseEvent;
import com.cyberlink.cosmetic.event.circle.CircleOpenEvent;
import com.cyberlink.cosmetic.event.circle.CircleUpdateEvent;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.post.event.PostViewUpdateEvent;
import com.cyberlink.cosmetic.modules.post.service.PostService;

@UrlBinding("/api/circle/update-circle.action")
public class UpdateCircleAction extends AbstractCircleAction {
	
    @SpringBean("circle.circleTypeDao")
	private CircleTypeDao circleTypeDao;

	@SpringBean("circle.circleDao")
	private CircleDao circleDao;
	
	@SpringBean("post.PostService")
	private PostService postService;

	private String circleName;
	private String description;
	private Long circleId;
	private Long circleTypeId;
	private Boolean isSecret = false;
	
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
	
	@Validate(required = true, on = "route")
	public void setCircleId(Long circleId) {
        this.circleId = circleId;
    }
	
	public void setCircleTypeId(Long circleTypeId) {
        this.circleTypeId = circleTypeId;
    }
	
	public void setIsSecret(Boolean isSecret) {
        this.isSecret = isSecret;
    }
	
	@DefaultHandler
	public Resolution route() {
	    RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
	    if(!authenticate())
            return new ErrorResolution(authError);
	    
	    if(!circleDao.exists(circleId))
            return new ErrorResolution(ErrorDef.InvalidCircleId);
        
		Circle relatedCircle = circleDao.findById(circleId, false);
		if (relatedCircle == null)
			return null; //the circle is already deleted
		
        Circle userCircle = getUserAccessibleCircle(relatedCircle, getCurrentUserId(), true);
        Boolean oldIsSecret = userCircle.getIsSecret();
        
        if(userCircle == null)
            return new ErrorResolution(ErrorDef.InvalidCircleNotAuth);
        
	    if(circleTypeId != null && !circleTypeDao.exists(circleTypeId))
	        return new ErrorResolution(ErrorDef.InvalidCircleTypeId);
	    
	    if(circleName != null && !circleName.equals(userCircle.getCircleName())) {
	        userCircle.setCircleName(circleName);
	        if(userCircle.getDefaultType() != null)
	            userCircle.setIsCustomized(true);
	    }
	    if(circleTypeId != null) 
	        userCircle.setCricleTypeId(circleTypeId);
	    if(description != null) 
	        userCircle.setDescription(description);
	    if(isSecret != null) 
	        userCircle.setIsSecret(isSecret);

	    Circle updatedCircle = circleDao.update(userCircle);
	    if(updatedCircle == null)
	        return new ErrorResolution(ErrorDef.UnknownCircleError);
	    
		if (oldIsSecret != updatedCircle.getIsSecret()) // the circle status had been changed
			postService.checkPostNewByCircle(getCurrentUserId(), userCircle.getId(), updatedCircle.getIsSecret());
	    
	    publishCircleChangeEvent(updatedCircle.getCircleCreatorId(), updatedCircle.getId(),
                oldIsSecret, updatedCircle.getIsSecret());
	    publishDurableEvent(new PostViewUpdateEvent(updatedCircle.getId(), updatedCircle.getCircleName(), updatedCircle.getIsSecret()));
        publishDurableEvent(new CircleUpdateEvent(updatedCircle.getId(), getCurrentUserId()));
	    final Map<String, Object> results = new HashMap<String, Object>();
	    results.put("circleId", updatedCircle.getId());
		return json(results);
	}

    private void publishCircleChangeEvent(Long creatorId, Long circleId, Boolean oldSecret,
            Boolean newSecret) {
        if (oldSecret == null) {
            return;
        }
        if (newSecret == null) {
            return;
        }
        if (oldSecret.equals(newSecret)) {
            return;
        }
        if (newSecret.booleanValue())
            publishDurableEvent(new CircleCloseEvent(creatorId, circleId));
        else
            publishDurableEvent(new CircleOpenEvent(creatorId, circleId));
        
    }

}
