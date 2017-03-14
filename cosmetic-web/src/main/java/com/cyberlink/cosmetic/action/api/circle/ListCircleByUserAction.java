package com.cyberlink.cosmetic.action.api.circle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.circle.AbstractCircleAction;
import com.cyberlink.cosmetic.modules.circle.dao.CircleAttributeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleSubscribeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.Circle.ListCicleView;
import com.cyberlink.cosmetic.modules.circle.model.Circle.UserCicleView;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Subscribe;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;
import com.cyberlink.cosmetic.modules.user.model.User;

@UrlBinding("/api/circle/list-circle-by-user.action")
public class ListCircleByUserAction extends AbstractCircleAction {
	
	@SpringBean("circle.circleAttributeDao")
    private CircleAttributeDao circleAttributeDao;
	
	@SpringBean("circle.circleSubscribeDao")
    private CircleSubscribeDao circleSubscribeDao;
	
	@SpringBean("user.SubscribeDao")
    private SubscribeDao subscribeDao;
	
	@SpringBean("common.localeDao")
    private LocaleDao localeDao;
	
    @SpringBean("user.UserDao")    
    protected UserDao userDao;
	
	private int offset = 0;
    private int limit = Integer.MAX_VALUE;
    private Long userId;
    private Long curUserId;
    private String responseType = "Basic";//Basic, Detail
    private String locale = "en_US";
    
	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Validate(required = true, on = "route")
	public void setUserId(Long userId) {
		this.userId = userId;
	}

    public void setCurUserId(Long curUserId) {
        this.curUserId = curUserId;
    }
    
    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }
    
    public void setLocale(String locale) {
        this.locale = locale;
    }
    
	@DefaultHandler
	public Resolution route() {
	    List<Circle> bcDefaiultCircles = circleService.getBcDefaultCircle(locale);
	    Map<String, String> defaultTypeNameMap = new HashMap<String, String>();
	    for(Circle dc : bcDefaiultCircles) {
	        defaultTypeNameMap.put(dc.getDefaultType(), dc.getCircleName());
	    }
	    
	    Boolean withSecret = (curUserId == null || !userId.equals(curUserId)) ? false : true;
	    User circleCreator = userDao.findById(userId);
	    PageResult<Circle> circles = null;
	    Class<?> serializationView = null;
	    switch(responseType)
	    {
	    case "Detail":
	    {
	        circles = circleService.listUserCreatedCircle(userId, withSecret, new BlockLimit(offset, limit));
	        List<Long> subcribedCircleIds = null; 
	        if(curUserId != null && !userId.equals(curUserId)) {
	            List<Subscribe> subscribeList = subscribeDao.findBySubscriberAndSubscribees(curUserId, SubscribeType.User, userId);
	            if(subscribeList != null && subscribeList.size() > 0) {
	                subcribedCircleIds = new ArrayList<Long>();
	                for(Circle c : circles.getResults()) {
	                    subcribedCircleIds.add(c.getId());
	                }
	            }
	            else {
	                subcribedCircleIds = circleSubscribeDao.listSubcribeCircle(curUserId, circles.getResults());
	            }
	        }

	        circles.setResults(circleAttributeDao.getCircleAttribute(circles.getResults(), curUserId, subcribedCircleIds));
	        serializationView = UserCicleView.class;
	        break;
	    }
	    case "Basic":
        default:
            circles = listUserCircle(userId, withSecret, locale, true, new BlockLimit(offset, limit));
            serializationView = ListCicleView.class;
            break;
	    }
	    
	    for(Circle c : circles.getResults()) {
	        String cDefaultType = c.getDefaultType();
	        if(cDefaultType == null)
	            continue;
	        if(c.getIsCustomized())
	            continue;
	        String translatedCircleName = defaultTypeNameMap.get(cDefaultType);
	        c.setTranslatedCircleName(translatedCircleName);
	    }
	    return json(circles, serializationView);
	    
		
		
	}
}
