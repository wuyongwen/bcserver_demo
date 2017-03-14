package com.cyberlink.cosmetic.action.api.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.product.dao.StorePriceRangeDao;
import com.cyberlink.cosmetic.modules.product.model.StorePriceRange;
import com.cyberlink.cosmetic.modules.product.model.result.PriceRangeWrapper;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/store/listPriceRangAndName.action")
public class ListPriceRangAndNameAction extends AbstractAction{
	
	private String locale ;
	
	@SpringBean("product.StorePriceRangeDao")
	private StorePriceRangeDao storePriceRangeDao;
	
	@DefaultHandler
    public Resolution route() {
		
		final Map<String, Object> results = new HashMap<String, Object>();
		List<StorePriceRange> priceRangeResult = new ArrayList<StorePriceRange>();
		priceRangeResult = storePriceRangeDao.listAllPriceRangeByLocale(locale);
		List<PriceRangeWrapper> wrappedPriceRangeList = new ArrayList<PriceRangeWrapper>();
		
		for( StorePriceRange priceRageItem: priceRangeResult ){
			wrappedPriceRangeList.add(new PriceRangeWrapper( priceRageItem ));
		}
		results.put("results", wrappedPriceRangeList);
		return json(results);
	}
	
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
}
