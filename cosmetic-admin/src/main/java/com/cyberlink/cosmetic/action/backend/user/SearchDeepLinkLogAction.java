package com.cyberlink.cosmetic.action.backend.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.product.dao.DeeplinkLogDao;
import com.cyberlink.cosmetic.modules.product.model.DeeplinkLog;
import com.cyberlink.cosmetic.modules.user.model.User;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/user/searchDeepLinkLog.action")
public class SearchDeepLinkLogAction extends AbstractAction {
	
	@SpringBean("product.DeeplinkLogDao")
    private DeeplinkLogDao deeplinkLogDao;
	
	//private static final String PageRoute = "/user/autoCreateUser-route.jsp";

    private Boolean isSearch = Boolean.FALSE;
    private String searchDeviceOS;
	private String searchDeviceBrowser;
	private String searchDeviceApp;
	private String searchReferrer;
	private String searchFry;

	private List<String> deviceOSList;
	private List<String> deviceBrowserList;
	private List<String> deviceAppList;
	private List<String> referrerList;
	private List<String> fryList;
	
	private PageResult<DeeplinkLog> pageResult;
	
	//Getter and setter
	public Boolean getIsSearch() {
		return isSearch;
	}
	public void setIsSearch(Boolean isSearch) {
		this.isSearch = isSearch;
	}
	public String getSearchDeviceOS() {
		return searchDeviceOS;
	}
	public void setSearchDeviceOS(String searchDeviceOS) {
		this.searchDeviceOS = searchDeviceOS;
	}
	public String getSearchDeviceBrowser() {
		return searchDeviceBrowser;
	}
	public void setSearchDeviceBrowser(String searchDeviceBrowser) {
		this.searchDeviceBrowser = searchDeviceBrowser;
	}
	public String getSearchDeviceApp() {
		return searchDeviceApp;
	}
	public void setSearchDeviceApp(String searchDeviceApp) {
		this.searchDeviceApp = searchDeviceApp;
	}
	public String getSearchReferrer() {
		return searchReferrer;
	}
	public void setSearchReferrer(String searchReferrer) {
		this.searchReferrer = searchReferrer;
	}
	public String getSearchFry() {
		return searchFry;
	}
	public void setSearchFry(String searchFry) {
		this.searchFry = searchFry;
	}
	
	public List<String> getDeviceOSList() {
		if (deviceOSList == null) {
			deviceOSList = new ArrayList<String>(deeplinkLogDao.findDistinctByDeviceOS());
			removeNullAndSetNullString(deviceOSList);
			Collections.reverse(deviceOSList);
		}
		return deviceOSList;
	}
	public void setDeviceOSList(List<String> deviceOSList) {
		this.deviceOSList = deviceOSList;
	}

	public List<String> getDeviceBrowserList() {
		if (deviceBrowserList == null) {
			deviceBrowserList = new ArrayList<String>(deeplinkLogDao.findDistinctByDeviceBrowser());
			removeNullAndSetNullString(deviceBrowserList);
			Collections.reverse(deviceBrowserList);
		}
		return deviceBrowserList;
	}
	public void setDeviceBrowserList(List<String> deviceBrowserList) {
		this.deviceBrowserList = deviceBrowserList;
	}
	
	public List<String> getDeviceAppList() {
		if (deviceAppList == null) {
			deviceAppList = new ArrayList<String>(deeplinkLogDao.findDistinctByDeviceAPP());
			removeNullAndSetNullString(deviceAppList);
			Collections.reverse(deviceAppList);
		}
		return deviceAppList;
	}
	public void setDeviceAppList(List<String> deviceAppList) {
		this.deviceAppList = deviceAppList;
	}
	
	public List<String> getReferrerList() {
		if (referrerList == null) {
			referrerList = new ArrayList<String>(deeplinkLogDao.findDistinctByReferrer());
			removeNullAndSetNullString(referrerList);
			Collections.reverse(referrerList);
		}
		return referrerList;
	}
	public void setReferrerList(List<String> referrerList) {
		this.referrerList = referrerList;
	}
	
	public List<String> getFryList() {
		if (fryList == null) {
			fryList = new ArrayList<String>(deeplinkLogDao.findDistinctByFry());
			removeNullAndSetNullString(fryList);
			Collections.reverse(fryList);
		}
		return fryList;
	}
	public void setFryList(List<String> fryList) {
		this.fryList = fryList;
	}
	
	public PageResult<DeeplinkLog> getPageResult() {
		return pageResult;
	}
	public void setPageResult(PageResult<DeeplinkLog> pageResult) {
		this.pageResult = pageResult;
	}
	
	
	/*
	 * Because Need null item , Remove null And Set "NULL"
	 */
	private List<String> removeNullAndSetNullString(List<String> ObjectList){
		if(ObjectList.contains(null)){
			ObjectList.remove(null);
			ObjectList.add("NULL");
		}
		return ObjectList;
	}
	
	@DefaultHandler
	public Resolution route() {
		
		if(isSearch != Boolean.FALSE){
			PageLimit page = getPageLimit("row");
			pageResult = deeplinkLogDao.findDeeplinkLogByParameters(
					searchDeviceOS, searchDeviceBrowser, searchDeviceApp, searchReferrer,searchFry,
					Long.valueOf(page.getStartIndex()),
					Long.valueOf(page.getPageSize()));
		}
		return forward();
	}

}
