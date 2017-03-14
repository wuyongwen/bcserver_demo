package com.cyberlink.cosmetic.action.api.user;

import org.apache.commons.lang.StringUtils;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.user.model.Session;
import com.cyberlink.cosmetic.modules.user.service.UserService;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/user/sign-out.action")
public class SignOutAction extends AbstractAction {
	@SpringBean("user.userService")
    protected UserService userService;
	
    private String uuid;

    private String app;

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setApp(String app) {
        this.app = app;
    }

    @DefaultHandler
    public Resolution route() {
        RedirectResolution redirect = redirectWriteAPI();
        if (redirect != null)
            return redirect;

        if (!authenticateByRedis())
            return new ErrorResolution(authError);
        
        userService.signOutToken(getToken());
        return success();
    }

    private String getPartOfToken(Session session) {
        return StringUtils.substring(session.getToken(), 0, 15);
    }
}
