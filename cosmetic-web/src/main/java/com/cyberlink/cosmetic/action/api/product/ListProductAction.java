package com.cyberlink.cosmetic.action.api.product;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;  
import org.slf4j.LoggerFactory;  

import net.sourceforge.stripes.action.DefaultHandler;

import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;

import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.product.dao.BackendProductDao;
import com.cyberlink.cosmetic.modules.product.dao.BrandDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductTypeDao;
import com.cyberlink.cosmetic.modules.product.dao.StorePriceRangeDao;
import com.cyberlink.cosmetic.modules.product.model.Product;
import com.cyberlink.cosmetic.modules.product.model.BackendProduct;
import com.cyberlink.cosmetic.modules.product.model.StorePriceRange;
import com.cyberlink.cosmetic.modules.product.model.result.BackendProductWrapper;
import com.cyberlink.cosmetic.modules.product.model.result.ProductWrapper;

@UrlBinding("/api/product/ListProduct.action")
public class ListProductAction extends AbstractAction{
	//private static Logger log = LoggerFactory.getLogger(ListProductAction.class);  
    private Long brandId;
    private Long typeId; 
    private Float priceMin = null, priceMax = null;
    private Long priceRangeId;
    private Long offset = Long.valueOf(0);
    private Long limit = Long.valueOf(10);
    private Long langId ;
    private String brandCode;
    private String locale ;
    private Boolean onShelf = true;
    private Boolean forBackend = false;

    @SpringBean("product.BrandDao")
	protected BrandDao brandDao;
	
	@SpringBean("product.ProductTypeDao")
	protected ProductTypeDao productTypeDao;
	
	@SpringBean("product.ProductDao")
	protected ProductDao productDao;
	
	@SpringBean("product.BackendProductDao")
	protected BackendProductDao backendProductDao;
	
	@SpringBean("product.StorePriceRangeDao")
    protected StorePriceRangeDao storePriceRangeDao;
	
    @DefaultHandler
    public Resolution route() {
    	//Long initTime = System.nanoTime();
    	//Long startTime = System.nanoTime();
    	//no need if we don't check the brand Id and type Id no matter they exist
    	/* 
    	if( brandId != null && !brandDao.exists(brandId)){
    		return new ErrorResolution(ErrorDef.InvalidBrandId);
    	}
    	if( typeId != null && !productTypeDao.exists(typeId)){
    		return new ErrorResolution(ErrorDef.InvalidTypeId);
    	}
    	*/
    	if( limit > 20 || limit < 0 ){
    		return new ErrorResolution(ErrorDef.InvalidLimit);
    	}
    	if( offset < 0 ){
    		return new ErrorResolution(ErrorDef.InvalidOffset);
    	}
    	
    	final Map<String, Object> results = new HashMap<String, Object>();
    	List<StorePriceRange> storePriceRange = new ArrayList<StorePriceRange>();
    	//Long estimatedTime1 = System.nanoTime() - startTime;
    	Boolean comparePriceRange = Boolean.FALSE ;
    	//startTime = System.nanoTime() ;
    	if( priceRangeId == null && !forBackend ){
    		if( priceMax != null && priceMin != null && priceMax < priceMin){
        		return new ErrorResolution(ErrorDef.InvalidPriceRange);
        	}
    		storePriceRange = storePriceRangeDao.listAllPriceRangeByLocalePrice(locale, priceMax, priceMin) ;
    		comparePriceRange = Boolean.TRUE;
    	}
    	//Long estimatedTime2 = System.nanoTime() - startTime ;
    	//startTime = System.nanoTime() ;
    	if(forBackend){
    		PageResult<BackendProduct> productResult = new PageResult<BackendProduct>();
    		productResult = backendProductDao.findProdByParams(locale, brandId, typeId, 
    				brandCode, offset, limit, onShelf, Long.valueOf(0) );
    		List<BackendProductWrapper>  wrapperList = new ArrayList<BackendProductWrapper>();
    		for( BackendProduct product: productResult.getResults()){
    			wrapperList.add(new BackendProductWrapper(product));
    		}
    		results.put("totalSize", productResult.getTotalSize());
    		results.put("results", wrapperList);
    	}
    	else if( !forBackend && comparePriceRange ){
	    	PageResult<Product> productResult = new PageResult<Product>();
	    	
	    	productResult = productDao.findProdByParameters(locale, brandId, typeId, 
	    			brandCode, offset, limit, onShelf, storePriceRange );
	    	
			List<ProductWrapper>  wrapperList = new ArrayList<ProductWrapper>();
			for( Product product: productResult.getResults()){
				wrapperList.add(new ProductWrapper(product));
			}
			results.put("totalSize", productResult.getTotalSize());
			results.put("results", wrapperList);
    	}
    	else{
    		PageResult<Product> productResult = new PageResult<Product>();
    		productResult = productDao.findProdByParams(locale, brandId, typeId, 
    				brandCode, offset, limit, onShelf, priceRangeId );
    		List<ProductWrapper>  wrapperList = new ArrayList<ProductWrapper>();
			for( Product product: productResult.getResults()){
				wrapperList.add(new ProductWrapper(product));
			}
			results.put("totalSize", productResult.getTotalSize());
			results.put("results", wrapperList);
    	}
    	//Long estimatedTime3 = System.nanoTime() - startTime ;
    	//Long totalTime = System.nanoTime() - initTime ;
    	/*String text = estimatedTime1/1000000 + " " + estimatedTime2/1000000 + " " +
    			estimatedTime3/1000000 + " " + totalTime/1000000 + "\r\n" ;
    	File file = new File("durationn.txt");
		try {
			BufferedWriter bufWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true),"utf8"));
			bufWriter.write(text);
            bufWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		return json(results);
		
    }
    
    public Long getBrandId() {
		return brandId;
	}

	public void setBrandId(Long brandID) {
		this.brandId = brandID;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeID) {
		this.typeId = typeID;
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

	public String getBrandCode() {
		return brandCode;
	}

	public void setBrandCode(String brandCode) {
		this.brandCode = brandCode;
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

	public Float getPriceMax() {
		return priceMax;
	}

	public void setPriceMax(Float priceMax) {
		this.priceMax = priceMax;
	}

	public Float getPriceMin() {
		return priceMin;
	}

	public void setPriceMin(Float priceMin) {
		this.priceMin = priceMin;
	}

	public Boolean getOnShelf() {
		return onShelf;
	}

	public void setOnShelf(Boolean onShelf) {
		this.onShelf = onShelf;
	}

	public Boolean getForBackend() {
		return forBackend;
	}

	public void setForBackend(Boolean forBackend) {
		this.forBackend = forBackend;
	}

	public Long getPriceRangeId() {
		return priceRangeId;
	}

	public void setPriceRangeId(Long priceRangeId) {
		this.priceRangeId = priceRangeId;
	}
    
}
