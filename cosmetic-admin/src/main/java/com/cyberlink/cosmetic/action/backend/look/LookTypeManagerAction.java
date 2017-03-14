package com.cyberlink.cosmetic.action.backend.look;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.action.backend.UserAccessControl;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTagDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTagGroupDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeGroupDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleTag;
import com.cyberlink.cosmetic.modules.circle.model.CircleTagGroup;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;
import com.cyberlink.cosmetic.modules.circle.model.CircleTypeGroup;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.look.dao.LookTypeDao;
import com.cyberlink.cosmetic.modules.look.model.LookType;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@UrlBinding("/look/look-type-manager.action")
public class LookTypeManagerAction extends AbstractAction{
    
    @SpringBean("look.LookTypeDao")
    private LookTypeDao lookTypeDao;
	
    @SpringBean("common.localeDao")
    private LocaleDao localeDao;
    
	private List<LookType> lookTypes = new ArrayList<LookType>();
	private List<String> availableLocale = new ArrayList<String>();
	private Long lookTypeId = null;
    private String typeNameMap;
    private Long iconId;
    private String iconUrl;
    private String locale;
    private Boolean isVisible;
    private String typeName;
    private String typeCodeName;

    public List<LookType> getLookTypes() {
	   return lookTypes;
	}
	
	public Long getLookTypeId() {
	    return lookTypeId;
	}
	
	public void setLookTypeId(Long lookTypeId) {
	    this.lookTypeId = lookTypeId;
	}
	
    public String getTypeNameMap() {
        return typeNameMap;
    }

    public void setTypeNameMap(String typeNameMap) {
        this.typeNameMap = typeNameMap;
    }
	
	public void setIconId(Long iconId) {
        this.iconId = iconId;
    }
	
	public Long getIconId() {
        return iconId;
    }
	
    public String getIconUrl() {
        return iconUrl;
    }
    
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
	
    public String getLocale() {
        return locale;
    }

    public String getTypeName() {
        return typeName;
    }
    
    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }
    
    public List<String> getAvailableLocale() {
        return availableLocale;
    }
    
    public String getTypeCodeName() {
        return typeCodeName;
    }

    public void setTypeCodeName(String typeCodeName) {
        this.typeCodeName = typeCodeName;
    }
    
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
        
	    lookTypes = lookTypeDao.findAll();
	    return forward();
	}
	
	public Resolution newType() {
	    ErrorResolution err = authorized();
        if(err != null)
            return err;
        
	    if(lookTypeId != null) {
	        LookType lookType = lookTypeDao.findById(lookTypeId);
	        if(lookType != null) {
	            typeName = lookType.getName();
	            typeCodeName = lookType.getCodeName();
	            iconId = lookType.getBgImgId();
	            iconUrl = lookType.getBgImgUrl();
	            locale = lookType.getLocale();
	            isVisible = lookType.getIsVisible();
	        }
	        else
	            return new ErrorResolution(400, "Bad lookTypeId");
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
        
        if(lookTypeId != null) {
            LookType lookType = lookTypeDao.findById(lookTypeId);
            if(lookType != null) {
                typeName = lookType.getName();
                typeCodeName = lookType.getCodeName();
                iconId = lookType.getBgImgId();
                iconUrl = lookType.getBgImgUrl();
                locale = lookType.getLocale();
                isVisible = lookType.getIsVisible();
            }
            else
                return new ErrorResolution(400, "Bad lookTypeId");
        }
        else
            locale = "en_US";
        
        lookTypeId = null;
        availableLocale.addAll(localeDao.getAvailableLocaleByType(LocaleType.POST_LOCALE));
        return new ForwardResolution("look-type-manager-new-type.jsp");
    }
	
	public Resolution create() {
	    ErrorResolution err = authorized();
        if(err != null)
            return err;

        if(typeNameMap == null || typeNameMap.length() <= 0)
            return new RedirectResolution("./look/look-type-manager.action");
        
        JsonNode typeLocaleObj = null;
        
        try {
            final ObjectMapper m = BeanLocator.getBean("web.objectMapper");
            typeLocaleObj = m.readValue(typeNameMap, JsonNode.class);
        } catch (Exception e) {
            return new ErrorResolution(400, "Bad typeNameMap");
        }
            
        
        Iterator<String> locales = typeLocaleObj.fieldNames();
        
        if(!locales.hasNext()) 
            return new ErrorResolution(400, "Empty typeNameMap");

        if(typeCodeName == null || typeCodeName.length() <= 0)
            return new ErrorResolution(400, "Empty typeCodeName");
        
        Boolean isUpdate = false;
        LookType lt = null;
        if(lookTypeId != null && lookTypeDao.exists(lookTypeId)) {
            lt = lookTypeDao.findById(lookTypeId);   
            isUpdate = true;
        }
        else if(lookTypeId != null) {
            return new ErrorResolution(400, "Bad lookTypeId");
        }
        else {
            lt = new LookType();
        }
        String loc = locales.next();
        String typeName = typeLocaleObj.get(loc).asText();
        
        if(typeName == null || typeName.length() <= 0) 
            return new ErrorResolution(400, "Empty typeNameMap");
        
        lt.setName(typeName);
        lt.setCodeName(typeCodeName);
        lt.setLocale(loc);
        lt.setbgImgId(iconId);
        lt.setBgImgUrl(iconUrl);
        lt.setIsVisible(isVisible);
        if(isUpdate)
            lookTypeDao.update(lt);
        else
            lookTypeDao.create(lt);
        return json("Done");
	}
	
	public Resolution delete() {
        ErrorResolution err = authorized();
        if(err != null)
            return err;
        
        LookType lookType = null;
        if(lookTypeId != null && lookTypeDao.exists(lookTypeId)) {
            lookType = lookTypeDao.findById(lookTypeId);   
            lookType.setIsDeleted(true);
            lookTypeDao.update(lookType);
            return json("Done");
        }

        return new ErrorResolution(400, "Bad lookTypeId");
	}
	
	public Resolution show() {
        ErrorResolution err = authorized();
        if(err != null)
            return err;
        
        LookType lookType = null;
        if(lookTypeId != null && lookTypeDao.exists(lookTypeId)) {
            lookType = lookTypeDao.findById(lookTypeId);   
            lookType.setIsVisible(true);
            lookTypeDao.update(lookType);
            return json("Done");
        }

        return new ErrorResolution(400, "Bad lookTypeId");
    }
	
	public Resolution hide() {
        ErrorResolution err = authorized();
        if(err != null)
            return err;
        
        LookType lookType = null;
        if(lookTypeId != null && lookTypeDao.exists(lookTypeId)) {
            lookType = lookTypeDao.findById(lookTypeId);   
            lookType.setIsVisible(false);
            lookTypeDao.update(lookType);
            return json("Done");
        }

        return new ErrorResolution(400, "Bad lookTypeId");
    }
}
