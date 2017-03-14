package com.cyberlink.cosmetic.action.backend.user;

import javax.servlet.http.HttpSession;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.action.backend.IndexAction;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;
import com.cyberlink.cosmetic.modules.user.model.Session;
import com.cyberlink.cosmetic.modules.user.model.SessionStatus;
import com.cyberlink.cosmetic.modules.user.service.UserService;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/user/logout.action")
public class LogoutAction extends AbstractAction{
	@SpringBean("user.userService")
    private UserService userService;

	@DefaultHandler
    public Resolution route() {
		Long userId = getCurrentUserId();
		if (userId == null) {
			getContext().getRequest().getSession().setAttribute("token", null); 
			return new RedirectResolution(IndexAction.class, "route");
		}
        HttpSession session = getContext().getRequest().getSession();
        if(session != null) {
            String token = (String) getContext().getRequest().getSession().getAttribute("token");
            userService.signOutToken(token);
        }
		getContext().getRequest().getSession().setAttribute("token", null); 
		return new RedirectResolution(IndexAction.class, "route");
    }
}
