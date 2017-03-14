package com.cyberlink.cosmetic.action.api.product;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.api.AbstractAction;

import com.cyberlink.cosmetic.modules.product.model.Brand;
import com.cyberlink.cosmetic.modules.product.model.BrandIndex;
import com.cyberlink.cosmetic.modules.product.service.BrandIndexService;
import com.cyberlink.cosmetic.modules.product.service.BrandService;

@UrlBinding("/api/product/CreateBrand.action")
public class CreateBrandAction extends AbstractAction{
	
	@SpringBean("product.BrandService")
	private BrandService brandService;
	
	@SpringBean("product.BrandIndexService")
	private BrandIndexService brandIndexService ;
	
	private String brandName ;
	private String brandIndexName ;
	private String locale ;
	
	@DefaultHandler
	public Resolution route() {
		if( brandName == null  ){
			return new ErrorResolution(ErrorDef.InvalidBrandName);
		}
		if( brandIndexName == null ){
			return new ErrorResolution(ErrorDef.InvalidBrandIndexName);
		}
		if( locale == null ){
			return new ErrorResolution(ErrorDef.InvalidLocale);
		}
		BrandIndex newBrandIndex = brandIndexService.createOrUpdate(brandIndexName, locale) ;
		Brand newBrand = brandService.createOrUpdate(brandName, newBrandIndex, locale) ;
		return new StreamingResolution("text/html", "ID " + newBrand.getId()
				+ " Brand Name " + newBrand.getBrandName() + " index " 
				+ newBrand.getBrandIndex().getIndex() + " created" );
	}

	public String getLocale() {
		return locale;
	}
	
	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getBrandIndexName() {
		return brandIndexName;
	}

	public void setBrandIndexName(String brandIndexName) {
		this.brandIndexName = brandIndexName;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}


}
