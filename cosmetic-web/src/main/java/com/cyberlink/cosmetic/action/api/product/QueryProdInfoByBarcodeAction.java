package com.cyberlink.cosmetic.action.api.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.product.model.Product;
import com.cyberlink.cosmetic.modules.product.model.result.ProductWrapper;

import net.sourceforge.stripes.action.DefaultHandler;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

@UrlBinding("/api/product/QueryProdInfoByBarcode.action")
public class QueryProdInfoByBarcodeAction extends AbstractProductAction{
	private Long[] barcode;
	private String langCode ;
	private String locale ;
	private Long offset = Long.valueOf(0), limit = Long.valueOf(10);
	
	@SpringBean("product.ProductDao")
	protected ProductDao productDao;
	
	@DefaultHandler
    public Resolution route() {
		if(barcode == null){
			return new ErrorResolution(ErrorDef.InvalidBarcode);
		}
		PageResult<Product> productList = productDao.findByBarcodeLocale(locale, offset, limit, barcode);
		
		final Map<String, Object> results = new HashMap<String, Object>();
		List<ProductWrapper> wrapperList = new ArrayList<ProductWrapper>();
		for (Product product : productList.getResults()) {
			wrapperList.add(new ProductWrapper(product));
		}
		results.put("totalSize", productList.getTotalSize());
		results.put("results", wrapperList);
		return json(results);
	}


	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	public Long[] getBarcode() {
		return barcode;
	}

	public void setBarcode(Long[] barcode) {
		this.barcode = barcode;
	}


	public Long getLimit() {
		return limit;
	}


	public void setLimit(Long limit) {
		this.limit = limit;
	}


	public Long getOffset() {
		return offset;
	}


	public void setOffset(Long offset) {
		this.offset = offset;
	}
	
	

}
