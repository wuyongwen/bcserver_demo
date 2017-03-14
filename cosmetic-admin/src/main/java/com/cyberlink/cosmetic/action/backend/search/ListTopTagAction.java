package com.cyberlink.cosmetic.action.backend.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.search.service.SearchPostService;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/search/listTopTag.action")
public class ListTopTagAction extends AbstractAction{

	@SpringBean("search.SearchPostService")
    private SearchPostService searchPostService;
    
    @SpringBean("common.localeDao")
    private LocaleDao localeDao;
	
    private String locale = "en_US";
    private int topNumber = 20;
    private Set<String> userLocales;
    private List<OrderTag> orderTagList = null;
	
    @DefaultHandler
    public Resolution route() {
		if (!getCurrentUserAdmin()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
		userLocales = localeDao.getAvailableLocaleByType(LocaleType.USER_LOCALE);
    	try{
	    	List<String> tagList = searchPostService.getTopTags(locale, topNumber);
	    	orderTagList = new ArrayList<OrderTag>();
	    	Integer tagIndex = 1;
	    	for(String tag : tagList){
	    		orderTagList.add(new OrderTag(tagIndex, tag));
	    		tagIndex++;
	    	}
	    	return forward();   	
    	}catch(Exception e){
			logger.error("ListTopTagAction route fail. message:" + e.getMessage());
			return new StreamingResolution("text/html", e.getMessage());
    	}
    }
    
	public int getTopNumber() {
		return topNumber;
	}

	public void setTopNumber(int topNumber) {
		this.topNumber = topNumber;
	}

	public List<OrderTag> getOrderTagList() {
		return orderTagList;
	}

	public Set<String> getUserLocales() {
		return userLocales;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	public class OrderTag{
		
    	private Integer index;
    	private String tag;
    	
    	OrderTag(Integer index,String tag){
    		this.index = index;
    		this.tag = tag;
    	}
    	
		public Integer getIndex() {
			return index;
		}
		public void setIndex(Integer index) {
			this.index = index;
		}
		public String getTag() {
			return tag;
		}
		public void setTag(String tag) {
			this.tag = tag;
		}
    }

}
