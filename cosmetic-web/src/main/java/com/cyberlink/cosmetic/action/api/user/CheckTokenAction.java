package com.cyberlink.cosmetic.action.api.user;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorResolution;

@UrlBinding("/api/user/check-token.action")
public class CheckTokenAction extends AbstractAction {
	
	@DefaultHandler
    public Resolution route() {
    	if (!authenticate())
    		return new ErrorResolution(authError); 		
    	return success();
	}
}
