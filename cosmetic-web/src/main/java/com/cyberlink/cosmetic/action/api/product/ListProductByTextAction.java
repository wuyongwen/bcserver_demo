package com.cyberlink.cosmetic.action.api.product;

import java.util.HashMap;
import java.util.Map;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.product.dao.SolrProductDao;
import com.cyberlink.cosmetic.modules.product.model.Product;
import com.cyberlink.cosmetic.modules.product.model.ProductSearchParam;
import com.cyberlink.cosmetic.modules.product.model.result.ProductWrapper;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/product/list-product-bytext.action")
public class ListProductByTextAction extends AbstractAction{
//    @SpringBean("product.solrProductDao")
//    private SolrProductDao solrDao;
//    private String keyword;
//    private String locale ;
//    private Long offset = Long.valueOf(0);
//    private Long limit = Long.valueOf(10);
//    private Long brandId ;
//    private Long typeId ;
//    private Long priceRangeId ;
//    
//    @DefaultHandler
//    public Resolution route() {
//    	final Map<String, Object> results = new HashMap<String, Object>();
//    	ProductSearchParam param = new ProductSearchParam();
//    	param.setKeyword(keyword);
//    	param.setOffset(offset.intValue());
//    	param.setPageSize(limit.intValue());
//    	param.setLocale(locale);
//    	PageResult<Product> productList = null;
//    	PageResult<ProductWrapper> resultList = new  PageResult<ProductWrapper>();
//    	for( Product p : productList.getResults() ){
//    		resultList.add(new ProductWrapper(p));
//    	}
//    	
//    	results.put("results", resultList.getResults());
//    	results.put("totalSize", productList.getTotalSize());
//    	return json(results);
//    }
//    
//	public String getKeyword() {
//		return keyword;
//	}
//	public void setKeyword(String keyword) {
//		this.keyword = keyword;
//	}
//	public Long getOffset() {
//		return offset;
//	}
//	public void setOffset(Long offset) {
//		this.offset = offset;
//	}
//	public Long getLimit() {
//		return limit;
//	}
//	public void setLimit(Long limit) {
//		this.limit = limit;
//	}
//
//	public String getLocale() {
//		return locale;
//	}
//
//	public void setLocale(String locale) {
//		this.locale = locale;
//	}
//
//	public Long getBrandId() {
//		return brandId;
//	}
//
//	public void setBrandId(Long brandId) {
//		this.brandId = brandId;
//	}
//
//	public Long getTypeId() {
//		return typeId;
//	}
//
//	public void setTypeId(Long typeId) {
//		this.typeId = typeId;
//	}
//
//	public Long getPriceRangeId() {
//		return priceRangeId;
//	}
//
//	public void setPriceRangeId(Long priceRangeId) {
//		this.priceRangeId = priceRangeId;
//	}
}
