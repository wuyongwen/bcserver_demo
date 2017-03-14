package com.cyberlink.cosmetic.modules.campaign.dao.hibernate;

import java.util.LinkedList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.campaign.dao.CampaignGroupDao;
import com.cyberlink.cosmetic.modules.campaign.model.CampaignGroup;

public class CampaignGroupDaoHibernate extends AbstractDaoCosmetic<CampaignGroup, Long>
implements CampaignGroupDao{
	private String regionOfFindByLocale = "com.cyberlink.cosmetic.modules.campaign.model.CampaignGroup.query.findByLocale";
	
	@Override
	public PageResult<CampaignGroup> findByLocale(String locale, Long offset,
			Long limit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        if(locale != null)
        	dc.add(Restrictions.eq("locale", locale));
		return findByCriteria(dc, offset, limit, regionOfFindByLocale);
	}
	
	@Override
	public List<CampaignGroup> getAllCampaignGroupByGroupName(Long offset, Long limit){
		List<CampaignGroup> campaignList = new LinkedList<CampaignGroup>();
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.projectionList().add(Projections.distinct(Projections.property("name"))).add(Projections.property("rotationPeriod")));
        List<Object> objectList = findByCriteria(dc, offset, limit , null).getResults();
        if(objectList.size() <= 0)
        	return campaignList;
        for(Object obj : objectList) {
            Object[] row = (Object[]) obj;
        	CampaignGroup campaignGroup = new CampaignGroup();
        	campaignGroup.setName((row[0]==null)?null:String.valueOf(row[0].toString()));
        	campaignGroup.setRotationPeriod((row[1]==null)?null:Long.valueOf(row[1].toString()));
        	campaignList.add(campaignGroup);
        }
		return campaignList;
	}
	
	@Override
	public List<String> getAllCampaignGroupLocaleByGroupName(String groupName){
		List<String> campaignGroupLocaleList = new LinkedList<String>();
        List<CampaignGroup> campaignGroupList = getCampaignGroupByGroupName(groupName);
        if(campaignGroupList.isEmpty())
        	return campaignGroupLocaleList;
        for(CampaignGroup campaignGroup : campaignGroupList){
        	campaignGroupLocaleList.add(campaignGroup.getLocale());
        }
        return campaignGroupLocaleList;
	}
	
	@Override
	public List<CampaignGroup> getCampaignGroupByGroupName(String groupName){
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("name", groupName));
		return findByCriteria(dc);
	}
}
