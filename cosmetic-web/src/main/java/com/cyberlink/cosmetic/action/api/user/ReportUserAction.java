package com.cyberlink.cosmetic.action.api.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.dao.UserReportedDao;
import com.cyberlink.cosmetic.modules.user.model.SessionStatus;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserReported;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.cyberlink.cosmetic.modules.user.model.UserReported.UserReportedReason;
import com.cyberlink.cosmetic.modules.user.model.UserReported.UserReportedStatus;
import com.cyberlink.cosmetic.modules.user.result.UserApiResult;
import com.cyberlink.cosmetic.modules.user.service.UserService;

@UrlBinding("/api/v4.2/user/report-user.action")
public class ReportUserAction extends AbstractAction {
	@SpringBean("user.UserReportedDao")
	private UserReportedDao userReportedDao;

	@SpringBean("user.UserDao")
	private UserDao userDao;
	
	@SpringBean("user.userService")
	private UserService userService;

	private Long targetId;
	private String reason;

	@Validate(required = true, on = "route")
	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

	@Validate(required = true, on = "route")
	public void setReason(String reason) {
		this.reason = reason;
	}

	@DefaultHandler
	public Resolution route() {
		RedirectResolution redirect = redirectWriteAPI();
		if (redirect != null)
			return redirect;

		if (!authenticate()) {
			return new ErrorResolution(authError);
		} else if (getSession().getStatus() == SessionStatus.Invalied)
			return new ErrorResolution(ErrorDef.AccountEmailDeleted);

		UserReportedReason reasonE = null;
		switch (reason) {
		case "SPAMMING":
			reasonE = UserReportedReason.SPAMMING;
			break;
		case "GRAPHIC":
			reasonE = UserReportedReason.GRAPHIC;
			break;
		case "ABUSIVE":
			reasonE = UserReportedReason.ABUSIVE;
			break;
		case "PRETENDING":
			reasonE = UserReportedReason.PRETENDING;
			break;
		default:
			break;
		}
		if (reasonE == null)
			return new ErrorResolution(ErrorDef.InvalidUserReportReason);
		
		UserApiResult<Boolean> result = userService.reportUser(targetId, getCurrentUserId(), reasonE);
		if(result.success())
			return success();
		else
			return new ErrorResolution(result.getErrorDef());
	}
}