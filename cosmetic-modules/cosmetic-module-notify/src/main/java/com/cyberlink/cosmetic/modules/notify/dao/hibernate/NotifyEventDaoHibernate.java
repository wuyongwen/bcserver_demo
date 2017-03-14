package com.cyberlink.cosmetic.modules.notify.dao.hibernate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.cosmetic.modules.notify.dao.NotifyEventDao;
import com.cyberlink.cosmetic.modules.notify.model.NotifyEvent;
import com.cyberlink.cosmetic.modules.notify.model.NotifyType;

public class NotifyEventDaoHibernate extends AbstractDaoCosmetic<NotifyEvent, Long> implements NotifyEventDao {
	
	@Override
	public Map<Long, Long> getGroupMap(String notifyType, PageLimit limit, Date checkTime) {
		Map<Long, Long> resultMap = new HashMap<Long, Long>();
		if (notifyType == null || NotifyType.CommentPost.equals(notifyType) || limit == null)
			return resultMap;
        
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("notifyType", notifyType));
        dc.add(Restrictions.le("createdTime", checkTime));
        
		// Group by sender
		if (NotifyType.getSenderGroupType().contains(notifyType)) {
			dc.setProjection(Projections.projectionList()
                    .add(Projections.groupProperty("senderId"))
                    .add(Projections.count("id")));  
		} // Group by item
		else if (NotifyType.getNonSenderGroupType().contains(notifyType)) {
			dc.setProjection(Projections.projectionList()
                    .add(Projections.groupProperty("refId"))
                    .add(Projections.count("id")));
		} // Group by receiver
		else if (NotifyType.getFollowType().contains(notifyType)) {
			dc.setProjection(Projections.projectionList()
                    .add(Projections.groupProperty("receiverId"))
                    .add(Projections.count("id")));
		}
        
        List<Object> objs = findByCriteria(dc, limit);
        for (Object obj : objs) {
            Object[] row = (Object[]) obj;
            resultMap.put((Long) row[0], (Long) row[1]);
        }
        return resultMap;
	}
	
	@Override
	public List<NotifyEvent> listByGroupId(String notifyType, Long groupId, PageLimit limit, Date checkTime) {
		if (notifyType == null || NotifyType.CommentPost.equals(notifyType) || limit == null)
			return null;
		
		DetachedCriteria dc = createDetachedCriteria();		
		// Group by sender
		if (NotifyType.getSenderGroupType().contains(notifyType)) {
			dc.add(Restrictions.eq("senderId", groupId));
		} // Group by item
		else if (NotifyType.getNonSenderGroupType().contains(notifyType)) {
			dc.add(Restrictions.eq("refId", groupId));
		} // Group by receiver
		else if (NotifyType.getFollowType().contains(notifyType)) {
			dc.add(Restrictions.eq("receiverId", groupId));
		}
		dc.add(Restrictions.eq("notifyType", notifyType));
		dc.add(Restrictions.le("createdTime", checkTime));
		dc.addOrder(Order.desc("createdTime"));
		
		return findByCriteria(dc, limit);
	}
}