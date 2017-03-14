package com.cyberlink.cosmetic.modules.post.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.dao.HoroscopeDao;
import com.cyberlink.cosmetic.modules.post.model.Horoscope;

public class HoroscopeDaoHibernate extends AbstractDaoCosmetic<Horoscope, Long>
    implements HoroscopeDao {

	public List<Horoscope> listByLocale(String locale) {
		DetachedCriteria dc = createDetachedCriteria();
		if (locale != null)
			dc.add(Restrictions.eq("locale", locale));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.addOrder(Order.desc("createdTime"));
		return findByCriteria(dc);
	}
	
	public PageResult<Horoscope> listByLocale(String locale, BlockLimit blockLimit) {
		DetachedCriteria dc = createDetachedCriteria();
		if (locale != null)
			dc.add(Restrictions.eq("locale", locale));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.addOrder(Order.desc("createdTime"));
		return blockQuery(dc, blockLimit);
	}
}
