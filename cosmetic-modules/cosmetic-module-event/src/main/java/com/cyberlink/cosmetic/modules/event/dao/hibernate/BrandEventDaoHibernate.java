package com.cyberlink.cosmetic.modules.event.dao.hibernate;


import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
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
import com.cyberlink.cosmetic.modules.event.dao.BrandEventDao;
import com.cyberlink.cosmetic.modules.event.model.BrandEvent;
import com.cyberlink.cosmetic.modules.event.model.EventType;
import com.cyberlink.cosmetic.modules.event.model.EventUser;
import com.cyberlink.cosmetic.modules.event.model.ServiceType;

public class BrandEventDaoHibernate extends AbstractDaoCosmetic<BrandEvent, Long> 
implements BrandEventDao{

	@Override
	public List<BrandEvent> listBrandEvent(String locale, ServiceType serviceType) {
		DetachedCriteria dc = createDetachedCriteria();
		if (locale != null)
			dc.add(Restrictions.eq("locale", locale));
		if (serviceType != null)
			dc.add(Restrictions.eq("serviceType", serviceType));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));   
        dc.addOrder(Order.desc("priority"));
        return findByCriteria(dc);
	}
	
	@Override
	public PageResult<BrandEvent> findBrandEventByType(String locale, List<ServiceType> serviceTypes, List<EventType> eventTypes, BlockLimit blockLimit) {
		DetachedCriteria dc = createDetachedCriteria();
		if (locale != null)
			dc.add(Restrictions.eq("locale", locale));
		if (serviceTypes != null && !serviceTypes.isEmpty())
			dc.add(Restrictions.in("serviceType", serviceTypes));
		if (eventTypes != null && !eventTypes.isEmpty())
			dc.add(Restrictions.in("eventType", eventTypes));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));   
        return blockQuery(dc, blockLimit);
	}

    @Override
    public PageResult<Pair<Long, BrandEvent>> findByEventIds(Long userId, Set<Long> eventIds, BlockLimit blockLimit) {
        PageResult<Pair<Long, BrandEvent>> result = new PageResult<Pair<Long, BrandEvent>>();
        if(eventIds == null || eventIds.size() <= 0)
            return result;
        
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("brandId", userId));
        dc.add(Restrictions.in("id", eventIds));
        PageResult<BrandEvent> pgResult = blockQuery(dc, blockLimit);
        result.setTotalSize(pgResult.getTotalSize());
        for(BrandEvent be : pgResult.getResults()) {
            result.add(Pair.of((Long)be.getId(), (BrandEvent)be));
        }
        return result;
    }

    @Override
    public Boolean batchCreate(List<BrandEvent> list) {
		Session session = getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		try {
			int i = 0;
			for (BrandEvent be : list) {
				session.save(be);
				i++;
				if (i % 50 == 0) {
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
	        return true;
		} catch (RuntimeException e) {
			try {
				tx.rollback();
			} catch (RuntimeException rbe) {

			}
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
		return false;
    }
    
    @Override
    public int bacthDeleteByEventIds(Set<Long> eventIdSet) {
        if(eventIdSet == null || eventIdSet.size() <= 0)
            return 0;
        String updatePostSqlCmd = "UPDATE BC_BRAND_EVENT SET IS_DELETED=1 WHERE ID IN (:eventIdSet)";
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(updatePostSqlCmd);
        sqlPostsQuery.setParameterList("eventIdSet", eventIdSet);
        return sqlPostsQuery.executeUpdate();
    }

	@Override
	public BrandEvent findByBrandEventId(Long id) {
		DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("id", id));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return uniqueResult(dc);
	}
	
	@Override
	public List<Object> findImageSetListByBrandId(Long brandId) {
		DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("brandId", brandId));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("serviceType", ServiceType.FREE_SAMPLE));
        dc.setProjection(Projections.projectionList()
        		.add(Projections.property("id"))
        		.add(Projections.property("imageUrl"))
        		.add(Projections.property("eventLink")));
        return findByCriteria(dc);
	}
}
