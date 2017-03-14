package com.cyberlink.cosmetic.action.backend.v2.circle;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.circle.dao.CircleAttributeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleSubscribeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.action.backend.v2.circle.ListCircleByUserAction;

@UrlBinding("/v2/circle/update-circle.action")
public class UpdateCircleAction extends AbstractCircleAction {
	
    @SpringBean("circle.circleTypeDao")
	private CircleTypeDao circleTypeDao;

	@SpringBean("circle.circleDao")
	private CircleDao circleDao;
	
	@SpringBean("circle.circleAttributeDao")
    private CircleAttributeDao circleAttributeDao;
	
	@SpringBean("circle.circleSubscribeDao")
    private CircleSubscribeDao circleSubscribeDao;

	@SpringBean("post.PostService")
    private PostService postService;
	
	private String circleName;
	private String description;
	private Long circleId;
	private Long circleTypeId;
	private Boolean isSecret = false;
	private List<CircleType> availableCircleType = new ArrayList<CircleType>(0);;
    
	public void setCircleName(String circleName) {
        this.circleName = circleName;
    }
	
	public String getCircleName() {
        return this.circleName;
    }

	public void setDescription(String description) {
        this.description = description;
    }
	
	public String getDescription() {
        return this.description;
    }
	
	@Validate(required = true, on = "route")
	public void setCircleId(Long circleId) {
        this.circleId = circleId;
    }
	
	public void setCircleTypeId(Long circleTypeId) {
        this.circleTypeId = circleTypeId;
    }
	
	public Long getCircleTypeId() {
        return this.circleTypeId;
    }
	
	public void setIsSecret(Boolean isSecret) {
        this.isSecret = isSecret;
    }
	
	public Boolean getIsSecret() {
        return this.isSecret;
    }
	
	public List<CircleType> getAvailableCircleType() {
		return this.availableCircleType;
	}
	
	@DefaultHandler
	public Resolution route() {
    	if (getCurrentUser() == null) {
    		return new StreamingResolution("text/html", "Need Login");
        }
		
		Circle relatedCircle = circleDao.findById(circleId);
        Circle userCircle = getUserAccessibleCircle(relatedCircle, getCurrentUserId(), true);
        if(userCircle == null)
            return new ErrorResolution(ErrorDef.InvalidCircleNotAuth);
        
		circleName = userCircle.getCircleName();
		description = userCircle.getDescription();
		circleTypeId = userCircle.getCircleTypeId();
		isSecret = userCircle.getIsSecret();
		
		availableCircleType.clear();
		availableCircleType.addAll(circleTypeDao.listTypesByLocale(getCurrentUserLocale(), true));
		
        return forward();
	}
	
	public Resolution save() {
    	if (getCurrentUser() == null) {
    		return new StreamingResolution("text/html", "Need Login");
        }
	    
	    if(!circleDao.exists(circleId))
            return new ErrorResolution(ErrorDef.InvalidCircleId);
        
        Circle relatedCircle = circleDao.findById(circleId);
        Circle userCircle = getUserAccessibleCircle(relatedCircle, getCurrentUserId(), true);
        
        if(userCircle == null)
            return new ErrorResolution(ErrorDef.InvalidCircleNotAuth);
        
	    if(circleTypeId != null && !circleTypeDao.exists(circleTypeId))
	        return new ErrorResolution(ErrorDef.InvalidCircleTypeId);
	    
	    Boolean oldIsSecret = userCircle.getIsSecret();
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
	    
		return new RedirectResolution(ListCircleByUserAction.class, "route");
	}
	
	public Resolution cancel() {
    	if (getCurrentUser() == null) {
    		return new StreamingResolution("text/html", "Need Login");
        }
		
		return new RedirectResolution(ListCircleByUserAction.class, "route");
	}

}
