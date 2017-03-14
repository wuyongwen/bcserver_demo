package com.cyberlink.cosmetic.modules.search.dao.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;
import com.cyberlink.cosmetic.modules.search.dao.PostKeywordDao;
import com.cyberlink.cosmetic.modules.search.model.PostKeyword;

public class PostKeywordDaoHibernate extends
		AbstractDaoCosmetic<PostKeyword, Long> implements PostKeywordDao {

	@Override
	public PostKeyword findByKeyword(String keyword, String lang) {
		DetachedCriteria d = createDetachedCriteria();
		d.add(Restrictions.eq("keyword", keyword));
		d.add(Restrictions.eq("lang", lang));
		return uniqueResult(d);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getKeywords(String lang, int topN) {
		String hql = "SELECT P.keyword FROM PostKeyword P WHERE P.lang = :lang ORDER BY P.freq DESC";
		Query query = getSession().createQuery(hql);
		query.setString("lang", lang);
		query.setMaxResults(topN);
		return query.list();
	}

	public Long getCircleTypeId(String local, String circleTypeName){
		String hql = "SELECT C.id FROM CircleType C WHERE C.locale = :locale AND C.circleTypeName = :name";
		Query query = getSession().createQuery(hql);
		query.setString("locale", local);
		query.setString("name", circleTypeName);
		query.setMaxResults(1);
		if(query.list().size()==0)
			return null;
		
		return (Long)query.list().get(0);
	}

}
