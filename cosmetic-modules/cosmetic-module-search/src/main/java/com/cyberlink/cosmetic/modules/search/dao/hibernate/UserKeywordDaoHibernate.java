package com.cyberlink.cosmetic.modules.search.dao.hibernate;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.cosmetic.modules.search.dao.UserKeywordDao;
import com.cyberlink.cosmetic.modules.search.model.TypeKeyword;
import com.cyberlink.cosmetic.modules.search.model.UserKeyword;

public class UserKeywordDaoHibernate extends
		AbstractDaoCosmetic<UserKeyword, Long> implements UserKeywordDao {

	public UserKeyword findByUserIdAndType(long userId, TypeKeyword type) {
		DetachedCriteria d = createDetachedCriteria();
		d.add(Restrictions.eq("userId", userId));
		d.add(Restrictions.eq("type", type));
		return uniqueResult(d);
	}

	public void removeKeyword(long userId, TypeKeyword type) {
		String hql = "delete from UserKeyword where userId = :userId and type = :type";
		Query query = getSession().createQuery(hql);
		query.setLong("userId", userId);
		query.setString("type", type.toString());
		query.executeUpdate();
	}
}
