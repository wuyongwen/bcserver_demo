package com.cyberlink.cosmetic.modules.post.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.core.web.jackson.Views.Public;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PostTags implements Serializable {
    
    public static class MainPostDbView extends Public {
    }
    
    public static class MainPostDetailView extends MainPostDbView {
    }
    
    private static final long serialVersionUID = -4410580703624751515L;
    
    @JsonView(MainPostDetailView.class)
    public Set<String> emojiTags = null;

    @JsonView(MainPostDetailView.class)
    public Set<Long> circleTags = new HashSet<Long>();
    
    @JsonView(MainPostDetailView.class)
    private List<PostProductTag> productTags;
    
    @JsonView(MainPostDetailView.class)
    private List<PostExProductTag> exProductTags;

    @JsonView(MainPostDbView.class)
    private Set<String> userDefTags;

	@JsonView(MainPostDbView.class)
	private Set<String> keywords;
    
    @JsonView(Views.Public.class)
    public String lookTag = null;
    
    @JsonView(Views.Public.class)
    public PostHoroscopeTag horoscopeTag;
    
    @JsonView(MainPostDbView.class)
    public String lookVer = null;
    
    public Boolean IsNullProductTags() {
        return productTags == null;
    }
    public Boolean IsNullExProductTags() {
        return exProductTags == null;
    }
    
    public Boolean IsNullUserDefTags() {
        return userDefTags == null;
    }
    
    public Boolean IsNullKeywords() {
        return keywords == null;
    }
    
    public Set<String> getEmojiTags() {
        return emojiTags;
    }

    public String getLookVer() {
        return lookVer;
    }
    
    public void setCircleTags(Set<Long> circleTags) {
        if(circleTags == null)
            return;
        this.circleTags = circleTags;
    }
    
    public Set<Long> getCircleTags() {
        return circleTags;
    }

    public void setProductTags(List<PostProductTag> productTags) {
        if(productTags == null)
            return;
        this.productTags = productTags;
    }
    
    public List<PostProductTag> getProductTags() {
        if(productTags == null)
            productTags = new ArrayList<PostProductTag>();
        return productTags;
    }
    
	public void setExProductTags(List<PostExProductTag> exProductTags) {
		if(exProductTags == null)
            return;
		this.exProductTags = exProductTags;
	}

    public List<PostExProductTag> getExProductTags() {
        if(exProductTags == null)
            exProductTags = new ArrayList<PostExProductTag>();
		return exProductTags;
	}

    public String getLookTag() {
        return lookTag;
    }

	public PostHoroscopeTag getHoroscopeTag() {
		return horoscopeTag;
	}
	
	public Set<String> getUserDefTags() {
        if(userDefTags == null)
            userDefTags = new HashSet<String>();
        return userDefTags;
    }
    
    public void setUserDefTags(Set<String> userDefTags) {
        if(userDefTags == null)
            return;
        this.userDefTags = userDefTags;
    }
    
    public Set<String> getKeywords() {
        if(keywords == null)
            keywords = new HashSet<String>();
        return keywords;
    }
    
    public void setKeywords(Set<String> keywords) {
        if(keywords == null)
            return;
        this.keywords = keywords;
    }
    
    public void setLookVer(String lookVer) {
        this.lookVer = lookVer;
    }
}
