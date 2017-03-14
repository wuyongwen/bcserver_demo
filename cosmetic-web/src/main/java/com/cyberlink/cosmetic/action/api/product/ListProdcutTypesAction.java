package com.cyberlink.cosmetic.action.api.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.dao.BrandDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductTypeDao;
import com.cyberlink.cosmetic.modules.product.model.ProductType;
import com.cyberlink.cosmetic.modules.product.model.result.ProductTypeWrapper;

import net.sourceforge.stripes.action.DefaultHandler;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/prod/listtypes.action")
public class ListProdcutTypesAction extends AbstractProductAction{
	private Long brandId ;
	private int langId ;
	private String brandCode ;
	private String locale ;
	
	@SpringBean("product.BrandDao")
	protected BrandDao brandDao;
	
	@SpringBean("product.ProductTypeDao")
	protected ProductTypeDao productTypeDao;
	
	@DefaultHandler
    public Resolution route() {
		if(brandId!= null && !brandDao.exists(brandId)){
			return new ErrorResolution(ErrorDef.InvalidBrandId);
		}
		
		final Map<String, Object> results = new HashMap<String, Object>();
		List<ProductType> typeSearchResult = productTypeDao.listAllProdTypeByLocale(locale);
		List<ProductTypeWrapper>  wrapperList = new ArrayList<ProductTypeWrapper>();
		for( ProductType type: typeSearchResult ){
			wrapperList.add(new ProductTypeWrapper(type));
		}
		results.put("totalSize", wrapperList.size() );
		results.put("results", wrapperList);
		return json(results);

	}
	
	public Long getBrandID() {
		return brandId;
	}
	public void setBrandID(Long brandID) {
		brandId = brandID;
	}

	public String getBrandCode() {
		return brandCode;
	}

	public void setBrandCode(String brandCode) {
		this.brandCode = brandCode;
	}

	public int getLangId() {
		return langId;
	}

	public void setLangId(int langId) {
		this.langId = langId;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	

}
