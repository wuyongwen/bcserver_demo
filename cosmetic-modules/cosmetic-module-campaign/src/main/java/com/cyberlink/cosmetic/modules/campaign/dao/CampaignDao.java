package com.cyberlink.cosmetic.modules.campaign.dao;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.campaign.model.Campaign;

public interface CampaignDao extends GenericDao<Campaign, Long>{
	PageResult<Campaign> findByGroupId(Long groupId, Long offset, Long limit);

}
