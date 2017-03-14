package com.cyberlink.cosmetic.action.backend.misc;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;

import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.model.Locale;
import com.cyberlink.cosmetic.modules.common.model.Locale.DiscoverTabType;
import com.cyberlink.cosmetic.modules.common.model.Locale.TrendingTabType;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/misc/DiscoverTabManage.action")
public class DiscoverTabManageAction extends AbstractAction{

    @SpringBean("common.localeDao")
    private LocaleDao localeDao;
    
    @SpringBean("web.objectMapper")
    private ObjectMapper objectMapper;
    
    private PageResult<Locale> pageResult = new PageResult<Locale>();
    private Long localeId;
    private Locale relLocale;
    private String searchLocale;

	private String visibleDiscoverTabsString;
	private String visibleTrendingTabsString;
    
    List<String> discoverTabs = null;
    List<String> invisibleDiscoverTabs = null;
    
    List<String> trendingTabs = null;
    List<String> invisibleTrendingTabs = null;
	@DefaultHandler
	public Resolution route() {
	    if (!getCurrentUserAdmin()) {
            return new ErrorResolution(403, "Need to login");
        }
	    
	    PageLimit pageLimit = getPageLimit("row");
	    pageLimit.setPageSize(100);
	    pageLimit.setOrderBy("id");
	    pageLimit.setAsc(true);
	    pageResult = localeDao.pageQuery(pageLimit);
		return forward();
    }

	public Resolution searchByLocale() throws JsonParseException, JsonMappingException, IOException {
	    relLocale = localeDao.getAvailableInputLocale(searchLocale);
	    if(relLocale.getId() == null) {
	        relLocale = null;
	        return new RedirectResolution(DiscoverTabManageAction.class, "route");
	    }
	        
	    localeId = relLocale.getId();
	    return editRoute();
	}
	
	public Resolution editRoute() throws JsonParseException, JsonMappingException, IOException {
	    if (!getCurrentUserAdmin())
            return new ErrorResolution(403, "Need to login");

	    if(localeId == null)
	        return new ErrorResolution(400, "Bad request");
	    
	    setRelLocale(localeDao.findById(localeId));
	    discoverTabs = Locale.turnEnumListtoStringList(relLocale.getDiscoverTabs());
	    invisibleDiscoverTabs =Locale.turnEnumListtoStringList(Locale.getNotInDiscoverTypes(relLocale.getDiscoverTabs()));
	    
	    trendingTabs = Locale.turnEnumListtoStringList(relLocale.getTrendingTabs());
	    invisibleTrendingTabs =Locale.turnEnumListtoStringList(Locale.getNotInTrendingTabTypes(relLocale.getTrendingTabs()));
	    trendingTabs = CheckTrendingTabInDiscoverTab(trendingTabs,discoverTabs);
	    invisibleTrendingTabs = CheckTrendingTabInDiscoverTab(invisibleTrendingTabs,discoverTabs);
	    
	    return forward();
	}
	
	
    @SuppressWarnings("unchecked")
	public Resolution save() throws JsonParseException, JsonMappingException, IOException {
    	if (getCurrentUser() == null) {
    		return new StreamingResolution("text/html", "Need Login");
        }
    	if(visibleDiscoverTabsString != null)
    		discoverTabs = Arrays.asList(visibleDiscoverTabsString.split(","));
    	else
    		discoverTabs = new LinkedList<String>();
    	if(visibleTrendingTabsString != null)
    		trendingTabs = Arrays.asList(visibleTrendingTabsString.split(","));
    	else
    		trendingTabs = new LinkedList<String>();
    	JSONArray discoverTabsJson = new JSONArray();
    	discoverTabsJson.addAll(discoverTabs);
    	JSONArray trendingTabsJson = new JSONArray();
    	trendingTabsJson.addAll(trendingTabs);
        List<DiscoverTabType> discoverTabs = objectMapper.readValue(discoverTabsJson.toJSONString(), new TypeReference<List<DiscoverTabType>>() {});
        List<TrendingTabType> trendingTabs = objectMapper.readValue(trendingTabsJson.toJSONString(), new TypeReference<List<TrendingTabType>>() {});
        relLocale = localeDao.findById(localeId);
        relLocale.setDiscoverTabs(discoverTabs);
        relLocale.setTrendingTabs(trendingTabs);
        localeDao.update(relLocale);
        return new RedirectResolution(DiscoverTabManageAction.class, "route");
    }
    
	private List<String> CheckTrendingTabInDiscoverTab(List<String> trendingTabs,List<String> discoverTabs){
		List<String> newTrendingTabsList = new LinkedList<String>();
		if(trendingTabs != null && trendingTabs.size() >0){
			if(discoverTabs != null && discoverTabs.size() >0)
			{
				for(String trendingTab : trendingTabs){
					if(discoverTabs.contains(trendingTab)){
						newTrendingTabsList.add(trendingTab);
					}
				}
			}
		}
		return newTrendingTabsList;
	}
	
    public PageResult<Locale> getPageResult() {
        return pageResult;
    }

    public void setPageResult(PageResult<Locale> pageResult) {
        this.pageResult = pageResult;
    }

    public Long getLocaleId() {
        return localeId;
    }

    public void setLocaleId(Long localeId) {
        this.localeId = localeId;
    }

    public Locale getRelLocale() {
        return relLocale;
    }

    public void setRelLocale(Locale relLocale) {
        this.relLocale = relLocale;
    }

	public String getSearchLocale() {
        return searchLocale;
    }

    public void setSearchLocale(String searchLocale) {
        this.searchLocale = searchLocale;
    }

	public List<String> getDiscoverTabs() {
		return discoverTabs;
	}

	public void setDiscoverTabs(List<String> discoverTabs) {
		this.discoverTabs = discoverTabs;
	}

	public List<String> getInvisibleDiscoverTabs() {
		return invisibleDiscoverTabs;
	}

	public void setInvisibleDiscoverTabs(List<String> invisibleDiscoverTabs) {
		this.invisibleDiscoverTabs = invisibleDiscoverTabs;
	}

	public List<String> getTrendingTabs() {
		return trendingTabs;
	}

	public void setTrendingTabs(List<String> trendingTabs) {
		this.trendingTabs = trendingTabs;
	}

	public List<String> getInvisibleTrendingTabs() {
		return invisibleTrendingTabs;
	}

	public void setInvisibleTrendingTabs(List<String> invisibleTrendingTabs) {
		this.invisibleTrendingTabs = invisibleTrendingTabs;
	}

	public void setVisibleDiscoverTabsString(String visibleDiscoverTabsString) {
		this.visibleDiscoverTabsString = visibleDiscoverTabsString;
	}

	public void setVisibleTrendingTabsString(String visibleTrendingTabsString) {
		this.visibleTrendingTabsString = visibleTrendingTabsString;
	}
	
}
