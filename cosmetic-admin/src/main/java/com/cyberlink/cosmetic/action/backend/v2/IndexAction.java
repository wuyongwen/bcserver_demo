package com.cyberlink.cosmetic.action.backend.v2;

import com.cyberlink.cosmetic.action.backend.AbstractAction;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import com.cyberlink.cosmetic.action.backend.v2.user.LoginAction;
import com.cyberlink.cosmetic.action.backend.v2.user.EditCurrentUser;

@UrlBinding("/v2/index.action")
public class IndexAction extends AbstractAction {
	
	@DefaultHandler
    public Resolution route() {
        if (getCurrentUserId() == null) {
        	return new RedirectResolution(LoginAction.class, "route");
        }
        return new RedirectResolution(EditCurrentUser.class, "route");
    }

}
