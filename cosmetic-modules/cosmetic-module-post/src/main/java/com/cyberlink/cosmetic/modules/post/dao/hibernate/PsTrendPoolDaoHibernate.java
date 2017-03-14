package com.cyberlink.cosmetic.modules.post.dao.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.dao.PsTrendPoolDao;
import com.cyberlink.cosmetic.modules.post.model.PsTrendPool;
import com.cyberlink.cosmetic.modules.post.model.PsTrendPool.PsTrendPoolKey;

public class PsTrendPoolDaoHibernate extends AbstractDaoCosmetic<PsTrendPool, PsTrendPool.PsTrendPoolKey>
    implements PsTrendPoolDao {
    
    @Override
    public PageResult<PsTrendPool> findByCircleType(Integer bucket, Long circleTypeId, Date from, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("id.bucket", bucket));
        dc.add(Restrictions.eq("id.circleTypeId", circleTypeId));
        dc.add(Restrictions.gt("displayTime", from));
        blockLimit.getOrderBy().clear();
        blockLimit.addOrderBy("displayTime", true);
        return blockQueryWithoutSize(dc, blockLimit);
    }

    @Override
    public List<PsTrendPool> findByIds(List<PsTrendPoolKey> ids) {
        if(ids == null || ids.size() <= 0)
            return new ArrayList<PsTrendPool>();
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("id", ids));
        return findByCriteria(dc);
    }
    
    @Override
    public void batchInsert(List<PsTrendPool> list) {
        if (list == null || list.size() <= 0)
            return;
        Session session = getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            int i = 0;
            for (PsTrendPool n : list) {
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
    public Integer batchDelete(Integer bucket) {
        String updateSqlCmd = "DELETE FROM BC_PS_TREND_POOL WHERE BUCKET = :bucket";
        SQLQuery sqlQuery = getSession().createSQLQuery(updateSqlCmd);
        sqlQuery.setParameter("bucket", bucket);
        return sqlQuery.executeUpdate();
    }
    
	@Override
	public int deleteByPost(Long pId, List<Long> circleTypeIds) {
        String updatePostSqlCmd = "UPDATE BC_PS_TREND_POOL SET IS_DELETED = 1 WHERE ID = :pId AND CIRCLE_TYPE_ID IN ( :circleTypeId )";
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(updatePostSqlCmd);
        sqlPostsQuery.setParameter("pId", pId);
        sqlPostsQuery.setParameterList("circleTypeId", circleTypeIds);
        return sqlPostsQuery.executeUpdate();
	}

}
