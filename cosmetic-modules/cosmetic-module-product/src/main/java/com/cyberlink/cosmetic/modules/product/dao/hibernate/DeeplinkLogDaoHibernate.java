package com.cyberlink.cosmetic.modules.product.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.dao.DeeplinkLogDao;
import com.cyberlink.cosmetic.modules.product.model.DeeplinkLog;

public class DeeplinkLogDaoHibernate extends AbstractDaoCosmetic<DeeplinkLog, Long>
implements DeeplinkLogDao{

	
	/**
	 * return List<String> String is DeviceOSName
	 */
	@Override
	public List<String> findDistinctByDeviceOS() {
		//"SELECT DISTINCT DEVICE_OS FROM BC_DEEPLINK_LOG";
		DetachedCriteria dc = createDetachedCriteria();
        dc.setProjection(Projections.distinct(Projections.property("platform")));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return findByCriteria(dc);
	}
	
	/**
	 * return List<String> String is DeviceBrowserName
	 */
	@Override
	public List<String> findDistinctByDeviceBrowser() {
		//"SELECT DISTINCT DEVICE_BROWSER FROM BC_DEEPLINK_LOG";
		DetachedCriteria dc = createDetachedCriteria();
        dc.setProjection(Projections.distinct(Projections.property("browser")));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return findByCriteria(dc);
	}

	/**
	 * return List<String> String is DeviceAPPName
	 */
	@Override
	public List<String> findDistinctByDeviceAPP() {
		//"SELECT DISTINCT DEVICE_APP FROM BC_DEEPLINK_LOG";
		DetachedCriteria dc = createDetachedCriteria();
        dc.setProjection(Projections.distinct(Projections.property("appName")));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return findByCriteria(dc);
	}

	/**
	 * return List<String> String is Referrer
	 */
	@Override
	public List<String> findDistinctByReferrer() {
		//"SELECT DISTINCT REFERRER FROM BC_DEEPLINK_LOG";
		DetachedCriteria dc = createDetachedCriteria();
        dc.setProjection(Projections.distinct(Projections.property("referrer")));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return findByCriteria(dc);
	}
	

	/**
	 * return List<String> String is Fry
	 */
	@Override
	public List<String> findDistinctByFry() {
		//"SELECT DISTINCT FRY FROM BC_DEEPLINK_LOG";
		DetachedCriteria dc = createDetachedCriteria();
        dc.setProjection(Projections.distinct(Projections.property("fry")));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return findByCriteria(dc);
	}
	
	/**
	 * Find deeplinkLogData By OS, browser , appName ,and referrer
	 */
	@Override
	public PageResult<DeeplinkLog> findDeeplinkLogByParameters(String deviceOS, String deviceBrowser,
			String deviceApp, String referrer, String fry, Long offset, Long limit) {
        
		DetachedCriteria dc = createDetachedCriteria();
		
    	if (deviceOS == null)
    	{}
    	else if(deviceOS.equals("NULL"))
    		dc.add(Restrictions.isNull("platform"));
    	else
    		dc.add(Restrictions.eq("platform", deviceOS));
		
    	if (deviceBrowser == null)
    	{}
    	else if(deviceBrowser.equals("NULL"))
    		dc.add(Restrictions.isNull("browser"));
    	else
    		dc.add(Restrictions.eq("browser", deviceBrowser));
    	
    	if (deviceApp == null){}
    	else if(deviceApp.equals("NULL"))
    		dc.add(Restrictions.isNull("appName"));
    	else
    		dc.add(Restrictions.eq("appName", deviceApp));
    	
    	if (referrer == null){}
    	else if(referrer.equals("NULL"))
    		dc.add(Restrictions.isNull("referrer"));
    	else
    		dc.add(Restrictions.eq("referrer", referrer));
    	
    	if (fry == null){}
    	else if(fry.equals("NULL"))
    		dc.add(Restrictions.isNull("fry"));
    	else
    		dc.add(Restrictions.eq("fry", fry));
    	
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        
		return findByCriteria(dc, offset, limit, null);
	}
}