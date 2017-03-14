package com.cyberlink.cosmetic.action.api.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.User.LookSource;

@UrlBinding("/api/v4.7/user/list-user-byLookSource.action")
public class ListUserByLookAction extends ListUserByTypeAction {
	private List<String> sourceType;

	@DefaultHandler
	public Resolution route() {
		final Map<String, Object> results = new HashMap<String, Object>();
		PageResult<User> pageResult = null;

		pageResult = userDao.findByUserTypeAndLookSource(userType,
				getLookSource(), locale, offset, limit);
		setUserArribute(pageResult);

		results.put("results", pageResult.getResults());
		results.put("totalSize", pageResult.getTotalSize());
		return json(results);
	}

	private List<LookSource> getLookSource() {
		List<LookSource> lookSource = new ArrayList<LookSource>();
		for (String source : sourceType) {
			if (source.equalsIgnoreCase("YCL"))
				lookSource.add(LookSource.YCL);
		}
		if (lookSource.size() > 0)
			lookSource.add(LookSource.ALL);

		return lookSource;
	}

	public List<String> getSourceType() {
		return sourceType;
	}

	public void setSourceType(List<String> sourceType) {
		this.sourceType = sourceType;
	}
}