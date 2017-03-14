package com.cyberlink.cosmetic.action.api.product;

import java.util.HashMap;
import java.util.Map;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.product.dao.StoreDao;
import com.cyberlink.cosmetic.modules.product.model.Store;

import net.sourceforge.stripes.action.DefaultHandler;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/product/QueryProdStore.action")
public class QueryStoreAction extends AbstractProductAction{
	
	private int langId ;
	private Long productId ;
	private String locale;
	private Long offset= Long.valueOf(0);
	private Long limit= Long.valueOf(10);	
	
	@SpringBean("product.StoreDao")
	private StoreDao storeDao;
	
	@SpringBean("product.ProductDao")
	protected ProductDao productDao;
	
	@DefaultHandler
    public Resolution route() {
    	final Map<String, Object> results = new HashMap<String, Object>();

		if(!productDao.exists(productId)){
			return new ErrorResolution(ErrorDef.InvalidProductId);
		}

    	Store targetStore = storeDao.findById(productDao.findById(productId).getStore().getId());
    	results.put("results", targetStore);
    	return json(results);
    }
	
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	public Long getOffset() {
		return offset;
	}

	public void setOffset(Long offset) {
		this.offset = offset;
	}

	public Long getLimit() {
		return limit;
	}

	public void setLimit(Long limit) {
		this.limit = limit;
	}

	public int getLangId() {
		return langId;
	}

	public void setLangId(int langId) {
		this.langId = langId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}


}
