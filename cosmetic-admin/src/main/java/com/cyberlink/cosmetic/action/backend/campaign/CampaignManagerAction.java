package com.cyberlink.cosmetic.action.backend.campaign;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.campaign.dao.CampaignDao;
import com.cyberlink.cosmetic.modules.campaign.dao.CampaignGroupDao;
import com.cyberlink.cosmetic.modules.campaign.model.Campaign;
import com.cyberlink.cosmetic.modules.campaign.model.CampaignGroup;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.file.dao.FileDao;
import com.cyberlink.cosmetic.modules.file.dao.FileItemDao;
import com.restfb.util.StringUtils;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/campaign/campaignManager.action")
public class CampaignManagerAction extends AbstractAction{
	
	@SpringBean("common.localeDao")
    private LocaleDao localeDao;
	
	@SpringBean("campaign.campaignDao")
	private CampaignDao campaignDao;

	@SpringBean("campaign.campaignGroupDao")
	private CampaignGroupDao campaignGroupDao;
	
    @SpringBean("file.fileItemDao")
    private FileItemDao fileItemDao;
    
    @SpringBean("file.fileDao")
    private FileDao fileDao;
	
	private Long offset = Long.valueOf(0);
    private Long limit = Long.valueOf(20);
    private Long campaignGroupId;
    private String campaignGroupName;
    private Long campaignId;
	private Boolean isUpdate = Boolean.FALSE;
	private Boolean isCreate = Boolean.FALSE;
    
    private CampaignGroup campaignGroup;
    private Campaign campaign;
    private List<CampaignGroup> campaignGroupList;
    private List<String> campaignGroupLocaleList;
    private List<Campaign> campaignList;
    private List<String> localeList;
    private String locales;
    private String[] campaignGroupLocaleArray;
    private String imgfileOriginalUrl;
    private String imgfile720OriginalUrl;
    private String imgfile1080OriginalUrl;
    
    private String campaignListJsonString;
	
	@DefaultHandler
    public Resolution routeGroupEvent() {
		if (!getCurrentUserAdmin()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
		campaignGroupList = campaignGroupDao.getAllCampaignGroupByGroupName(offset, limit);
		return forward();
	}
    
	public Resolution modifyGroupEvent(){
		if (!getCurrentUserAdmin()) {
	    	return new StreamingResolution("text/html", "Need to login");
	    }
		if(campaignGroupName != null){
			campaignGroupLocaleList = campaignGroupDao.getAllCampaignGroupLocaleByGroupName(campaignGroupName);
			campaignGroup = campaignGroupDao.getCampaignGroupByGroupName(campaignGroupName).get(0);
		}
		return forward();
	}
	
	public Resolution createGroupEvent() {
		if (!getCurrentUserAdmin()) {
	    	return new StreamingResolution("text/html", "Need to login");
	    }
		if(localeList.size() > 0){
			for(String locale : localeList)
			{
				CampaignGroup newCampaignGroup = new CampaignGroup();
				newCampaignGroup.setName(campaignGroup.getName());
				newCampaignGroup.setRotationPeriod(campaignGroup.getRotationPeriod());
				newCampaignGroup.setLocale(locale);
		    	campaignGroupDao.create(newCampaignGroup);
			}
		}
    	return new RedirectResolution(CampaignManagerAction.class, "routeGroupEvent");
	}
	
    public Resolution updateGroupEvent() {
		if (!getCurrentUserAdmin()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
		List<String> newLocaleList = new ArrayList<String>();
		newLocaleList.addAll(localeList);
		List<CampaignGroup> oldCampaignGroupList = campaignGroupDao.getCampaignGroupByGroupName(campaignGroupName);
		for(CampaignGroup oldCampaignGroup : oldCampaignGroupList){
			if(newLocaleList.contains(oldCampaignGroup.getLocale())){
				newLocaleList.remove(oldCampaignGroup.getLocale());
				oldCampaignGroup.setName(campaignGroup.getName());
				oldCampaignGroup.setRotationPeriod(campaignGroup.getRotationPeriod());
				campaignGroupDao.update(oldCampaignGroup);
			}else{
				List<Campaign> campaignList = oldCampaignGroup.getCampaigns();
				if(campaignList.size() > 0){
					for(Campaign campaign : campaignList){
						campaignDao.delete(campaign.getId());
					}
				}
				campaignGroupDao.delete(oldCampaignGroup.getId());
			}
		}
		if(!newLocaleList.isEmpty()){
			for(String locale : newLocaleList){
				CampaignGroup newCampaignGroup = new CampaignGroup();
				newCampaignGroup.setLocale(locale);
				newCampaignGroup.setName(campaignGroup.getName());
				newCampaignGroup.setRotationPeriod(campaignGroup.getRotationPeriod());
				campaignGroupDao.create(newCampaignGroup);
			}
		}
    	return new RedirectResolution(CampaignManagerAction.class, "routeGroupEvent");
    }
	
	public Resolution deleteGroupEvent(){
		if (!getCurrentUserAdmin()) {
	    	return new StreamingResolution("text/html", "Need to login");
	    }
		if(campaignGroupName != null){
			List<CampaignGroup> campaignGroupList = campaignGroupDao.getCampaignGroupByGroupName(campaignGroupName);
			for(CampaignGroup deleteCampaignGroup : campaignGroupList){
				List<Campaign> campaignList = deleteCampaignGroup.getCampaigns();
				if(campaignList.size() > 0){
					for(Campaign campaign : campaignList){
						campaignDao.delete(campaign.getId());
					}
				}
				campaignGroupDao.delete(deleteCampaignGroup.getId());
			}
		}
		return new RedirectResolution(CampaignManagerAction.class, "routeGroupEvent");
	}

    public Resolution routeGroup() {
		if (!getCurrentUserAdmin()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
		campaignGroupList = campaignGroupDao.getCampaignGroupByGroupName(campaignGroupName);
		campaignGroup = campaignGroupList.get(0);
		return forward();
	}
	
	public Resolution deleteGroup(){
		if (!getCurrentUserAdmin()) {
	    	return new StreamingResolution("text/html", "Need to login");
	    }
		if(campaignGroupId != null){
			List<Campaign> campaignList = campaignGroupDao.findById(campaignGroupId).getCampaigns();
			if(campaignList.size() > 0){
				for(Campaign campaign : campaignList){
					campaignDao.delete(campaign.getId());
				}
			}
			campaignGroupDao.delete(campaignGroupId);
		}
		return new RedirectResolution(CampaignManagerAction.class, "routeGroup").addParameter("campaignGroupName", campaignGroupName);
	}
	
    
    public Resolution route() {
		if (!getCurrentUserAdmin()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
		campaignGroup = campaignGroupDao.findById(campaignGroupId);
		campaignList = campaignDao.findByGroupId(campaignGroupId, offset, limit).getResults();
		return forward();
	}
    
	public Resolution modify(){
		if (!getCurrentUserAdmin()) {
	    	return new StreamingResolution("text/html", "Need to login");
	    }
		if(campaignId != null){
			campaign = campaignDao.findById(campaignId);
			if(fileDao.exists(null))
				imgfileOriginalUrl = fileItemDao.findOriginal(campaign.getFileId()).getOriginalUrl();
			if(fileDao.exists(campaign.getFile720Id()))
				imgfile720OriginalUrl = fileItemDao.findOriginal(campaign.getFile720Id()).getOriginalUrl();
			if(fileDao.exists(campaign.getFile1080Id()))
				imgfile1080OriginalUrl = fileItemDao.findOriginal(campaign.getFile1080Id()).getOriginalUrl();
		}
		return forward();
	}
	
	public Resolution modifyCreateAll(){
		if (!getCurrentUserAdmin()) {
	    	return new StreamingResolution("text/html", "Need to login");
	    }
		campaignGroupList = campaignGroupDao.getCampaignGroupByGroupName(campaignGroupName);
		locales = "";
		if(campaignGroupList.size() > 0){
			for(CampaignGroup campaignGroup : campaignGroupList){
				locales += (campaignGroup.getLocale() + " "); 
			}
			locales = locales.substring(0,locales.length()-1);
		}
		return forward();
	}

	public Resolution create() {
		if (!getCurrentUserAdmin()) {
	    	return new StreamingResolution("text/html", "Need to login");
	    }
		if(campaignGroupName != null && campaignGroupId != null){
			Campaign newCampaign = new Campaign();
			newCampaign.setFileId(campaign.getFileId());
			newCampaign.setFile720Id(campaign.getFile720Id());
			newCampaign.setFile1080Id(campaign.getFile1080Id());
			newCampaign.setLink(campaign.getLink());
			newCampaign.setEndDate(campaign.getEndDate());
			newCampaign.setGroupId(campaign.getGroupId());
	    	campaignDao.create(newCampaign);
			return new RedirectResolution(CampaignManagerAction.class, "route").addParameter("campaignGroupId", campaignGroupId).addParameter("campaignGroupName", campaignGroupName);
		}else if(campaignGroupName != null){
			List<CampaignGroup> campaignGroupList = campaignGroupDao.getCampaignGroupByGroupName(campaignGroupName);
			for(CampaignGroup campaignGroup : campaignGroupList){
				Campaign newCampaign = new Campaign();
				newCampaign.setFileId(campaign.getFileId());
				newCampaign.setFile720Id(campaign.getFile720Id());
				newCampaign.setFile1080Id(campaign.getFile1080Id());
				newCampaign.setLink(campaign.getLink());
				newCampaign.setEndDate(campaign.getEndDate());
				newCampaign.setGroupId(campaignGroup.getId());
		    	campaignDao.create(newCampaign);
			}
			return new RedirectResolution(CampaignManagerAction.class, "routeGroup").addParameter("campaignGroupName", campaignGroupName);
		}
		return new RedirectResolution(CampaignManagerAction.class, "route");
	}
	
	public Resolution createAll() {
		if (!getCurrentUserAdmin()) {
	    	return new StreamingResolution("text/html", "Need to login");
	    }
		JSONParser parser = new JSONParser();
		JSONArray campaignArray = new JSONArray();
		try {
			campaignArray = (JSONArray)parser.parse(campaignListJsonString);
		} catch (ParseException e) {}
		for(int index = 0;index < campaignArray.size();index++){
			JSONObject jsonObject = (JSONObject)campaignArray.get(index);
			Campaign newCampaign = new Campaign();
			if(jsonObject.containsKey("fileId"))
				newCampaign.setFileId(Long.valueOf(jsonObject.get("fileId").toString()));
			if(jsonObject.containsKey("file720Id"))
				newCampaign.setFile720Id(Long.valueOf(jsonObject.get("file720Id").toString()));
			if(jsonObject.containsKey("file1080Id"))
				newCampaign.setFile1080Id(Long.valueOf(jsonObject.get("file1080Id").toString()));
			if(jsonObject.containsKey("link"))
				newCampaign.setLink(StringUtils.isBlank(jsonObject.get("link").toString())?null:jsonObject.get("link").toString());
			if(jsonObject.containsKey("endDate")){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					newCampaign.setEndDate(sdf.parse(jsonObject.get("endDate").toString()));
				} catch (java.text.ParseException e) {}
			}
			if(jsonObject.containsKey("campaignGroupId"))
				newCampaign.setGroupId(Long.valueOf(jsonObject.get("campaignGroupId").toString()));
	    	campaignDao.create(newCampaign);
		}
		return new RedirectResolution(CampaignManagerAction.class, "routeGroup").addParameter("campaignGroupName", campaignGroupName);
	}
    
    public Resolution update() {
		if (!getCurrentUserAdmin()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
		Campaign oldCampaign = campaignDao.findById(campaign.getId());
		oldCampaign.setFileId(campaign.getFileId());
		oldCampaign.setFile720Id(campaign.getFile720Id());
		oldCampaign.setFile1080Id(campaign.getFile1080Id());
		oldCampaign.setLink(campaign.getLink());
		oldCampaign.setEndDate(campaign.getEndDate());
		oldCampaign.setGroupId(campaign.getGroupId());
		campaignDao.update(oldCampaign);
    	return new RedirectResolution(CampaignManagerAction.class, "route").addParameter("campaignGroupId", campaignGroupId).addParameter("campaignGroupName", campaignGroupName);
    }
	
	public Resolution delete(){
		if (!getCurrentUserAdmin()) {
	    	return new StreamingResolution("text/html", "Need to login");
	    }
		if(campaignId != null){
			campaignDao.delete(campaignId);
		}
		return new RedirectResolution(CampaignManagerAction.class, "route").addParameter("campaignGroupId", campaignGroupId).addParameter("campaignGroupName", campaignGroupName);
	}
	
    public Resolution cancel() {
		if(campaignGroupName != null && campaignGroupId != null){
			return new RedirectResolution(CampaignManagerAction.class, "route").addParameter("campaignGroupId", campaignGroupId).addParameter("campaignGroupName", campaignGroupName);
		}
		return new RedirectResolution(CampaignManagerAction.class, "routeGroup").addParameter("campaignGroupName", campaignGroupName);
	}
    
	public Long getOffset() {
		return offset;
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

	public List<CampaignGroup> getCampaignGroupList() {
		return campaignGroupList;
	}
	
	public List<Campaign> getCampaignList() {
		return campaignList;
	}

	public void setCampaignList(List<Campaign> campaignList) {
		this.campaignList = campaignList;
	}

	public CampaignGroup getCampaignGroup() {
		return campaignGroup;
	}

	public void setCampaignGroup(CampaignGroup campaignGroup) {
		this.campaignGroup = campaignGroup;
	}
	
	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}
	
	public Long getCampaignGroupId() {
		return campaignGroupId;
	}

	public void setCampaignGroupId(Long campaignGroupId) {
		this.campaignGroupId = campaignGroupId;
	}

	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
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

	public String getImgfileOriginalUrl() {
		return imgfileOriginalUrl;
	}

	public void setImgfileOriginalUrl(String imgfileOriginalUrl) {
		this.imgfileOriginalUrl = imgfileOriginalUrl;
	}

	public String getImgfile720OriginalUrl() {
		return imgfile720OriginalUrl;
	}

	public void setImgfile720OriginalUrl(String imgfile720OriginalUrl) {
		this.imgfile720OriginalUrl = imgfile720OriginalUrl;
	}

	public String getImgfile1080OriginalUrl() {
		return imgfile1080OriginalUrl;
	}

	public void setImgfile1080OriginalUrl(String imgfile1080OriginalUrl) {
		this.imgfile1080OriginalUrl = imgfile1080OriginalUrl;
	}

	public void setLocaleList(List<String> localeList) {
		this.localeList = localeList;
	}
	
	public String getLocales() {
		return locales;
	}

	public void setLocales(String locales) {
		this.locales = locales;
	}

	public String getCampaignGroupName() {
		return campaignGroupName;
	}

	public void setCampaignGroupName(String campaignGroupName) {
		this.campaignGroupName = campaignGroupName;
	}

	public List<String> getCampaignGroupLocaleList() {
		return campaignGroupLocaleList;
	}

	public String[] getCampaignGroupLocaleArray() {
		return campaignGroupLocaleArray;
	}

	public void setCampaignGroupLocaleArray(String[] campaignGroupLocaleArray) {
		this.campaignGroupLocaleArray = campaignGroupLocaleArray;
	}

	public String getCampaignListJsonString() {
		return campaignListJsonString;
	}

	public void setCampaignListJsonString(String campaignListJsonString) {
		this.campaignListJsonString = campaignListJsonString;
	}
	
}
