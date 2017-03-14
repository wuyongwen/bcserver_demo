package com.cyberlink.cosmetic.action.api.search;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.search.service.PostKeywordService;

@UrlBinding("/api/search/save-post-keyword.action")
public class SavePostKeywordAction extends AbstractAction{
	@SpringBean("search.PostKeywordService")
	private PostKeywordService postKeywordService;
	
	private String keyword;
	private String lang;
	
	public String getLang() {
		return lang;
	}

	@Validate(required = true, on = "route")
	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getKeyword() {
		return keyword;
	}

	@Validate(required = true, on = "route")
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	@DefaultHandler
    public Resolution route() {
		postKeywordService.saveKeyword(keyword, lang);
    	return success();
    }
}
