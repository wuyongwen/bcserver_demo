package com.cyberlink.cosmetic.action.api.campaign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.campaign.dao.CampaignDao;
import com.cyberlink.cosmetic.modules.campaign.dao.CampaignGroupDao;
import com.cyberlink.cosmetic.modules.campaign.model.Campaign;
import com.cyberlink.cosmetic.modules.campaign.model.result.CampaignWrapper;

@UrlBinding("/api/campaign/list.action")
public class ListCampaignAction extends AbstractAction{
	@SpringBean("campaign.campaignDao")
	private CampaignDao campaignDao;

	@SpringBean("campaign.campaignGroupDao")
	private CampaignGroupDao campaignGroupDao;
	
	private Long offset = Long.valueOf(0);
    private Long limit = Long.valueOf(10);
	private Long groupId;
	
	@DefaultHandler
	public Resolution route() {
    	if (!campaignGroupDao.exists(groupId)) {
    		return new ErrorResolution(ErrorDef.InvalidGroupId);
    	}
		final Map<String, Object> results = new HashMap<String, Object>();
    	PageResult<Campaign> pageResult = campaignDao.findByGroupId(groupId, offset, limit);
    	List<CampaignWrapper> list = new ArrayList<CampaignWrapper>();
    	for (Campaign c : pageResult.getResults()) {
    		list.add(new CampaignWrapper(c));
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

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

}
