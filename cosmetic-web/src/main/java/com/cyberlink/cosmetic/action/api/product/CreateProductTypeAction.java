package com.cyberlink.cosmetic.action.api.product;

import net.sourceforge.stripes.action.DefaultHandler;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.product.model.ProductType;
import com.cyberlink.cosmetic.modules.product.service.ProductTypeService;


@UrlBinding("/api/product/CreateProductType.action")
public class CreateProductTypeAction extends AbstractAction{
	
	@SpringBean("product.ProductTypeService")
	private ProductTypeService productTypeService ;
	
	private String typeName ;
	private String locale ;
	
	@DefaultHandler
	public Resolution route() {
		if( typeName == null ){
			return new ErrorResolution(ErrorDef.InvalidProdTypeName);
		}
		if( locale == null ){
			return new ErrorResolution(ErrorDef.InvalidLocale);
		}
		ProductType newProductType = productTypeService.createOrUpdate(typeName, locale);
		return new StreamingResolution("text/html", "ID " + newProductType.getId()
				+ " Category Name " + newProductType.getTypeName() + " created" );
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}
	

}
