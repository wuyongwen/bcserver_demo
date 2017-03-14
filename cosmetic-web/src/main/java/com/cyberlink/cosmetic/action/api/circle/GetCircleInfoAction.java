package com.cyberlink.cosmetic.action.api.circle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.circle.dao.CircleAttributeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleSubscribeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.Circle.UserCicleView;
import com.google.common.collect.ImmutableList;

// get circle information by circle ID
@UrlBinding("/api/circle/get-circleinfo.action")
public class GetCircleInfoAction extends AbstractCircleAction {
	
	@SpringBean("circle.circleDao")
	private CircleDao circleDao;
	
	@SpringBean("circle.circleAttributeDao")
    private CircleAttributeDao circleAttributeDao;
    
    @SpringBean("circle.circleSubscribeDao")
    private CircleSubscribeDao circleSubscribeDao;
    
	private Long circleId;
	private Long circleCreatorId;
	private Long curUserId;
	
	@Validate(required = true, on = "route")
	public void setCircleId(Long circleId) {
		this.circleId = circleId;
	}

	public void setCircleCreatorId(Long circleCreatorId) {
        this.circleCreatorId = circleCreatorId;
    }
	
	public void setCurUserId(Long curUserId) {
        this.curUserId = curUserId;
    }
	
	@DefaultHandler
	public Resolution route() {
    	
		final Map<String, Object> results = new HashMap<String, Object>();
		Circle relatedCircle = circleDao.findById(circleId, false);
		if (relatedCircle == null)
			return new ErrorResolution(ErrorDef.InvalidCircleId); //the circle is already deleted
		
		if (circleCreatorId == null) {
			if (relatedCircle.getCreatorId() == null) {
				return new ErrorResolution(ErrorDef.UnknownCircleError);
			} else {
				circleCreatorId = relatedCircle.getCreatorId();
			}
		}
		Circle userCircle = getUserAccessibleCircle(relatedCircle, circleCreatorId, false);
		if(userCircle.getCreatorId() == null) {
		    if(circleCreatorId != null)
		        userCircle.setCircleCreatorId(circleCreatorId);
		    else
		        return new ErrorResolution(ErrorDef.UnknownCircleError);
		}
		else
		    circleCreatorId = userCircle.getCreatorId();
		List<Circle> circles = ImmutableList.of(userCircle);
		List<Long> subcribedCircleIds = circleSubscribeDao.listSubcribeCircle(curUserId, circles);
		circles = circleAttributeDao.getCircleAttribute(circles, curUserId, subcribedCircleIds);   
		results.put("results", circles);
        results.put("resultSize", circles.size());
        return json(results, UserCicleView.class);
	}
}
