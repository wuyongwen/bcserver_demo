package com.cyberlink.cosmetic.action.api.campaign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.campaign.dao.CampaignGroupDao;
import com.cyberlink.cosmetic.modules.campaign.model.CampaignGroup;
import com.cyberlink.cosmetic.modules.campaign.model.result.CampaignGroupWrapper;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/campaign/list-group.action")
public class ListCampaignGroupAction extends AbstractAction{
	@SpringBean("campaign.campaignGroupDao")
	private CampaignGroupDao campaignGroupDao;
	
	private Long offset = Long.valueOf(0);
    private Long limit = Long.valueOf(10);
	private String locale;

	@DefaultHandler
	public Resolution route() {
    	final Map<String, Object> results = new HashMap<String, Object>();
    	PageResult<CampaignGroup> pageResult = campaignGroupDao.findByLocale(locale, offset, limit);
    	List<CampaignGroupWrapper> list = new ArrayList<CampaignGroupWrapper>();
    	for (CampaignGroup c : pageResult.getResults()) {
    		list.add(new CampaignGroupWrapper(c));
    	}

    	results.put("results", list);
    	results.put("totalSize", pageResult.getTotalSize());    	
    	return json(results);
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
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
}
