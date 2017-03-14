package com.cyberlink.cosmetic.action.api.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.product.model.result.BrandIndexWrapper;
import com.cyberlink.cosmetic.modules.product.model.result.BrandWrapper;
import com.cyberlink.cosmetic.modules.product.dao.BrandDao;
import com.cyberlink.cosmetic.modules.product.dao.BrandIndexDao;
import com.cyberlink.cosmetic.modules.product.model.Brand;
import com.cyberlink.cosmetic.modules.product.model.BrandIndex;

import net.sourceforge.stripes.action.DefaultHandler;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/prod/listBrandIndex.action")
public class ListBrandIndexAction extends AbstractAction{
	
	private String locale ;
	
	@SpringBean("product.BrandIndexDao")
	protected BrandIndexDao brandIndexDao;

	@SpringBean("product.BrandDao")
	protected BrandDao brandDao;
	
	@DefaultHandler
    public Resolution route() {
		if(locale == null){
			return new ErrorResolution(ErrorDef.InvalidLocale);
		}
		final Map<String, Object> results = new HashMap<String, Object>();
		List<BrandIndex> brandIndexList = brandIndexDao.listAllIndexByLocale(locale);
		List<BrandIndexWrapper> brandIndexWrapper = new ArrayList<BrandIndexWrapper> ();
		for( BrandIndex bIndex: brandIndexList){
			BrandIndexWrapper newBrandIndexWrapper = new BrandIndexWrapper(bIndex);
			List<Brand> currentIndexedBrandList = 
					brandDao.listBrandByLocale(bIndex.getId(), locale, Long.valueOf(0), Long.MAX_VALUE).getResults() ;
			List<BrandWrapper> currentIndexedBrandWrapperList = new ArrayList<BrandWrapper> ();
			for( Brand currentIndexedBrand : currentIndexedBrandList ){
				currentIndexedBrandWrapperList.add(new BrandWrapper(currentIndexedBrand));
			}
			newBrandIndexWrapper.setBrandList(currentIndexedBrandWrapperList);
			brandIndexWrapper.add( newBrandIndexWrapper );
		}
		results.put("results", brandIndexWrapper);
		results.put("totalSize", brandIndexWrapper.size());
		return json(results);
	
	}
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}
	

}
