package com.cyberlink.cosmetic.action.api.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.product.model.result.BrandWrapper;
import com.cyberlink.cosmetic.modules.product.dao.BrandDao;
import com.cyberlink.cosmetic.modules.product.model.Brand;

import net.sourceforge.stripes.action.DefaultHandler;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/prod/listbrands.action")
public class ListBrandsAction extends AbstractAction{

    private Long offset = Long.valueOf(0);
    private Long limit = Long.valueOf(10);
	private Long langId = Long.valueOf(0);
	private String locale ;
	private Long brandIndexId ;
	
	@SpringBean("product.BrandDao")
	protected BrandDao brandDao;

	@DefaultHandler
    public Resolution route() {
		if( offset < 0 ){
    		return new ErrorResolution(ErrorDef.InvalidOffset);
    	}
		if( limit < 0 || limit > 20 ){
    		return new ErrorResolution(ErrorDef.InvalidLimit);
    	}
		final Map<String, Object> results = new HashMap<String, Object>();
		List<Brand> brandResult = brandDao.listAllBrandByLocalePriority(locale);
		List<BrandWrapper>  wrapperList = new ArrayList<BrandWrapper>();
		for( Brand brand: brandResult){
			wrapperList.add(new BrandWrapper(brand));
		}
		results.put("results", wrapperList);
		results.put("totalSize", brandResult.size());
		return json(results);
	}
	
	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public long getLimit() {
		return limit;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}


	public Long getLangId() {
		return langId;
	}

	public void setLangId(Long langId) {
		this.langId = langId;
	}

	public Long getBrandIndexId() {
		return brandIndexId;
	}

	public void setBrandIndexId(Long brandIndexId) {
		this.brandIndexId = brandIndexId;
	}

}
