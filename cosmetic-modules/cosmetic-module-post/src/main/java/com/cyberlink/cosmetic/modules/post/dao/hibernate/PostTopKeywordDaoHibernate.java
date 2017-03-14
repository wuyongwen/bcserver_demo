package com.cyberlink.cosmetic.modules.post.dao.hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.dao.PostTopKeywordDao;
import com.cyberlink.cosmetic.modules.post.model.PostTopKeyword;

public class PostTopKeywordDaoHibernate extends AbstractDaoCosmetic<PostTopKeyword, Long>
    implements PostTopKeywordDao {

	private String regionOfGetPopularKeywords = "com.cyberlink.cosmetic.modules.post.model.PostTopKeyword.getPopularKeywords";
	
	@Override
	public void updateKeywordsFreq(String locale, Set<String> keywords, Integer kwBucketId)	{
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("locale", locale));
		dc.add(Restrictions.in("keyword", keywords));
		dc.add(Restrictions.eq("isTop", Boolean.FALSE));
		dc.add(Restrictions.eq("bucketId", kwBucketId));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.setProjection(Projections.projectionList()
        	.add(Projections.property("keyword")));
		List<String> lists = findByCriteria(dc);
		
		Set<String> createKwSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		createKwSet.addAll(keywords);
		for(String list : lists) {
			if(createKwSet.contains(list))
				createKwSet.remove(list);
		}
		
		List<PostTopKeyword> createLists = new ArrayList<PostTopKeyword>();
		for(String kw: createKwSet) {
			PostTopKeyword exKeyword = new PostTopKeyword();
			exKeyword.setLocale(locale);
			exKeyword.setKeyword(kw);
			exKeyword.setFrequency(1L);
			exKeyword.setIsTop(false);
			exKeyword.setBucketId(kwBucketId);
			createLists.add(exKeyword);
		}
		batchInsert(createLists);

		if(lists.size() <= 0)
			return;
		String updateSqlCmd = "UPDATE BC_POST_TOP_KW SET FREQUENCY = FREQUENCY + 1 WHERE LOCALE = :locale AND BUCKET_ID = :bucketId AND KEYWORD IN ( :keywords ) "
							+ "AND IS_TOP = 0 AND IS_DELETED = 0";
		SQLQuery sqlQuery = getSession().createSQLQuery(updateSqlCmd);
		sqlQuery.setParameter("locale", locale);
		sqlQuery.setParameter("bucketId", kwBucketId);
		sqlQuery.setParameterList("keywords", lists);
		sqlQuery.executeUpdate();
	}

	@Override
	public PageResult<PostTopKeyword> getPopularKeywords(String locale, BlockLimit blockLimit, Boolean isTop, Integer kwBucketId, List<String> exCircleName) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("locale", locale));
		dc.add(Restrictions.eq("isTop", isTop));
		dc.add(Restrictions.eq("bucketId", kwBucketId));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		if (exCircleName != null && exCircleName.size() > 0)
			dc.add(Restrictions.not(Restrictions.in("keyword", exCircleName)));
		dc.addOrder(Order.desc("frequency"));
		return blockQuery(dc, blockLimit, regionOfGetPopularKeywords);
	}

	@Override
	public void deleteOldRecord(Integer kwBucketId) {
		String updateSqlCmd = "DELETE FROM BC_POST_TOP_KW WHERE BUCKET_ID = :bucketId ";
		SQLQuery sqlQuery = getSession().createSQLQuery(updateSqlCmd);
		sqlQuery.setParameter("bucketId", kwBucketId);
		sqlQuery.executeUpdate();
	}
	
	@Override
	public void batchInsert(List<PostTopKeyword> list) {
		if (list == null || list.size() <= 0)
			return;
		Session session = getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		try {
			int i = 0;
			for (PostTopKeyword n : list) {
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
