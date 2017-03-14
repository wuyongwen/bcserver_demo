package com.cyberlink.cosmetic.action.backend.post;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.post.dao.PostDefaultTagDao;
import com.cyberlink.cosmetic.modules.post.model.PostDefaultTag;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@UrlBinding("/post/post-default-tag-manager.action")
public class PostDefaultTagManagerAction extends AbstractAction{
    
    @SpringBean("post.PostDefaultTagDao")
    private PostDefaultTagDao postDefaultTagDao;
	
    @SpringBean("common.localeDao")
    private LocaleDao localeDao;

    private List<PostDefaultTag> defaultTags = new ArrayList<PostDefaultTag>();
    private List<String> availableLocale = new ArrayList<String>();
	private Long defaultTagId = null;
    private String tagNameMap;
    private String locale;
    private String tagName;
    private Boolean isDeleted;
    
	public ErrorResolution authorized() {
	    User curUser = getCurrentUser();
        if(curUser == null)
            return new ErrorResolution(403, "You need to login");
        
        if(!getCurrentUserAdmin()) {
            return new ErrorResolution(403, "You need to login");
        }
        
        return null;
	}
	
	@DefaultHandler
	public Resolution route() {
	    ErrorResolution err = authorized();
	    if(err != null)
	        return err;
        
	    defaultTags = postDefaultTagDao.findAll();
	    return forward();
	}
	
	public Resolution newTag() {
	    ErrorResolution err = authorized();
        if(err != null)
            return err;
        
	    if(defaultTagId != null) {
	        PostDefaultTag tag = postDefaultTagDao.findById(defaultTagId);
	        if(tag != null) {
	            tagName = tag.getTagName();
	            locale = tag.getLocale();
	            isDeleted = tag.getIsDeleted();
	        }
	        else
	            return new ErrorResolution(400, "Bad defaultTagId");
	    }
	    else
	        locale = "en_US";
	    
        availableLocale.addAll(localeDao.getAvailableLocaleByType(LocaleType.POST_LOCALE));
		return forward();
	}
	
	public Resolution copy() {
	    ErrorResolution err = authorized();
        if(err != null)
            return err;
        
        if(defaultTagId != null) {
            PostDefaultTag tag = postDefaultTagDao.findById(defaultTagId);
            if(tag != null) {
                tagName = tag.getTagName();
                locale = tag.getLocale();
                isDeleted = tag.getIsDeleted();
            }
            else
                return new ErrorResolution(400, "Bad lookTypeId");
        }
        else
            locale = "en_US";
        
        defaultTagId = null;
        availableLocale.addAll(localeDao.getAvailableLocaleByType(LocaleType.POST_LOCALE));
        return new ForwardResolution("post-default-tag-manager-new-tag.jsp");
    }
	
	public Resolution create() {
	    ErrorResolution err = authorized();
        if(err != null)
            return err;

        if(tagNameMap == null || tagNameMap.length() <= 0)
            return new RedirectResolution("./post/post-default-tag-manager.action");
        
        JsonNode tagLocaleObj = null;
        
        try {
            final ObjectMapper m = BeanLocator.getBean("web.objectMapper");
            tagLocaleObj = m.readValue(tagNameMap, JsonNode.class);
        } catch (Exception e) {
            return new ErrorResolution(400, "Bad typeNameMap");
        }
            
        
        Iterator<String> locales = tagLocaleObj.fieldNames();
        
        if(!locales.hasNext()) 
            return new ErrorResolution(400, "Empty typeNameMap");

        Boolean isUpdate = false;
        PostDefaultTag tag = null;
        if(defaultTagId != null && postDefaultTagDao.exists(defaultTagId)) {
            tag = postDefaultTagDao.findById(defaultTagId);   
            isUpdate = true;
        }
        else if(defaultTagId != null) {
            return new ErrorResolution(400, "Bad lookTypeId");
        }
        else {
            tag = new PostDefaultTag();
        }
        String loc = locales.next();
        String typeName = tagLocaleObj.get(loc).asText();
        
        if(typeName == null || typeName.length() <= 0) 
            return new ErrorResolution(400, "Empty typeNameMap");
        
        tag.setTagName(typeName);
        tag.setLocale(loc);
        tag.setIsDeleted(isDeleted);
        if(isUpdate)
            postDefaultTagDao.update(tag);
        else
            postDefaultTagDao.create(tag);
        return json("Done");
	}
	
	public Resolution show() {
        ErrorResolution err = authorized();
        if(err != null)
            return err;
        
        PostDefaultTag tag = null;
        if(defaultTagId != null && postDefaultTagDao.exists(defaultTagId)) {
            tag = postDefaultTagDao.findById(defaultTagId);   
            tag.setIsDeleted(false);
            postDefaultTagDao.update(tag);
            return json("Done");
        }

        return new ErrorResolution(400, "Bad lookTypeId");
    }
	
	public Resolution hide() {
        ErrorResolution err = authorized();
        if(err != null)
            return err;
        
        PostDefaultTag tag = null;
        if(defaultTagId != null && postDefaultTagDao.exists(defaultTagId)) {
            tag = postDefaultTagDao.findById(defaultTagId);   
            tag.setIsDeleted(true);
            postDefaultTagDao.update(tag);
            return json("Done");
        }

        return new ErrorResolution(400, "Bad lookTypeId");
    }
	
    public List<PostDefaultTag> getDefaultTags() {
        return defaultTags;
    }

    public void setDefaultTags(List<PostDefaultTag> defaultTags) {
        this.defaultTags = defaultTags;
    }

    public List<String> getAvailableLocale() {
        return availableLocale;
    }

    public void setAvailableLocale(List<String> availableLocale) {
        this.availableLocale = availableLocale;
    }

    public Long getDefaultTagId() {
        return defaultTagId;
    }

    public void setDefaultTagId(Long defaultTagId) {
        this.defaultTagId = defaultTagId;
    }

    public String getTagNameMap() {
        return tagNameMap;
    }

    public void setTagNameMap(String tagNameMap) {
        this.tagNameMap = tagNameMap;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
