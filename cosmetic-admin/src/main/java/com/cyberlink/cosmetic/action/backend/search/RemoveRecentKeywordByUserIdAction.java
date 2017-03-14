package com.cyberlink.cosmetic.action.backend.search;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import java.util.Arrays;
import java.util.List;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.search.model.TypeKeyword;
import com.cyberlink.cosmetic.modules.search.service.UserKeywordService;

@UrlBinding("/search/removeRecentKeywordByUserId.action")
public class RemoveRecentKeywordByUserIdAction extends AbstractAction{
	
	@SpringBean("search.UserKeywordService")
	private UserKeywordService userKeywordService;
	
	private Long curUserId;
	private String message;
	private TypeKeyword type;
	private List<TypeKeyword> typeKeywordList;
	

	@DefaultHandler
    public Resolution route() {
		if (!getCurrentUserAdmin()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
		typeKeywordList = Arrays.asList(TypeKeyword.values());
		try{
			if(curUserId != null){
				userKeywordService.deleteUserKeyword(curUserId, type);
				message = String.format("Delete recent keyword success!(user id: %d | keyword type : %s)" , curUserId , type.toString());
			}
			return forward();
		}catch(Exception e){
			logger.error("RemoveRecentKeywordByUserIdAction route fail. message:" + e.getMessage());
			return new StreamingResolution("text/html", e.getMessage());
		}
    }
	
    public String getMessage() {
		return message;
	}
    
	public List<TypeKeyword> getTypeKeywordList() {
		return typeKeywordList;
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
