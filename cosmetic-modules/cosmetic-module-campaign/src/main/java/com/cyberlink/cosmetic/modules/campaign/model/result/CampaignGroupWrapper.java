package com.cyberlink.cosmetic.modules.campaign.model.result;

import java.util.ArrayList;
import java.util.List;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.campaign.model.Campaign;
import com.cyberlink.cosmetic.modules.campaign.model.CampaignGroup;
import com.fasterxml.jackson.annotation.JsonView;

public class CampaignGroupWrapper {
	CampaignGroup compaignGroup;
	public CampaignGroupWrapper(CampaignGroup compaignGroup)
    {
        this.compaignGroup = compaignGroup;
    }

	@JsonView(Views.Simple.class)
    public String getName() {
        return compaignGroup.getName();
    }
	
	@JsonView(Views.Simple.class)
    public Long getRotationPeriod() {
        return compaignGroup.getRotationPeriod();
    }
	
	@JsonView(Views.Simple.class)
    public Long getId() {
        return compaignGroup.getId();
    }
	
	@JsonView(Views.Simple.class)
    public List<CampaignWrapper> getCampaigns() {
		List<CampaignWrapper> list = new ArrayList<CampaignWrapper>();
        for (Campaign c : compaignGroup.getCampaigns()) {
        	list.add(new CampaignWrapper(c));
        }
        return list;
    }
}
