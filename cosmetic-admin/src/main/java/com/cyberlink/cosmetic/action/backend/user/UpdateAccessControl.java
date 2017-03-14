package com.cyberlink.cosmetic.action.backend.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.action.backend.UserAccessControl;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.user.dao.AccountDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.AccountSourceType;
import com.cyberlink.cosmetic.modules.user.model.User;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/user/update-access.action")
public class UpdateAccessControl extends AbstractAction {
    @SpringBean("user.AccountDao")
    protected AccountDao accountDao;

	private UserAccessControl targetAccessControl = null;
	private String email;
	private Set<String> access;
	
	@DefaultHandler
    public Resolution route() {
		if (email == null) {
			return new ErrorResolution(ErrorDef.InvalidAccount);
		}
		Account account = accountDao.findBySourceAndReference(AccountSourceType.Email, email);
    	if (account == null) {
    		return new ErrorResolution(ErrorDef.InvalidAccount);
    	}
        User user = account.getUser();
        targetAccessControl = new UserAccessControl(user);
        if (access != null) {
        	targetAccessControl.setUserManagerAccess(access.contains("User"));
        	targetAccessControl.setPostManagerAccess(access.contains("Post"));
        	targetAccessControl.setCircleManagerAccess(access.contains("Circle"));
        	targetAccessControl.setProductManagerAccess(access.contains("Product"));
        	targetAccessControl.setReportManagerAccess(access.contains("Report"));
        	targetAccessControl.setReportAuditorAccess(access.contains("Auditor"));
        	targetAccessControl.setEventManagerAccess(access.contains("Event"));
        	targetAccessControl.saveAccessControl();
        }
        return new StreamingResolution("text/html", "");
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set<String> getAccess() {
		if (access == null)
			this.access = new HashSet<String>();
		return access;
	}

	public void setAccess(Set<String> access) {
		this.access = access;
	}
}
