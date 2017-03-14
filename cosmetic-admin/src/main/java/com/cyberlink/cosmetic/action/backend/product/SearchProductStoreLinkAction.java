package com.cyberlink.cosmetic.action.backend.product;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.store.Amazon;
import com.cyberlink.store.Taobao;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/product/SearchProductStoreLink.action")
public class SearchProductStoreLinkAction extends AbstractAction{
	
	@SpringBean("common.localeDao")
	private LocaleDao localeDao;
	
	private String locale;
	private String exProductId;
	private String productStoreLink;
	private Set<String> localeList;
	
	@DefaultHandler
    public Resolution route() {
		if (!getCurrentUserAdmin() && !getAccessControl().getProductManagerAccess()) {
	    	return new StreamingResolution("text/html", "Need to login");
	    }
		localeList = localeDao.getAvailableLocaleByType(LocaleType.PRODUCT_LOCALE);
		if(locale != null && !StringUtils.isBlank(exProductId)){
			String tempProductStoreLink = null;
			if(locale.equalsIgnoreCase("zh_TW")){
				tempProductStoreLink = "https://buy.yahoo.com.tw/gdsale/gdsale.asp?gdid=" + exProductId + "&act=gdsearch&co_servername=37643de4cd429a0fd4c0547de27c5982";
			}else if(locale.equalsIgnoreCase("zh_CN")){
				Taobao tb = new Taobao("");
				tempProductStoreLink = tb.getStoreLinkByExProductID(exProductId);
			}else{//en_US,de_DE,fr_FR,en_GB,en_CA,ja_JP, and OTHER
				Amazon an = new Amazon(locale,"");
				tempProductStoreLink = an.getStoreLinkByExProductID(exProductId);
			}
			if(StringUtils.isBlank(tempProductStoreLink)){
				productStoreLink = null;
			} else {
				productStoreLink = tempProductStoreLink;
			}
		} else {
			productStoreLink = "";
		}
		return forward();
	}
	
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	public Set<String> getLocaleList() {
		return localeList;
	}

	public void setLocaleList(Set<String> localeList) {
		this.localeList = localeList;
	}

	public String getExProductId() {
		return exProductId;
	}

	public void setExProductId(String exProductId) {
		this.exProductId = exProductId;
	}

	public String getProductStoreLink() {
		return productStoreLink;
	}
}
