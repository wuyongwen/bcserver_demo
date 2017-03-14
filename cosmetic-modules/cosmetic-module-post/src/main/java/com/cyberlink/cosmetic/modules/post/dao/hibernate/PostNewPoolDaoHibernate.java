package com.cyberlink.cosmetic.modules.post.dao.hibernate;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.dao.PostNewPoolDao;
import com.cyberlink.cosmetic.modules.post.model.PostNewPool;
import com.cyberlink.cosmetic.modules.post.model.PostNewPool.NewPoolGroup;

public class PostNewPoolDaoHibernate extends AbstractDaoCosmetic<PostNewPool, Long>
    implements PostNewPoolDao {
    
    @Override
    public Long getPostCountInPool(String locale, List<NewPoolGroup> groups) {
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.eq("locale", locale));
        if(groups != null && groups.size() > 0)
            d.add(Restrictions.in("group", groups));
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        d.setProjection(Projections.rowCount());
        return uniqueResult(d);
    }

    @Override
    public Map<NewPoolGroup, Long> getPostCountInPoolPerGroup(String locale, List<NewPoolGroup> groups) {
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.eq("locale", locale));
        if(groups != null && groups.size() > 0)
            d.add(Restrictions.in("group", groups));
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        d.setProjection(Projections.projectionList()
                .add(Projections.groupProperty("group"))
                .add(Projections.rowCount()));
        List<Object> objs = findByCriteria(d);
        Map<NewPoolGroup, Long> result = new LinkedHashMap<NewPoolGroup, Long>();
        for(Object obj : objs) {
            Object[] row = (Object[]) obj;
            result.put((NewPoolGroup) row[0], (Long) row[1]);
        }
        return result;
    }
    
    @Override
    public PageResult<PostNewPool> getPostFromPool(String locale, List<NewPoolGroup> groups,
            BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", false));
        if(groups != null && groups.size() > 0)
            dc.add(Restrictions.in("group", groups));
        dc.add(Restrictions.eq("locale", locale));
        return blockQuery(dc, blockLimit);
    }

    @Override
    public Boolean batchCreate(List<PostNewPool> list) {
        if(list == null || list.size() <= 0)
            return true;
        Session session = getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        int i = 0;
        for (PostNewPool toCreate : list) {
            session.save(toCreate);
            i++;
            if ( i % 50 == 0 ) {
                session.flush();
                session.clear();
            }       
            if (i % 200 == 0) {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    return false;
                }                           
            }
        }       
        tx.commit();
        session.close();
        return true;
    }
    
    @Override
    public int batchSetDelete(List<Long> toDeleteIds) {
        if(toDeleteIds == null || toDeleteIds.size() <= 0)
            return 0;
        
        String updatePostSqlCmd = "UPDATE BC_POST_NEW_POOL SET IS_DELETED=1 WHERE ID IN (:toDeleteIds)";
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(updatePostSqlCmd);
        sqlPostsQuery.setParameterList("toDeleteIds", toDeleteIds);
        return sqlPostsQuery.executeUpdate();
    }
    
    @Override
    public int batchRealDelete(Date startDate, Date endDate) {
        String criteria = "WHERE IS_DELETED=1 ";
        if(startDate != null)
            criteria += "AND CREATED_TIME >= :startDate ";
        
        if(endDate != null)
            criteria += "AND CREATED_TIME < :endDate ";
        
        String updatePostSqlCmd = "DELETE FROM BC_POST_NEW_POOL " + criteria;        
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(updatePostSqlCmd);
        if(startDate != null)
            sqlPostsQuery.setParameter("startDate", startDate);
        if(endDate != null)
            sqlPostsQuery.setParameter("endDate", endDate);
        return sqlPostsQuery.executeUpdate();
    }
}
