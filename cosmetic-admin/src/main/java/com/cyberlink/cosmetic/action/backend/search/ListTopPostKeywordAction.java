package com.cyberlink.cosmetic.action.backend.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.search.service.PostKeywordService;
import com.cyberlink.cosmetic.modules.search.service.SearchPostService;

@UrlBinding("/search/listTopPostKeyword.action")
public class ListTopPostKeywordAction extends AbstractAction{
	
    @SpringBean("search.PostKeywordService")
    private PostKeywordService postKeywordService;
    
    @SpringBean("search.SearchPostService")
    private SearchPostService searchPostService;
    
    @SpringBean("common.localeDao")
    private LocaleDao localeDao;
    
    private Set<String> userLocales;
    private String locale = "en_US";
    private int topNumber = 20;
    private List<OrderKeyword> orderKeywordList = null;
    
    @DefaultHandler
    public Resolution route() {
		if (!getCurrentUserAdmin()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
		userLocales = localeDao.getAvailableLocaleByType(LocaleType.USER_LOCALE);
		orderKeywordList = new ArrayList<OrderKeyword>();
    	List<String> keywordList = postKeywordService.getTopKeywords(searchPostService.getLang(locale), topNumber);
    	Integer keywordIndex = 1;
    	for(String keyword : keywordList){
    		orderKeywordList.add(new OrderKeyword(keywordIndex, keyword));
    		keywordIndex++;
    	}
    	return forward();
    }
    
	public Set<String> getUserLocales() {
		return userLocales;
	}
    
	public List<OrderKeyword> getOrderKeywordList() {
		return orderKeywordList;
	}

	public String getLocale() {
		return locale;
	}
	
	public void setLocale(String locale) {
		this.locale = locale;
	}

	public int getTopNumber() {
		return topNumber;
	}

	public void setTopNumber(int topNumber) {
		this.topNumber = topNumber;
	}
	
	public class OrderKeyword{
		
    	private Integer index;
    	private String keyword;
    	
    	OrderKeyword(Integer index,String keyword){
    		this.index = index;
    		this.keyword = keyword;
    	}
    	
		public Integer getIndex() {
			return index;
		}
		public void setIndex(Integer index) {
			this.index = index;
		}
		public String getKeyword() {
			return keyword;
		}
		public void setKeyword(String keyword) {
			this.keyword = keyword;
		}
    }

}
