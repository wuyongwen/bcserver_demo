package com.cyberlink.cosmetic.action.api.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.product.dao.StorePriceRangeDao;
import com.cyberlink.cosmetic.modules.product.model.StorePriceRange;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/store/listPriceRange.action")
public class ListPriceRangAction extends AbstractAction{
	private String locale ;
	
	@SpringBean("product.StorePriceRangeDao")
	private StorePriceRangeDao storePriceRangeDao;
	
	@DefaultHandler
    public Resolution route() {
		
		final Map<String, Object> results = new HashMap<String, Object>();
		List<StorePriceRange> storePriceRange = new ArrayList<StorePriceRange>();
		storePriceRange = storePriceRangeDao.listAllPriceRangeByLocale(locale);
		List<String> priceRangeList = new ArrayList<String>();
		String priceFormatParam = getPriceFormatParamByLocale(locale) ;
		int index = 0 ;
		for( StorePriceRange priceRageItem: storePriceRange ){
			if( index != storePriceRange.size()-1 ){//don't output last price range.
				priceRangeList.add( String.format(priceFormatParam, priceRageItem.getPriceMin()) );
				priceRangeList.add( String.format(priceFormatParam, priceRageItem.getPriceMax()) );
			}
			index++;
		}
		results.put("results", priceRangeList);
		return json(results);
	}
	
	public String getPriceFormatParamByLocale(String locale){
		switch (locale){
		case "en_US":
		case "en_CA":
		case "en_GB":
		case "de_DE":
		case "fr_FR":
		default:
			return "%.2f" ;
		case "zh_TW":
		case "zh_CN":
		case "ja_JP":
			return "%.0f" ;
		}
	}
	
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
}
