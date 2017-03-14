package com.cyberlink.cosmetic.action.backend.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.event.dao.BeautyInsightDao;
import com.cyberlink.cosmetic.modules.event.model.BeautyInsight;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/event/EventBeautyInsightManager.action")
public class EventBeautyInsightManagerAction extends AbstractAction{
    
	@SpringBean("event.beautyInsightDao")
	protected BeautyInsightDao beautyInsightDao;
    
	@SpringBean("common.localeDao")
    private LocaleDao localeDao;
	
	private Long offset = Long.valueOf(0);
    private Long limit = Long.valueOf(10);
    private String defaultLocale = "en_US";
    private String locale;
    private List<String> availableLocale = new ArrayList<String>();
	public List<BeautyInsight> beautyInsightList;
	private Long beautyInsightId;
	public BeautyInsight beautyInsight;
	private Long postId;
	private Boolean isUpdate = Boolean.FALSE;
	private Boolean isCreate = Boolean.FALSE;
	
	public void setBeautyInsightList(List<BeautyInsight> beautyInsightList) {
		this.beautyInsightList = beautyInsightList;
	}

	public List<BeautyInsight> getBeautyInsightList() {
		   return beautyInsightList;
	}
	
	public List<String> getAvailableLocale() {
		return availableLocale;
	}
	public BeautyInsight getBeautyInsight() {
		return beautyInsight;
	}

	public void setBeautyInsight(BeautyInsight beautyInsight) {
		this.beautyInsight = beautyInsight;
	}
	
	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public void setOffset(Long offset) {
		this.offset = offset;
	}

	public Long getLimit() {
		return limit;
	}

	public void setLimit(Long limit) {
		this.limit = limit;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public Long getBeautyInsightId() {
		return beautyInsightId;
	}

	public void setBeautyInsightId(Long beautyInsightId) {
		this.beautyInsightId = beautyInsightId;
	}
	
	public Boolean getIsUpdate() {
		return isUpdate;
	}
	
	public void setIsUpdate(Boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public Boolean getIsCreate() {
		return isCreate;
	}
	
	public void setIsCreate(Boolean isCreate) {
		this.isCreate = isCreate;
	}

	@DefaultHandler
    public Resolution route() {
		if (!getCurrentUserAdmin() && !getAccessControl().getEventManagerAccess()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
		if(locale == null){
			locale = this.defaultLocale;
		}
		availableLocale.addAll(localeDao.getAvailableLocaleByType(LocaleType.POST_LOCALE));
		beautyInsightList = beautyInsightDao.listBeautyInsightByLocale(locale, Long.valueOf(offset), Long.valueOf(limit)).getResults();
		return forward();
	}

    public Resolution modify() {
		if (!getCurrentUserAdmin() && !getAccessControl().getEventManagerAccess()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
		availableLocale.addAll(localeDao.getAvailableLocaleByType(LocaleType.POST_LOCALE));
		if(beautyInsightId != null) {
			beautyInsight = beautyInsightDao.findById(beautyInsightId);
			String metaData = beautyInsight.getMetadata();
			if (metaData != null && metaData.length() > 0) {
				try {
					JSONObject metaDataJson = new JSONObject(metaData);
					if (metaDataJson != null)
						postId = Long.parseLong(metaDataJson.get("postId").toString());
				} catch (JSONException e) {
					logger.error(e.getMessage());
				}
			}
		}
		return forward();
	}
	
    public Resolution delete() {
		if (!getCurrentUserAdmin() && !getAccessControl().getEventManagerAccess()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
		if(beautyInsightId != null){
			beautyInsightDao.delete(beautyInsightId);
		}
		return new RedirectResolution(EventBeautyInsightManagerAction.class).addParameter("locale", locale);
	}
    
    public Resolution cancel() {
		if (!getCurrentUserAdmin() && !getAccessControl().getEventManagerAccess()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
		return new RedirectResolution(EventBeautyInsightManagerAction.class).addParameter("locale", beautyInsight.getLocale());
	}
    
    public Resolution update() {
		if (!getCurrentUserAdmin() && !getAccessControl().getEventManagerAccess()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
		BeautyInsight oldBeautyInsight = beautyInsightDao.findById(beautyInsight.getId());
		oldBeautyInsight.setLocale(beautyInsight.getLocale());
		oldBeautyInsight.setImgUrl(beautyInsight.getImgUrl());
		oldBeautyInsight.setRedirectUrl(beautyInsight.getRedirectUrl());
		oldBeautyInsight.setDescription(beautyInsight.getDescription());
		Map<String, Object> mataDataMap = new HashMap<String, Object>();
		mataDataMap.put("postId", postId);
		JSONObject metaDataJson = new JSONObject(mataDataMap);
		oldBeautyInsight.setMetadata(metaDataJson.toString());
		beautyInsightDao.update(oldBeautyInsight);
    	return new RedirectResolution(EventBeautyInsightManagerAction.class).addParameter("locale", beautyInsight.getLocale());
    }
    
    public Resolution create() {
    	BeautyInsight newBeautyInsight = new BeautyInsight();
    	newBeautyInsight.setLocale(beautyInsight.getLocale());
    	newBeautyInsight.setImgUrl(beautyInsight.getImgUrl());
    	newBeautyInsight.setRedirectUrl(beautyInsight.getRedirectUrl());
    	newBeautyInsight.setDescription(beautyInsight.getDescription());
    	Map<String, Object> mataDataMap = new HashMap<String, Object>();
		mataDataMap.put("postId", postId);
		JSONObject metaDataJson = new JSONObject(mataDataMap);
    	newBeautyInsight.setMetadata(metaDataJson.toString());
    	beautyInsightDao.create(newBeautyInsight);
    	return new RedirectResolution(EventBeautyInsightManagerAction.class).addParameter("locale", beautyInsight.getLocale());
    }
}
