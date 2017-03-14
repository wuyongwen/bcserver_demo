package com.cyberlink.cosmetic.modules.user.dao.hibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.user.dao.DeviceDao;
import com.cyberlink.cosmetic.modules.user.model.Device;
import com.cyberlink.cosmetic.modules.user.model.DeviceType;

public class DeviceDaoHibernate extends AbstractDaoCosmetic<Device, Long> implements DeviceDao{

	@Override
	public Device findDeviceInfo(Long userId, String uuid, DeviceType deviceType, String app) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("userId", userId));
		dc.add(Restrictions.eq("deviceType", deviceType));
		dc.add(Restrictions.eq("uuid", uuid));
		if(app != null)
		    dc.add(Restrictions.eq("app", app));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		PageResult<Device> pgResult = findByCriteriaWithoutCount(dc, 0L, 1L, null);
		if(pgResult.getResults() != null && pgResult.getResults().size() > 0)
		    return pgResult.getResults().get(0);
		return null;
	}
	
	@Override
	public Device findDeviceInfo(String uuid) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("uuid", uuid));
		PageResult<Device> pgResult = findByCriteriaWithoutCount(dc, 0L, 1L, null);
		if(pgResult.getResults() != null && pgResult.getResults().size() > 0)
		    return pgResult.getResults().get(0);
		return null;
	}

	@Override
	public List<String> findByUserId(Long userId) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("userId", userId));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.neOrIsNotNull("uuid", ""));
        dc.setProjection(Projections.property("uuid"));
        return findByCriteria(dc);
	}
	
	@Override
	public List<String> findDistinctByUserId(Long userId) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("userId", userId));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.neOrIsNotNull("uuid", ""));
        dc.setProjection(Projections.distinct(Projections.property("uuid")));
        return findByCriteria(dc);
	}
	
	@Override
	public PageResult<String> findDeviceUuidByUserId(Long userId, Long offset, Long limit) {
        PageResult<String> page = new PageResult<String>();
		DetachedCriteria dc = createDetachedCriteria();
        if (userId != null)
        	dc.add(Restrictions.eq("userId", userId));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.neOrIsNotNull("uuid", ""));
        dc.setProjection(Projections.groupProperty("uuid"));
        
        List<String> list = findByCriteria(dc);
        if (offset >= list.size()) {
        	page.setResults(new ArrayList<String>());
        } else {
        	page.setResults(list.subList(offset.intValue(), Long.valueOf(Math.min(list.size(), offset+limit)).intValue()));
        }
        page.setTotalSize(list.size());
        return page;
	}
	
	@Override
	public Map<Long, Device> findNotifyDeviceByUserIds(Set<Long> userIds) {
		Map<Long, Device> resultMap = new HashMap<Long, Device>();
		if (userIds == null ||userIds.size() == 0)
			return resultMap;
		DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("userId", userIds));
        dc.add(Restrictions.isNotNull("apnsToken"));
        dc.add(Restrictions.eq("app", "YBC"));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.addOrder(Order.asc("lastModified"));
        Projections.distinct(Projections.property("userId"));
        List<Device> objs = findByCriteria(dc);
        for (Device obj : objs) {
            resultMap.put(obj.getUserId(), obj);
        }
        return resultMap;
	}
}
