package com.cyberlink.cosmetic.action.api.search;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.search.model.TypeKeyword;
import com.cyberlink.cosmetic.modules.search.service.UserKeywordService;

@UrlBinding("/api/search/remove-recent-keyword-by-userid.action")
public class RemoveRecentKeywordByUserIdAction extends AbstractAction{
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
		try{
			userKeywordService.deleteUserKeyword(curUserId, type);
	    	return success();
		}catch(Exception e){
			return error();
		}
    }
}
