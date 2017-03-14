package com.cyberlink.cosmetic.modules.campaign.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.campaign.model.CampaignGroup;

public interface CampaignGroupDao extends GenericDao<CampaignGroup, Long>{
	PageResult<CampaignGroup> findByLocale(String locale, Long offset, Long limit);
	List<CampaignGroup> getAllCampaignGroupByGroupName(Long offset, Long limit);
	List<CampaignGroup> getCampaignGroupByGroupName(String gorupName);
	List<String> getAllCampaignGroupLocaleByGroupName(String groupName);
}
