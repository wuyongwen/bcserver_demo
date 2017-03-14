package com.cyberlink.cosmetic.action.backend.v2.circle;

import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.circle.dao.CircleAttributeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleSubscribeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.service.CircleService;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;

@UrlBinding("/v2/circle/list-circle-by-user.action")
public class ListCircleByUserAction extends AbstractCircleAction{
	
	@SpringBean("post.PostService")
    private PostService postService;
	
    @SpringBean("circle.circleTypeDao")
	private CircleTypeDao circleTypeDao;

    @SpringBean("user.UserDao")
    private UserDao userDao;

    @SpringBean("common.localeDao")
    private LocaleDao localeDao;
    
    @SpringBean("circle.circleService")
    private CircleService circleService;
    
    @SpringBean("circle.circleSubscribeDao")
    private CircleSubscribeDao circleSubscribeDao;
    
    @SpringBean("circle.circleAttributeDao")
    private CircleAttributeDao circleAttributeDao;
    
    @SpringBean("user.SubscribeDao")
    private SubscribeDao subscribeDao;
    
    private Long userId;
    private Long curUserId;
    private Long circleId;
    PageResult<Circle> circles = new PageResult<Circle>();
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getUserId() {
        return this.userId;
    }
    
    public void setCurUserId(Long curUserId) {
        this.curUserId = curUserId;
    }
    
    public Long getCurUserId() {
        return this.curUserId;
    }

    public PageResult<Circle> getCircles() {
        return circles;
    }
    
	public void setCircleId(Long circleId) {
        this.circleId = circleId;
    }
    
	@DefaultHandler
	public Resolution route() {
    	if (getCurrentUser() == null) {
    		return new StreamingResolution("text/html", "Need Login");
        }
	    
	    if(userId == null)
	        userId = getCurrentUserId();
	    if(curUserId == null)
	        curUserId = userId;
	    
	    Boolean withSecret = (curUserId == null || !userId.equals(curUserId)) ? false : true;
        User circleCreator = userDao.findById(userId);
        String locale = localeDao.getLocaleByType(circleCreator.getRegion(), LocaleType.POST_LOCALE).iterator().next();

        PageLimit pageLimit = getPageLimit("row");
        BlockLimit blockLimit = new BlockLimit((pageLimit.getPageIndex() - 1 ) * pageLimit.getPageSize(), pageLimit.getPageSize());
        circles = circleService.listUserCircle(userId, withSecret, locale, true, blockLimit);
        List<Long> subcribedCircleIds = circleSubscribeDao.listSubcribeCircle(curUserId, circles.getResults());
        circles.setResults(circleAttributeDao.getCircleAttribute(circles.getResults(), curUserId, subcribedCircleIds));
        
        
		return forward();
	}
	
	public Resolution deleteCircle() {
    	if (getCurrentUser() == null) {
    		return new StreamingResolution("text/html", "Need Login");
        }
	    
	    if(!circleDao.exists(circleId))
	        return new ErrorResolution(ErrorDef.InvalidCircleId);
        
	    Circle relatedCircle = circleDao.findById(circleId);
	    Circle userCircle = getUserAccessibleCircle(relatedCircle, getCurrentUserId(), true);
        if(userCircle == null)
            return new ErrorResolution(ErrorDef.InvalidCircleNotAuth);
		
        userCircle.setIsDeleted(true);
	    circleDao.update(userCircle);
	    subscribeDao.bacthDeleteSubscribe(getCurrentUserId(), SubscribeType.Circle);
	    circleSubscribeDao.bacthDeleteSubscribe(userCircle.getId());
	    postService.deletePostByCircle(getCurrentUserId(), userCircle.getIsSecret(), userCircle.getId());
	    return json("Done");
	}
}
