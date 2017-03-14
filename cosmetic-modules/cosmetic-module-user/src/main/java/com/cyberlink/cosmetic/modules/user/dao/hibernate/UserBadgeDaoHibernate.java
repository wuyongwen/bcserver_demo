package com.cyberlink.cosmetic.modules.user.dao.hibernate;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.user.dao.UserBadgeDao;
import com.cyberlink.cosmetic.modules.user.model.UserBadge;
import com.cyberlink.cosmetic.modules.user.model.UserBadge.BadgeType;

public class UserBadgeDaoHibernate extends AbstractDaoCosmetic<UserBadge, Long> implements UserBadgeDao {

	@Override
	public PageResult<UserBadge> listUsersByBadgeType(List<String> locale, BadgeType badgeType, BlockLimit blockLimit) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.in("locale", locale));
		dc.add(Restrictions.eq("badgeType", badgeType));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		return blockQuery(dc, blockLimit);
	}
	
	@Override
	public UserBadge findUserBadgeByUserId(String locale, Long userId, Boolean isStar) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("locale", locale));
		dc.add(Restrictions.eq("userId", userId));
		if(isStar)
			dc.add(Restrictions.eq("badgeType", BadgeType.StarOfWeek));
		else
			dc.add(Restrictions.ne("badgeType", BadgeType.StarOfWeek));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		return uniqueResult(dc);
	}
	
	@Override
	public List<UserBadge> findStarOfWeekByLocale(String locale) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("locale", locale));
		dc.add(Restrictions.eq("badgeType", BadgeType.StarOfWeek));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		return findByCriteria(dc);
	}
	
	@Override
	public Long findStarOfWeekByUserId(Long userId) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("userId", userId));
		dc.add(Restrictions.eq("badgeType", BadgeType.StarOfWeek));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.setProjection(Projections.rowCount());
		return uniqueResult(dc);
	}
	
	@Override
	public void batchInsert(List<UserBadge> list) {
		if (list == null || list.size() <= 0)
			return;
		Session session = getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		try {
			int i = 0;
			for (UserBadge n : list) {
				session.save(n);
				i++;
				if (i % 50 == 0) {
					session.flush();
					session.clear();
				}
				if (i % 200 == 0) {
					try {
						Thread.sleep(500);
					} catch (Exception e) {
					}
				}
			}
			tx.commit();
		} catch (RuntimeException e) {
			try {
				tx.rollback();
			} catch (RuntimeException rbe) {
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}
	
}
