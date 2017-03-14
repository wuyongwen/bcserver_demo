package com.cyberlink.cosmetic.modules.common.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.slf4j.LoggerFactory;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@Table(name = "BC_LOCALE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class Locale extends AbstractCoreEntity<Long>{

    private static final long serialVersionUID = 1389909134824778508L;
    
    public enum DiscoverTabType {
        BEAUTYIST, BRAND, BEAUTY_TIP, EDITORIAL, BEAUTYINSIGHT, FREE_SAMPLE, CONTEST, LOOK, HOROSCOPE, STAR_OF_WEEK;
    	
    }
    
    public enum TrendingTabType {
    	BEAUTYIST, BRAND, BEAUTY_TIP, EDITORIAL, BEAUTYINSIGHT, FREE_SAMPLE, CONTEST, LOOK, HOROSCOPE;
    }
    
    public static <T> List<String> turnEnumListtoStringList(List<T> TabTypeList){
        if(!TabTypeList.isEmpty()){
        	List<String> TabsByStringList = new LinkedList<String>();
        	for(T TabType : TabTypeList)
        		TabsByStringList.add(TabType.toString());
        	return TabsByStringList;
        }else
        	return null;
    }
    

    public static List<DiscoverTabType> getNotInDiscoverTypes(List<DiscoverTabType> TabTypeList){
    			List<DiscoverTabType> invisibleDiscoverTabsList = new ArrayList<DiscoverTabType>();
    			DiscoverTabType[] allDiscoverTabType = DiscoverTabType.values();
    			for(DiscoverTabType discoverTabType : allDiscoverTabType){
    				if(!TabTypeList.contains(discoverTabType))
    					invisibleDiscoverTabsList.add(discoverTabType);
    			}
    			return invisibleDiscoverTabsList;
    }

	public static List<TrendingTabType> getNotInTrendingTabTypes(List<TrendingTabType> TabTypeList){
		List<TrendingTabType> invisibleTrendingTabsList = new ArrayList<TrendingTabType>();
		TrendingTabType[] allTrendingTabType = TrendingTabType.values();
		for(TrendingTabType trendingTabType : allTrendingTabType){
			if(!TabTypeList.contains(trendingTabType))
				invisibleTrendingTabsList.add(trendingTabType);
		}
		return invisibleTrendingTabsList;
	}
    
    private String inputLocale;
    private String userLocales;
    private String postLocale;
    private String productLocale;
    private String eventLocale;
    private String discoverTabString;
    private List<DiscoverTabType> discoverTabs;
    private List<DiscoverTabType> defaultTabs;
    static public String localeDelimiter = ",";
    
    private String trendingTabString;
    private List<TrendingTabType> trendingTabs;
    private List<TrendingTabType> defaultTrendingTabs;
    
    private Set<String> userLocaleList;
    
    @Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @JsonView(Views.Public.class)
    @Column(name = "ID", unique = true, nullable = false)
    public Long getId() {
        return id;
    }    
        
	@JsonView(Views.Public.class)
	@Column(name = "LOCALE")
	public String getInputLocale() {
		return inputLocale;
	}

	public void setInputLocale(String inputLocale) {
		this.inputLocale = inputLocale;
	}
	
    @Column(name = "USER")
    public String getUserLocales() {
        return userLocales;
    }

    public void setUserLocales(String userLocales) {
        this.userLocales = userLocales;
    }

    @Column(name = "POST")
    public String getPostLocale() {
        return postLocale;
    }

    public void setPostLocale(String postLocale) {
        this.postLocale = postLocale;
    }

    @Column(name = "PRODUCT")
    public String getProductLocale() {
        return productLocale;
    }

    public void setProductLocale(String productLocale) {
        this.productLocale = productLocale;
    }
    
    @Column(name = "EVENT")
    public String getEventLocale() {
		return eventLocale;
	}

	public void setEventLocale(String eventLocale) {
		this.eventLocale = eventLocale;
	}

	@Column(name = "DISCOVER_TAB")
    public String getDiscoverTabString() {
        return discoverTabString;
    }

    public void setDiscoverTabString(String discoverTabString) {
        this.discoverTabString = discoverTabString;
    }
    
    @Column(name = "TRENDING_TAB")
    public String getTrendingTabString() {
        return trendingTabString;
    }

    public void setTrendingTabString(String trendingTabString) {
        this.trendingTabString = trendingTabString;
    }
    
    @Transient
    @JsonView(Views.Public.class)
    public Set<String> getUserLocaleList() {
        if(userLocaleList == null)
            userLocaleList = new LinkedHashSet<String>(Arrays.asList(userLocales.split(localeDelimiter)));
        
        return userLocaleList;
    }
    
    public void setUserLocaleList(Set<String> userLocaleList) {
        this.userLocaleList = userLocaleList;
    }
    
    @Transient
    private List<DiscoverTabType> getDefaultDiscoverTab() {
        if(defaultTabs != null)
            return defaultTabs;
    
        defaultTabs = new ArrayList<DiscoverTabType>();
        defaultTabs.add(DiscoverTabType.BEAUTYIST);
        defaultTabs.add(DiscoverTabType.BEAUTY_TIP);
        return defaultTabs;
    }
    
    @Transient
    public List<DiscoverTabType> getDiscoverTabs() {

        if(discoverTabs != null)
            return discoverTabs;
        if(discoverTabString == null)
            return getDefaultDiscoverTab();
        
        try {
            ObjectMapper m = BeanLocator.getBean("web.objectMapper");
            discoverTabs = m.readValue(discoverTabString, new TypeReference<List<DiscoverTabType>>() {});
            return discoverTabs;
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("", e);
        }
        return getDefaultDiscoverTab();
    }
    
    public void setDiscoverTabs(List<DiscoverTabType> discoverTabs) {
        ObjectMapper m = BeanLocator.getBean("web.objectMapper");
        try {
            setDiscoverTabString(m.writer((PrettyPrinter)null).withView(Views.Public.class).writeValueAsString(discoverTabs));
            this.discoverTabs = discoverTabs;
        } catch (JsonProcessingException e) {
            LoggerFactory.getLogger(getClass()).error("", e);
        }
    }
    
    @Transient
    public List<String> getDiscoverTabsByStringList() {
    	List<DiscoverTabType> discoverTabs = getDiscoverTabs();
        if(!discoverTabs.isEmpty()){
        	List<String> discoverTabsByStringList = new LinkedList<String>();
        	for(DiscoverTabType discoverTab : discoverTabs)
        		discoverTabsByStringList.add(discoverTab.toString());
        	return discoverTabsByStringList;
        }else
        	return null;
    }
    
    @Transient
    public List<TrendingTabType> getTrendingTabs() {

        if(trendingTabs != null)
            return trendingTabs;
        if(trendingTabString == null)
            return getDefaultTrendingTab();
        
        try {
            ObjectMapper m = BeanLocator.getBean("web.objectMapper");
            trendingTabs = m.readValue(trendingTabString, new TypeReference<List<TrendingTabType>>() {});
            return trendingTabs;
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("", e);
        }
        return getDefaultTrendingTab();
    }
    
    public void setTrendingTabs(List<TrendingTabType> trendingTabs) {
        ObjectMapper m = BeanLocator.getBean("web.objectMapper");
        try {
        	setTrendingTabString(m.writer((PrettyPrinter)null).withView(Views.Public.class).writeValueAsString(trendingTabs));
            this.trendingTabs = trendingTabs;
        } catch (JsonProcessingException e) {
            LoggerFactory.getLogger(getClass()).error("", e);
        }
    }
    
    /**
     * Because treadingTabs is subset of discoverTabs, you must check whether the set value discoverTabs
     * @return defaultTrendingTabs
     */
    @Transient
    private List<TrendingTabType> getDefaultTrendingTab() {
        if(defaultTrendingTabs != null)
            return defaultTrendingTabs;
        defaultTrendingTabs = new ArrayList<TrendingTabType>();
        
        List<String> discoverTabTypeStringList = turnEnumListtoStringList(getDiscoverTabs());
        TrendingTabType[] deafaultTrendingTabTypes = {TrendingTabType.LOOK,
                                            TrendingTabType.EDITORIAL,
                                            TrendingTabType.BEAUTYIST,
                                            TrendingTabType.BEAUTY_TIP};
        for(TrendingTabType deafaultTrendingTabType : deafaultTrendingTabTypes)
        {
        	if(discoverTabTypeStringList.contains(deafaultTrendingTabType.toString()))
        		defaultTrendingTabs.add(deafaultTrendingTabType);
        }
        return defaultTrendingTabs;
    }
    
}
