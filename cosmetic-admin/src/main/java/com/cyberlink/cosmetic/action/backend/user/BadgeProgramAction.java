package com.cyberlink.cosmetic.action.backend.user;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.core.repository.EsRepository.EsResult;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.notify.service.NotifyService;
import com.cyberlink.cosmetic.modules.post.repository.PostHeatRepository;
import com.cyberlink.cosmetic.modules.user.dao.UserBadgeDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserBadge;
import com.cyberlink.cosmetic.modules.user.model.UserBadge.BadgeType;
import com.cyberlink.cosmetic.modules.user.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/user/badge-program.action")
public class BadgeProgramAction extends AbstractAction {
    
    @SpringBean("user.UserDao")
    private UserDao userDao;
    
    @SpringBean("user.userService")
    private UserService userService;
    
    @SpringBean("user.userBadgeDao")
    private UserBadgeDao userBadgeDao;
    
    @SpringBean("common.localeDao")
    private LocaleDao localeDao;
    
    @SpringBean("post.postHeatRepository")
    private PostHeatRepository postHeatRepository;
    
    @SpringBean("notify.NotifyService")
    private NotifyService notifyService;
    
    @SpringBean("web.objectMapper")
    private ObjectMapper objectMapper;
    
    private Integer limit = 50;

    private String selLocale = "en_US";
    private String pageAction = "select";

	private final String FORWARD_JSP = "/user/badge-program.jsp";
    
    private String error;
    
    private List<String> availableRegion = new ArrayList<String>(0);
    
    private List<Map<String, Object>> usersList;
    
    private String uidsJson;

    @DefaultHandler
    public Resolution route() {
        if (!getCurrentUserAdmin() && !getAccessControl().getReportManagerAccess()) {
            error = "Need to login";
            return forward(FORWARD_JSP);
        }
        
        availableRegion.addAll(localeDao.getAvailableLocaleByType(LocaleType.USER_LOCALE));

        String profileUrl = Constants.getBCWebsiteDomain();
        if(profileUrl == null || profileUrl.length() <= 0)
            profileUrl = "./userEdit.action?edit=&userId=%d";
        else
            profileUrl = "http://" + profileUrl + "/profile/%d";
        
        List<String> selLocales = new ArrayList<String>();
        selLocales.add(selLocale);
        
        if(pageAction.equals("select"))
        	return candidateSOW(profileUrl, selLocales);
        else
        	return listSOW(profileUrl, selLocales);  
    }
    
    private Resolution candidateSOW(String profileUrl, List<String> selLocales){
    	Calendar cal = Calendar.getInstance();
        Date end = cal.getTime();
        cal.add(Calendar.DATE, -7);
        Date begin = cal.getTime();
        EsResult<Map<Long, Map<String, Integer>>> result = postHeatRepository.findTopUser(selLocale, limit, begin, end);
        if(result.error != null) {
            error = result.error;
            return forward(FORWARD_JSP);
        }
        
        PageResult<User> exSOWUsers = userService.getUsersByBadgeType(selLocales, BadgeType.StarOfWeek, new BlockLimit(0, limit));
        List<Long> exSOWUserIds = new ArrayList<Long>();
        if(exSOWUsers != null) {
            for(User exSOWUser : exSOWUsers.getResults()) {
                exSOWUserIds.add(exSOWUser.getId());
            }
        }
        
        Map<Long, User> userMap = userDao.findUserMap(result.result.keySet());
        usersList = new ArrayList<Map<String, Object>>();
        for(Long uid : result.result.keySet()) {
            Map<String, Integer> ri = result.result.get(uid);
            Map<String, Object> info = new HashMap<String, Object>();
            info.put("id", uid);
            info.put("score", ri.get("score"));
            info.put("postCount", ri.get("postCount"));
            info.put("selected", false);
            if(userMap.containsKey(uid)) {
                User u = userMap.get(uid);
                info.put("name", u.getDisplayName());
                info.put("avatar", u.getAvatarUrl());
            }
            if(exSOWUserIds.contains(uid))
                info.put("selected", true);
            else
                info.put("selected", false);
            info.put("profile", String.format(profileUrl, uid));
            usersList.add(info);
        }
        return forward(FORWARD_JSP);
    }
    
	public Resolution listSOW(String profileUrl, List<String> selLocales) {
		PageResult<UserBadge> userBadges = userBadgeDao.listUsersByBadgeType(selLocales, BadgeType.StarOfWeek, new BlockLimit(0, limit));
		usersList = new ArrayList<Map<String, Object>>();
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormater.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		for(UserBadge userBadge : userBadges.getResults()){
			Map<String, Object> info = new HashMap<String, Object>();
			info.put("avatar", userBadge.getUser().getAvatarUrl());
            info.put("id", userBadge.getUserId());
            info.put("name", userBadge.getUser().getDisplayName());
            info.put("score", userBadge.getScore());
            info.put("createTime", dateFormater.format(userBadge.getCreatedTime())+" (TW)");
            info.put("profile", String.format(profileUrl, userBadge.getUserId()));
            usersList.add(info);
		}
		return forward(FORWARD_JSP);
	}
    
    public Resolution addSOW() {
        if (!getCurrentUserAdmin() && !getAccessControl().getReportManagerAccess()) {
            return json("Error");
        }
        
        try {
            Map<Long, Long> userIdScoreMap = new LinkedHashMap<Long, Long>();
            Map<String, String>  userIdStrScoreMap = objectMapper.readValue(uidsJson, new TypeReference<Map<String, String>>(){});
            for(String uid : userIdStrScoreMap.keySet())
                userIdScoreMap.put(Long.valueOf(uid), Long.valueOf(userIdStrScoreMap.get(uid)));
            List<Long> newAddedSOW = userService.updateStarOfWeek(selLocale, userIdScoreMap);
            //notifyService.sendSOWNotify(newAddedSOW, selLocale);
            return json(newAddedSOW);
        } catch (IOException e) {
            logger.error("addSOW", e);
            return json("Error : " + e.getMessage());
        }
    }
    
    public String getError() {
        return error;
    }
    
    public List<String> getAvailableRegion() {
        return availableRegion;
    }
    
    public String getSelLocale() {
        return selLocale;
    }

    public void setSelLocale(String selLocale) {
        this.selLocale = selLocale;
    }
    
	public String getPageAction() {
		return pageAction;
	}

	public void setPageAction(String pageAction) {
		this.pageAction = pageAction;
	}

    public List<Map<String, Object>> getUsersList() {
        return usersList;
    }
    
    public void setUidsJson(String uidsJson) {
        this.uidsJson = uidsJson;
    }
    
    // Testing function
    public Resolution sendNotify() {
    	if (!getCurrentUserAdmin() && !getAccessControl().getReportManagerAccess()) {
            return json("Error");
        }
    	
    	if (receiverId != null && !receiverId.isEmpty())
    		notifyService.sendSOWNotify(receiverId, selLocale);
    	return json(receiverId);
    }
    
    private List<Long> receiverId;

	public void setReceiverId(List<Long> receiverId) {
		this.receiverId = receiverId;
	}    
}
