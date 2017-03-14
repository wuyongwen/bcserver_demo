package com.cyberlink.cosmetic.action.api.circle;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleUserDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.Circle.ListCicleView;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;

//list circle information by circle type ID or user ID
@UrlBinding("/api/circle/list-circle.action")
public class ListCircleAction extends AbstractCircleAction {
	@SpringBean("circle.circleDao")
	private CircleDao circleDao;

	@SpringBean("circle.circleUserDao")
	private CircleUserDao circleUserDao;
	
	@SpringBean("circle.circleTypeDao")    
    private CircleTypeDao circleTypeDao;
	
	@SpringBean("user.UserDao")    
    private UserDao userDao;
	
	private int offset = 0;
    private int limit = 10;
	private Long circleTypeId;
	private Long userId;
    private String locale ;
	
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public void setCircleTypeId(Long circleTypeId) {
		this.circleTypeId = circleTypeId;
	}	

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	protected PageResult<Circle> getCircle() {
	    PageResult<Circle> results = new PageResult<Circle>();
	
        if (userId != null) {
        	// by user ID
            BlockLimit blockLimit = new BlockLimit(offset, limit);
        	results = listUserCircle(userId, true, locale, false, blockLimit);
        } else if (circleTypeId != null) {
            List<Long> circleTypeIds = new ArrayList<Long>();
            circleTypeIds.add(circleTypeId);
            results = circleDao.findByTypeIds(circleTypeIds, (long)offset, (long)limit);
        } else {
            Long userTypeOffset = (long)0;
            Long userTypeLimit = (long)100;            
            List<UserType> userTypes = new ArrayList<UserType>();
            userTypes.add(UserType.CL);
            List<String> locales = new ArrayList<String>();
            locales.add(locale);
            List<Long> userIds = new ArrayList<Long>();
            do {
                PageResult<User> userResults = userDao.findByUserType(userTypes, locales, userTypeOffset, userTypeLimit);
                for(User usr : userResults.getResults()) {
                    userIds.add(usr.getId());
                }
                
                userTypeOffset += userTypeLimit;
                if(userTypeOffset > userResults.getTotalSize())
                    break;
            }while (true);
            
            if(userIds.size() > 0) {
                BlockLimit blockLimit = new BlockLimit(offset, limit);
                results = circleDao.findByCLUserIds(userIds, true, false, blockLimit);
            }
        }
	    return results;
	}
	
	@DefaultHandler
	public Resolution route() {
	    PageResult<Circle> results = getCircle();
	    List<Circle> toRemove = new ArrayList<Circle>();
        for(Circle c : results.getResults()) {
            if(c.getDefaultType().equals("HOW-TO"))
                toRemove.add(c);
        }
        if(toRemove.size() > 0) {
            results.getResults().removeAll(toRemove);
            Integer totalSize = results.getTotalSize() - toRemove.size();
            results.setTotalSize(totalSize);
        }

        return json(results, ListCicleView.class);
	}
}
