package com.cyberlink.cosmetic.action.api.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.user.dao.AccountDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.SessionStatus;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/user/list-binding.action")
public class ListBindingAction extends AbstractAction{
    @SpringBean("user.AccountDao")
    private AccountDao accountDao;

    @DefaultHandler
    public Resolution route() {
    	if (!authenticate())
    		return new ErrorResolution(authError); 
        else if (getSession().getStatus() == SessionStatus.Invalied)
        	return new ErrorResolution(ErrorDef.AccountEmailDeleted);
        
		final Map<String, Object> results = new HashMap<String, Object>();        
        List<Account> account = accountDao.findByUserId(getSession().getUserId());
        results.put("results", account);
        return json(results);
    }
}
