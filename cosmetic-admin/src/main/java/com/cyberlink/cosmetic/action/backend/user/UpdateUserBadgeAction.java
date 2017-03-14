package com.cyberlink.cosmetic.action.backend.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserBadge.BadgeType;
import com.cyberlink.cosmetic.modules.user.service.UserService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

@UrlBinding("/user/update-user-badge.action")
public class UpdateUserBadgeAction extends AbstractAction{

	@SpringBean("user.UserDao")
	private UserDao userDao;

	@SpringBean("user.userService")
	private UserService userService;
	
	@SpringBean("web.objectMapper")
	private ObjectMapper objectMapper;

	private String locale = "en_US";
	private String jsonUserScore = ""; // {   "155445001" : 999999,   "154702001" : 9999,  "154910001": 5 }
	private Long userId;
	private BadgeType badgeType = BadgeType.StarOfWeek;
	
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	public String getJsonUserScore() {
		return jsonUserScore;
	}

	@Validate(required = true, on = "updateStarOfWeekWithScore")
	public void setJsonUserScore(String jsonUserScore) {
		this.jsonUserScore = jsonUserScore;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public BadgeType getBadgeType() {
		return badgeType;
	}
	
	public void setBadgeType(BadgeType badgeType) {
		this.badgeType = badgeType;
	}

	@DefaultHandler
	public Resolution route() {
		return json("Please select an action: updateStarOfWeek / updateUserBadge");
	}
	
	public Resolution updateStarOfWeekWithScore() {
		Map<Long, Long> userIdScore = new HashMap<Long, Long>();
		try {
			userIdScore = objectMapper.readValue(jsonUserScore, new TypeReference<Map<Long, Long>>(){});
		} catch (JsonParseException e) {
			logger.error(e.getMessage());
		} catch (JsonMappingException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return json(userService.updateStarOfWeek(locale, userIdScore));
	}

	public Resolution updateUserBadge() {
		if(userId == null)
			return new ErrorResolution(ErrorDef.InvalidUserId);
		
		if(!userDao.exists(userId))
			return new ErrorResolution(ErrorDef.UnknownUserError);
		
		userService.updateUserBadge(locale, userId, badgeType);
		return json("Complete");
	}
	
}
