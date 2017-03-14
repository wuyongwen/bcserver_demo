package com.cyberlink.cosmetic.modules.product.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.model.DeeplinkLog;


public interface DeeplinkLogDao extends GenericDao<DeeplinkLog, Long>{

	
	List<String> findDistinctByDeviceOS();
	List<String> findDistinctByDeviceBrowser();
	List<String> findDistinctByDeviceAPP();
	List<String> findDistinctByReferrer();
	List<String> findDistinctByFry();
	
	PageResult<DeeplinkLog> findDeeplinkLogByParameters(String deviceOS , String deviceBrowser,
			String deviceApp, String referrer,String fry, Long offset, Long limit);
}