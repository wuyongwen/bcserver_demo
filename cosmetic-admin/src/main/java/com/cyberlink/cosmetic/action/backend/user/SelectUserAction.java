package com.cyberlink.cosmetic.action.backend.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.cyberlink.cosmetic.utils.Locale;
@UrlBinding("/user/selectUser.action")
public class SelectUserAction extends AbstractAction{
    @SpringBean("user.UserDao")
    private UserDao userDao;

    @SpringBean("user.SubscribeDao")
    private SubscribeDao subscribeDao;
    
    private PageResult<User> pageResult;
    private Long userId;
    private Set<Long> checkboxChoices;
    
	@DefaultHandler
	public Resolution list() {
    	userId = getCurrentUserId();
    	if(userId == null) {
        	return new StreamingResolution("text/html", "Need to login");            	
        }
    	User currentUser = userDao.findById(userId);
    	String locale = "en_US";
    	if (currentUser.getRegion() != null) {
    		locale = currentUser.getRegion();
    		//locale = Locale.getAvailableLocale(locale);
    	}
    	
    	PageLimit pageLimit = getPageLimit("row");
    	Long offset = Long.valueOf(pageLimit.getStartIndex());
    	Long limit = Long.valueOf(pageLimit.getPageSize());
    	List<UserType> userTypeList = new ArrayList<UserType>();
    	userTypeList.add(UserType.Expert);
    	List<String> localeList = Locale.mapLocaleByLanguage(locale);

    	pageResult = userDao.findByUserType(userTypeList, localeList, offset, limit);
    	
    	offset = offset - pageResult.getTotalSize();
    	if (offset < 0)
    		offset = Long.valueOf(0);
    	limit = limit - pageResult.getResults().size();
    	if (limit < 0)
    		limit = Long.valueOf(0);
    	PageResult<User> followingList = subscribeDao.findFollowingWithoutUserType(userId, UserType.Expert, offset, limit);
    	//List<User> followingList = userDao.findByIds(followingIdList.getResults().toArray(new Long[followingIdList.getResults().size()]));
    	pageResult.setTotalSize(pageResult.getTotalSize() + followingList.getTotalSize());
    	pageResult.getResults().addAll(followingList.getResults());
    	return forward();
	}
    public Resolution tag() {
    	final Map<String, Object> results = new HashMap<String, Object>();
    	List<User> list = userDao.findByIds(checkboxChoices.toArray(new Long[checkboxChoices.size()]));
    	results.put("results", list);
    	
    	return json(results);
    }

	public PageResult<User> getPageResult() {
		return pageResult;
	}

	public void setPageResult(PageResult<User> pageResult) {
		this.pageResult = pageResult;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Set<Long> getCheckboxChoices() {
		return checkboxChoices;
	}
	public void setCheckboxChoices(Set<Long> checkboxChoices) {
		this.checkboxChoices = checkboxChoices;
	}

}
