package com.cyberlink.cosmetic.action.api.product;

import java.util.Locale;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.product.service.SolrProductUpdater;
import com.cyberlink.cosmetic.modules.product.dao.BrandDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.product.dao.StoreDao;
import com.cyberlink.cosmetic.modules.product.dao.StorePriceRangeDao;
import com.cyberlink.cosmetic.modules.product.model.Product;

@UrlBinding("/api/product/UpdateProduct.action")
public class UpdateProductAction extends AbstractProductAction{
	
    private static final String VIEW = "/api/product/UpdateProduct.jsp";

    private Long productId ;
    private Long brandID ;
    private String prodName ;
	private String displayTitle ;
	private String description ;
	private String img_original ;
	private String img_thumbnail ;
	private Long barcode ;
    private String productStoreLink ;
    private Float price ;
    private String extProdID ;
    private Boolean onShelf ;
    private String trialOnYMK ;
    
	//@SpringBean("product.solrProductUpdater")
	//protected SolrProductUpdater solrProductUpdate;
	
	@SpringBean("product.BrandDao")
	protected BrandDao brandDao ;
	
	@SpringBean("product.StoreDao")
	protected StoreDao storeDao ;
	
	@SpringBean("product.StorePriceRangeDao")
	protected StorePriceRangeDao storePriceRangeDao;
	
	@SpringBean("product.ProductDao")
	protected ProductDao productDao ;
	
    @DefaultHandler
    public Resolution route() {
    	if( productDao.exists(productId)){
	    	Product existProduct = productDao.findById(productId) ;
	    	if( brandID != null )
	    		existProduct.setBrand(brandDao.findById(brandID));
	    	if(prodName != null)
	    		existProduct.setProductName(prodName);
	    	if(displayTitle != null)
	    		existProduct.setProductTitle(displayTitle);
	    	if(description != null)
	    		existProduct.setProductDescription(description);
	    	if(img_original != null)
	    		existProduct.setImg_original(img_original);
	    	if(img_thumbnail != null)
	    		existProduct.setImg_thumbnail(img_thumbnail);
	    	if(barcode != null)
	    		existProduct.setBarCode(barcode);
	    	if(productStoreLink != null)
	    		existProduct.setProductStoreLink(productStoreLink);
	    	if(price != null)
	    		existProduct.setPrice(price);
	    	if(extProdID != null)
	    		existProduct.setExtProdID(extProdID);
	    	if(onShelf != null)
	    		existProduct.setOnShelf(onShelf);
	    	if(trialOnYMK != null)
	    		existProduct.setTrialOnYCMakeUp(trialOnYMK);
	    	if(price != null) {
		    	existProduct.setPriceRange(storePriceRangeDao.findPriceRangeByLocalePrice(existProduct.getLocale(), price));
		    	setPriceString(existProduct);
	    	}
	    	productDao.update(existProduct);
	    	//solrProductUpdate.update(existProduct.getId());
    	}	
	    else{
	    	return new ErrorResolution(ErrorDef.InvalidProductId);
	    }
    	return success();
    }

    public void setPriceString( Product prodItem ){
		switch( prodItem.getLocale() ){
			case "de_DE":
				prodItem.setPriceString( "ab EUR " + String.format(Locale.GERMANY,"%,.2f", prodItem.getPrice() ) ) ;
				break;
			case "fr_FR":
				prodItem.setPriceString( "à partir de EUR " + String.format(Locale.GERMANY,"%,.2f", prodItem.getPrice() ) ) ;
				break;
			case "en_GB":
				prodItem.setPriceString( "from \u00A3" + String.format(Locale.UK,"%.2f", prodItem.getPrice() ) ) ;
				break;
			case "ja_JP":
				prodItem.setPriceString( "\u00A5 " + String.format("%,.0f", prodItem.getPrice() ) + "より" ) ;
				break;
			case "zh_CN":
				prodItem.setPriceString( "\u00A5" + String.format("%.0f", prodItem.getPrice() ) ) ;
				break;
			case "zh_TW":
				prodItem.setPriceString( "$" + String.format("%,.0f", prodItem.getPrice() ) ) ;
				break;
			case "en_CA":
				prodItem.setPriceString( "from CDN$ " + String.format("%.2f", prodItem.getPrice() ) ) ;
				break;
			case "en_US":
			default:
				prodItem.setPriceString( "from $" + String.format("%.2f", prodItem.getPrice() ) ) ;
				break;
		}
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

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public String getProductStoreLink() {
		return productStoreLink;
	}

	public void setProductStoreLink(String productStoreLink) {
		this.productStoreLink = productStoreLink;
	}

	public String getExtProdID() {
		return extProdID;
	}

	public void setExtProdID(String extProdID) {
		this.extProdID = extProdID;
	}

	public Boolean isOnShelf() {
		return onShelf;
	}

	public void setOnShelf(Boolean onShelf) {
		this.onShelf = onShelf;
	}

	public String getProdName() {
		return prodName;
	}

	public void setProdName(String prodName) {
		this.prodName = prodName;
	}

	public Long getBrandID() {
		return brandID;
	}

	public void setBrandID(Long brandID) {
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

	public Long getBarcode() {
		return barcode;
	}

	public void setBarcode(Long barcode) {
		this.barcode = barcode;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}
}
