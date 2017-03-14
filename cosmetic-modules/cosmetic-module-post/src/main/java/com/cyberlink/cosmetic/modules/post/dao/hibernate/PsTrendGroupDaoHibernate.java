package com.cyberlink.cosmetic.modules.post.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.cosmetic.modules.post.dao.PsTrendGroupDao;
import com.cyberlink.cosmetic.modules.post.model.PsTrendGroup;

public class PsTrendGroupDaoHibernate extends AbstractDaoCosmetic<PsTrendGroup, PsTrendGroup.PsTrendGroupKey>
    implements PsTrendGroupDao {

    private String regionOfFindAllGroupIds = "com.cyberlink.cosmetic.modules.post.model.PsTrendGroup.getAvailableId";
    private String regionOfFindByStep = "com.cyberlink.cosmetic.modules.post.model.PsTrendGroup.findByStep";
    
    @Override
    public Map<String, Long> getAvailableId() {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("id.locale", "en_US"));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.projectionList()
                .add(Projections.property("groups"))
                .add(Projections.property("id.gId")));
        List<Object> objs = findByCriteria(dc, regionOfFindAllGroupIds);
        Map<String, Long> result = new HashMap<String, Long>();
        for (Object obj : objs) {
            Object[] row = (Object[]) obj;
            result.put((String) row[0], (Long) row[1]);
        }
        return result;
    }
    
    @Override
    public List<PsTrendGroup> findByStep(Integer step) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("step", step));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return findByCriteria(dc, regionOfFindByStep);
    }
    
    @Override
    public void batchInsert(List<PsTrendGroup> list) {
        if (list == null || list.size() <= 0)
            return;
        Session session = getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            int i = 0;
            for (PsTrendGroup n : list) {
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
