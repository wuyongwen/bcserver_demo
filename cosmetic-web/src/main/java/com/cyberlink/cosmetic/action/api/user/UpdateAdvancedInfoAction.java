package com.cyberlink.cosmetic.action.api.user;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;

@UrlBinding("/api/user/update-advanced-info.action")
public class UpdateAdvancedInfoAction extends AbstractAction{
	@SpringBean("user.UserDao")
    private UserDao userDao;
	
	private Long level;
	private Boolean certify;
	
	@DefaultHandler
    public Resolution route() {
		RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
    	if (!authenticateByRedis()) {
    		return new ErrorResolution(authError);
    	}
    	
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("token", getToken());
    	if (level != null)
    		params.put("level", level.toString());
    	if (certify != null)
    		params.put("certify", certify.toString());
    	if (!authenticateBySignature(params))
    		return new ErrorResolution(ErrorDef.InvalidSignature);
    	
    	User user = getSession().getUser();
    	if (level != null)
    		user.setLevel(level);
    	
    	if (certify != null)
    		user.setCertify(certify);
    	
    	userDao.update(user);
    	final Map<String, Object> results = new HashMap<String, Object>();
        results.put("userId", user.getId());
        
        return json(results);
	}

	public void setLevel(Long level) {
		this.level = level;
	}

	public void setCertify(Boolean certify) {
		this.certify = certify;
	}
}