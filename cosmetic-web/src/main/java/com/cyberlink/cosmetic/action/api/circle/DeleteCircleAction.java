package com.cyberlink.cosmetic.action.api.circle;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleSubscribeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;

@UrlBinding("/api/circle/delete-circle.action")
public class DeleteCircleAction extends AbstractCircleAction {

    @SpringBean("post.PostService")
    private PostService postService;
    
	@SpringBean("circle.circleDao")
	private CircleDao circleDao;
	
	@SpringBean("user.SubscribeDao")
    private SubscribeDao subscribeDao;
    
    @SpringBean("circle.circleSubscribeDao")
    private CircleSubscribeDao circleSubscribeDao;
    
	private Long circleId;
	 
	@Validate(required = true, on = "route")
    public void setToken(String token) {
        super.setToken(token);
    }
	
	public void setCircleId(Long circleId) {
        this.circleId = circleId;
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
        if(userCircle == null)
            return new ErrorResolution(ErrorDef.InvalidCircleNotAuth);
		
        userCircle.setIsDeleted(true);
	    circleDao.update(userCircle);
	    subscribeDao.bacthDeleteSubscribe(getCurrentUserId(), SubscribeType.Circle);
	    circleSubscribeDao.bacthDeleteSubscribe(userCircle.getId());
	    postService.deletePostByCircle(userCircle.getCreatorId(), userCircle.getIsSecret(), userCircle.getId());
	    return success();
	}

}
