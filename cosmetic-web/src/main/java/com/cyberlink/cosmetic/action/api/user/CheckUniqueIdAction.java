package com.cyberlink.cosmetic.action.api.user;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;

@UrlBinding("/api/v4.8/user/check-uniqueId.action")
public class CheckUniqueIdAction extends AbstractAction {
	@SpringBean("user.UserDao")
	private UserDao userDao;

	private String uniqueId;

	@DefaultHandler
	public Resolution route() {
		final Map<String, Object> results = new HashMap<String, Object>();
		results.put("userId", userDao.verifyUniqueId(uniqueId));
		return json(results);
	}

	@Validate(required = true, on = "route")
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

}