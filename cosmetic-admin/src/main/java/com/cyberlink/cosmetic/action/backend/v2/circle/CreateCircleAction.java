package com.cyberlink.cosmetic.action.backend.v2.circle;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.circle.dao.CircleAttributeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleSubscribeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;
import com.cyberlink.cosmetic.action.backend.v2.circle.ListCircleByUserAction;

@UrlBinding("/v2/circle/create-circle.action")
public class CreateCircleAction extends AbstractCircleAction {
	
    @SpringBean("circle.circleTypeDao")
	private CircleTypeDao circleTypeDao;

	@SpringBean("circle.circleDao")
	private CircleDao circleDao;
	
	@SpringBean("circle.circleAttributeDao")
    private CircleAttributeDao circleAttributeDao;
	
	@SpringBean("circle.circleSubscribeDao")
    private CircleSubscribeDao circleSubscribeDao;

	private String circleName;
	private String description;
	private Long circleTypeId;
	private Boolean isSecret = false;
	private List<CircleType> availableCircleType = new ArrayList<CircleType>(0);;
    
	private Long OTHER_CIRCLE_TYPE_GROUP_ID = 8L;
	
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
		
		availableCircleType.clear();
		availableCircleType.addAll(circleTypeDao.listTypesByLocale(getCurrentUserLocale(), true));
		
        return forward();
	}
	
	public Resolution create() {
    	if (getCurrentUser() == null) {
    		return new StreamingResolution("text/html", "Need Login");
        } 
	    
	    if(circleTypeId == null) {
	        List<CircleType> circleTypes = circleTypeDao.listTypesByTypeGroup(OTHER_CIRCLE_TYPE_GROUP_ID, getCurrentUserLocale());
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

		return new RedirectResolution(ListCircleByUserAction.class, "route");
	}
	
	public Resolution cancel() {
    	if (getCurrentUser() == null) {
    		return new StreamingResolution("text/html", "Need Login");
        } 
		
		return new RedirectResolution(ListCircleByUserAction.class, "route");
	}

}
