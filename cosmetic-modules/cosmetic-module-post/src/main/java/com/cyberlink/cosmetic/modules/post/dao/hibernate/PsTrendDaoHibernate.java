package com.cyberlink.cosmetic.modules.post.dao.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.dao.PsTrendDao;
import com.cyberlink.cosmetic.modules.post.model.PsTrend;
import com.cyberlink.cosmetic.modules.post.model.PsTrend.PsTrendKey;

public class PsTrendDaoHibernate extends AbstractDaoCosmetic<PsTrend, PsTrend.PsTrendKey>
    implements PsTrendDao {

    private String regionOfFindPsTrendByGroup = "com.cyberlink.cosmetic.modules.post.model.PsTrend.listPostByGroup";
    
    @Override
    public Integer listPostByGroup(String locale, Long groups, List<Long> result, BlockLimit blockLimit) {
        if(result == null)
            return 0;
        
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("id.locale", locale));
        dc.add(Restrictions.eq("id.groups", groups));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.property("id.pid"));
        blockLimit.getOrderBy().clear();
        blockLimit.addOrderBy("promoteScore", false);
        blockLimit.addOrderBy("displayTime", false);
        PageResult<Long> tmpResult = blockQueryWithoutSize(dc, blockLimit, regionOfFindPsTrendByGroup);
        result.addAll(tmpResult.getResults());
        return Integer.MAX_VALUE;
    }

    @Override
    public List<PsTrend> findByIds(List<PsTrendKey> ids) {
        if(ids == null || ids.size() <= 0)
            return new ArrayList<PsTrend>();
        
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("id", ids));
        return findByCriteria(dc);
    }
    
    @Override
    public void batchInsert(List<PsTrend> list) {
        if (list == null || list.size() <= 0)
            return;
        Session session = getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            int i = 0;
            for (PsTrend n : list) {
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
    
	@Override
	public int deleteByPost(Long pId, String locale, Collection<Long> groups) {
		String updatePostSqlCmd = "UPDATE BC_PS_TREND SET IS_DELETED = 1 WHERE ID = :pId AND LOCALE = :locale AND GROUPS IN ( :groups )";
		SQLQuery sqlPostsQuery = getSession().createSQLQuery(updatePostSqlCmd);
		sqlPostsQuery.setParameter("pId", pId);
		sqlPostsQuery.setParameter("locale", locale);
		sqlPostsQuery.setParameterList("groups", groups);
		return sqlPostsQuery.executeUpdate();
	}
	
}
