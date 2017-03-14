package com.cyberlink.cosmetic.action.api.product;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.modules.product.service.SolrProductUpdater;
import com.cyberlink.cosmetic.modules.product.service.ProductService;
import com.cyberlink.cosmetic.modules.product.service.RelProductTypeService;
import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.product.model.Product;

@UrlBinding("/api/product/CreateProduct.action")
public class CreateProductAction extends AbstractProductAction{
	
    private static final String VIEW = "/api/product/CreateProduct.jsp";

	private String locale ;//actually means locale
    private long brandID ;
    private long storeID;
    private long typeGroupID ;
    private String prodName ;
	private String displayTitle ;
	private String description ;
	private String img_original ;
	private String img_thumbnail ;
	private long barcode ;
    private String productStoreLink ;
    private float price ;
    private String extProdID ;
    private boolean onShelf ;
    private String trialOnYMK ;
    private Long[] typeID ;
    
    @SpringBean("product.ProductDao")
    protected ProductDao productDao ;
    
	@SpringBean("product.productService")
	protected ProductService productService ;
	
	@SpringBean("product.RelProductTypeService")
	protected RelProductTypeService relProductTypeService;
	
	//@SpringBean("product.solrProductUpdater")
	//protected SolrProductUpdater solrProductUpdate;
	
    @DefaultHandler
    public Resolution route() {
    	/*
    	Product existedProduct = productDao.findByBrandIdExtProdID_StoreID(brandID, extProdID, storeID) ; 
    	if( existedProduct != null && existedProduct.getIsDeleted() ){
    		return new StreamingResolution("text/plain", existedProduct.getId().toString() );
    	}
    	*/
    	Product newProduct = 
    			productService.createOrUpdate(locale, brandID, storeID, typeGroupID,
    			prodName, displayTitle, description, img_original, img_thumbnail,
    			barcode, productStoreLink, price, extProdID, onShelf, trialOnYMK, typeID);
    	
    	for(long TID: typeID){
    		relProductTypeService.createOrUpdate(newProduct.getId(), TID);
    	}
    	//solrProductUpdate.update(newProduct.getId());
    	return new StreamingResolution("text/plain", newProduct.getId().toString() );
    }

	public String getDisplayTitle() {
		return displayTitle;
	}

	public void setDisplayTitle(String displayTitle) {
		this.displayTitle = displayTitle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getStoreID() {
		return storeID;
	}

	public void setStoreID(long storeID) {
		this.storeID = storeID;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getProductStoreLink() {
		return productStoreLink;
	}

	public void setProductStoreLink(String productStoreLink) {
		this.productStoreLink = productStoreLink;
	}

	public Long[] getTypeID() {
		return typeID;
	}

	public void setTypeID(Long[] typeID) {
		this.typeID = typeID;
	}

	public String getExtProdID() {
		return extProdID;
	}

	public void setExtProdID(String extProdID) {
		this.extProdID = extProdID;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public boolean isOnShelf() {
		return onShelf;
	}

	public void setOnShelf(boolean onShelf) {
		this.onShelf = onShelf;
	}

	public String getProdName() {
		return prodName;
	}

	public void setProdName(String prodName) {
		this.prodName = prodName;
	}

	public long getTypeGroupID() {
		return typeGroupID;
	}

	public void setTypeGroupID(long typeGroupID) {
		this.typeGroupID = typeGroupID;
	}

	public long getBrandID() {
		return brandID;
	}

	public void setBrandID(long brandID) {
		this.brandID = brandID;
	}

	public String getTrialOnYMK() {
		return trialOnYMK;
	}

	public void setTrialOnYMK(String trialOnYMK) {
		this.trialOnYMK = trialOnYMK;
	}

	public String getImg_original() {
		return img_original;
	}

	public void setImg_original(String img_original) {
		this.img_original = img_original;
	}

	public String getImg_thumbnail() {
		return img_thumbnail;
	}

	public void setImg_thumbnail(String img_thumbnail) {
		this.img_thumbnail = img_thumbnail;
	}

	public long getBarcode() {
		return barcode;
	}

	public void setBarcode(long barcode) {
		this.barcode = barcode;
	}

}
