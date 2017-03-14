package com.cyberlink.cosmetic.action.backend.search;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.post.AbstractPostAction;
import com.cyberlink.cosmetic.modules.search.model.TypeKeyword;
import com.cyberlink.cosmetic.modules.search.service.UserKeywordService;

@UrlBinding("/search/listRecentKeywordByUserId.action")
public class ListRecentKeywordByUserIdAction extends AbstractPostAction {
	
	@SpringBean("search.UserKeywordService")
	private UserKeywordService userKeywordService;

	private Long curUserId;
	private TypeKeyword type;
	private PageResult<String> result = new PageResult<String>();
	private List<TypeKeyword> typeKeywordList;

	@DefaultHandler
	public Resolution route() {
		if (!getCurrentUserAdmin()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
		typeKeywordList = Arrays.asList(TypeKeyword.values());
		try {
			if(curUserId != null){
				List<String> keywords = userKeywordService.getKeywords(curUserId, type);
				result.setResults(keywords);
				result.setTotalSize(keywords.size());
			}
			return forward();
		} catch (Exception e) {
			logger.error("ListRecentKeywordByUserIdAction route fail. message:" + e.getMessage());
			return new StreamingResolution("text/html", e.getMessage());
		}
	}

	public List<TypeKeyword> getTypeKeywordList() {
		return typeKeywordList;
	}
	
	public PageResult<String> getResult() {
		return result;
	}

	public Long getCurUserId() {
		return curUserId;
	}

	public void setCurUserId(Long curUserId) {
		this.curUserId = curUserId;
	}

	public TypeKeyword getType() {
		return type;
	}

	public void setType(TypeKeyword type) {
		this.type = type;
	}
	
}
