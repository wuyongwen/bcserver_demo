package com.cyberlink.cosmetic.action.api.search;

import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.post.AbstractPostAction;
import com.cyberlink.cosmetic.modules.search.model.TypeKeyword;
import com.cyberlink.cosmetic.modules.search.service.UserKeywordService;

@UrlBinding("/api/search/list-recent-keyword-by-userid.action")
public class ListRecentKeywordByUserIdAction extends AbstractPostAction {
	@SpringBean("search.UserKeywordService")
	private UserKeywordService userKeywordService;

	private Long curUserId;
	private TypeKeyword type;

	public Long getCurUserId() {
		return curUserId;
	}

	@Validate(required = true, on = "route")
	public void setCurUserId(Long curUserId) {
		this.curUserId = curUserId;
	}

	public TypeKeyword getType() {
		return type;
	}

	@Validate(required = true, on = "route")
	public void setType(TypeKeyword type) {
		this.type = type;
	}

	@DefaultHandler
	public Resolution route() {
		try {
			PageResult<String> result = new PageResult<String>();
			List<String> keywords = userKeywordService.getKeywords(curUserId, type);
			result.setResults(keywords);
			result.setTotalSize(keywords.size());
			return json(result);
		} catch (Exception e) {
			e.printStackTrace();
			return error();
		}
	}
}
