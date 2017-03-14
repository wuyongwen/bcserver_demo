package com.cyberlink.cosmetic.action.api.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cyberlink.cosmetic.modules.product.dao.ProductCommentDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.product.model.Product;
import com.cyberlink.cosmetic.modules.product.model.result.ProductWithCommentWrapper;
import com.cyberlink.cosmetic.modules.product.model.result.ProductWrapper;

import net.sourceforge.stripes.action.DefaultHandler;

import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;

import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/product/QueryProductInfo.action")
public class QueryProductInfoAction extends AbstractProductAction{
	private Long[] productId;
	private String langCode ;
	private String locale ;
	private Long userId ;
	
	@SpringBean("product.ProductDao")
	protected ProductDao productDao;
	
	@SpringBean("product.ProductCommentDao")
	protected ProductCommentDao commentDao;
	
    
	@DefaultHandler
    public Resolution route() {
		if(productId == null){
			return new ErrorResolution(ErrorDef.InvalidProductId);
		}
		/*if(userId == null){
			return new ErrorResolution(ErrorDef.InvalidUserId);
		}*/
		List<Product> productList = productDao.findByProductId(productId);

		final Map<String, Object> results = new HashMap<String, Object>();
		if(userId != null){
			List<ProductWithCommentWrapper> wrapperList = new ArrayList<ProductWithCommentWrapper>();
			for (Product product : productList) {
				wrapperList.add(new ProductWithCommentWrapper(product, userId));
			}
			results.put("totalSize", wrapperList.size());
			results.put("results", wrapperList);
			return json(results);
		}
		else{
			List<ProductWrapper> wrapperList = new ArrayList<ProductWrapper>();
			for (Product product : productList) {
				wrapperList.add(new ProductWrapper(product));
			}
			results.put("totalSize", wrapperList.size());
			results.put("results", wrapperList);
			return json(results);
		}
		
		
	}	


	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public Long[] getProductId() {
		return productId;
	}

	public void setProductId(Long[] productId) {
		this.productId = productId;
	}


	public Long getUserId() {
		return userId;
	}


	public void setUserId(Long userId) {
		this.userId = userId;
	}

	
}
