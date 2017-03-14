package com.cyberlink.cosmetic.action.api.event;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.helper.StringUtil;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.event.dao.BeautyInsightDao;
import com.cyberlink.cosmetic.modules.event.model.BeautyInsight;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/event/createBeautyBuzz.action")
public class CreateBeautyInsightAciton extends AbstractAction {

	@SpringBean("event.beautyInsightDao")
	protected BeautyInsightDao beautyInsightDao;
	
	private String locale;
	private String imgUrl;
	private String redirectUrl;
	private String description;
	private String metadata = "{}";

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public void setDescription(String description) {
		this.description = description; 
	}
	
	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}
	
	@DefaultHandler
    public Resolution route() {
		RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
		if(StringUtil.isBlank(locale)){
			return new ErrorResolution(ErrorDef.InvalidLocale);
		}
		if(StringUtil.isBlank(imgUrl)){
			return new ErrorResolution(ErrorDef.InvalidImgUrl);
		}
		if(StringUtil.isBlank(redirectUrl)){
			return new ErrorResolution(ErrorDef.InvalidRedirectUrl);
		}
		if(StringUtil.isBlank(description)){
			return new ErrorResolution(ErrorDef.InvalidDescription);
		}
		try {
			new JSONObject(metadata);
		} catch (JSONException e) {
			logger.error(e.getMessage());
			return new ErrorResolution(ErrorDef.InvalidJsonFormat);
		}
		
    	BeautyInsight newBeautyInsight = new BeautyInsight();
    	newBeautyInsight.setLocale(locale);
    	newBeautyInsight.setImgUrl(imgUrl);
    	newBeautyInsight.setRedirectUrl(redirectUrl);
    	newBeautyInsight.setDescription(description);
    	newBeautyInsight.setMetadata(metadata);
    	BeautyInsight newCreatedBeautyInsight = beautyInsightDao.create(newBeautyInsight);
    	final Map<String, Object> results = new HashMap<String, Object>();
	    results.put("beautyInsightId", newCreatedBeautyInsight.getId());
	    return new StreamingResolution("text/html", "create beauty insight information success");
    }
}