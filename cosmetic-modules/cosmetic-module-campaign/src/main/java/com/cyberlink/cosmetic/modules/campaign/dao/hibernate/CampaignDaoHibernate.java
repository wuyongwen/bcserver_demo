package com.cyberlink.cosmetic.modules.campaign.dao.hibernate;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.campaign.dao.CampaignDao;
import com.cyberlink.cosmetic.modules.campaign.model.Campaign;

public class CampaignDaoHibernate extends AbstractDaoCosmetic<Campaign, Long>
implements CampaignDao {
	private String regionOfFindByGroupId = "com.cyberlink.cosmetic.modules.campaign.model.Campaign.query.findByGroupId";

	@Override
	public PageResult<Campaign> findByGroupId(Long groupId, Long offset,
			Long limit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("group.id", groupId));
		return findByCriteria(dc, offset, limit, regionOfFindByGroupId);
	}

}
