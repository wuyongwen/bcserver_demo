package com.cyberlink.cosmetic.modules.event.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.event.dao.BeautyInsightDao;
import com.cyberlink.cosmetic.modules.event.model.BeautyInsight;

public class BeautyInsightDaoHibernate extends AbstractDaoCosmetic<BeautyInsight, Long> implements BeautyInsightDao {

	@Override
	public PageResult<BeautyInsight> listAllBeautyInsight(Long offset, Long limit) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		return findByCriteria(dc, offset, limit, null);
	}

	@Override
	public PageResult<BeautyInsight> listBeautyInsightByLocale(String locale, Long offset, Long limit) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("locale", locale));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.addOrder(Order.desc("createdTime"));
		return findByCriteria(dc, offset, limit, null);
	}

	@Override
	public List<BeautyInsight> listAllBeautyInsightByLocale(String locale) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("locale", locale));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.addOrder(Order.desc("createdTime"));
		return findByCriteria(dc);
	}

}
