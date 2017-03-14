package com.cyberlink.cosmetic.modules.product.service.impl;


import java.util.List;
import java.util.Locale;

import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.modules.product.model.Product;
import com.cyberlink.cosmetic.modules.product.model.StorePriceRange;
import com.cyberlink.cosmetic.modules.product.service.ProductService;
import com.cyberlink.cosmetic.modules.product.dao.BrandDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.product.dao.StoreDao;
import com.cyberlink.cosmetic.modules.product.dao.StorePriceRangeDao;

public class ProductServiceImpl extends AbstractService implements ProductService{

	@SpringBean("product.ProductDao")
	private ProductDao productDao ;
	
	@SpringBean("product.BrandDao")
	private BrandDao brandDao ;
	
	@SpringBean("product.StoreDao")
	private StoreDao storeDao ;
	
	@SpringBean("product.StorePriceRangeDao")
	private StorePriceRangeDao storePriceRangeDao;

	public Product createOrUpdate(String locale, long BrandID, long storeID, long typeGroupID,
			String ProductName, String ProductTitle, String Description, String Img_original, 
			String Img_thumbnail, long barcode, String productStoreLink, float price,
			String ExtProdID, boolean OnShelf, String TrialOnYMK, Long typeId[]) {
		
		Product ExistProduct = productDao.findByBrandIdExtProdID_StoreID(BrandID, ExtProdID, storeID, typeId);
		if(ExistProduct != null){
			ExistProduct.setLocale(locale);
			ExistProduct.setBrand(brandDao.findById(BrandID));
			ExistProduct.setStore(storeDao.findById(storeID));
			ExistProduct.setTypeGroupId(typeGroupID);
			ExistProduct.setProductName(ProductName);
			ExistProduct.setProductTitle(ProductTitle);
			ExistProduct.setProductDescription(Description);
			ExistProduct.setImg_original(Img_original);
			ExistProduct.setImg_thumbnail(Img_thumbnail);
			ExistProduct.setBarCode(barcode);
			ExistProduct.setProductStoreLink(productStoreLink);
			ExistProduct.setPrice(price);
			ExistProduct.setExtProdID(ExtProdID);
			//ExistProduct.setOnShelf(OnShelf);
			ExistProduct.setTrialOnYCMakeUp(TrialOnYMK);
			ExistProduct.setObjVersion(ExistProduct.getObjVersion()+1);
			setPriceString(ExistProduct);
			StorePriceRange priceRange =
					storePriceRangeDao.findPriceRangeByLocalePrice(locale, price) ;
			ExistProduct.setPriceRange(priceRange);
			return productDao.update(ExistProduct);
		}
		else{
			Product newProduct = new Product();
			newProduct.setLocale(locale);
			newProduct.setBrand(brandDao.findById(BrandID));
			newProduct.setStore(storeDao.findById(storeID));
			newProduct.setTypeGroupId(typeGroupID);
			newProduct.setProductName(ProductName);
			newProduct.setProductTitle(ProductTitle);
			newProduct.setProductDescription(Description);
			newProduct.setImg_original(Img_original);
			newProduct.setImg_thumbnail(Img_thumbnail);
			newProduct.setBarCode(barcode);
			newProduct.setProductStoreLink(productStoreLink);
			newProduct.setPrice(price);
			newProduct.setExtProdID(ExtProdID);
			newProduct.setOnShelf(OnShelf);
			newProduct.setTrialOnYCMakeUp(TrialOnYMK);
			setPriceString(newProduct);
			StorePriceRange priceRange =
					storePriceRangeDao.findPriceRangeByLocalePrice(locale, price) ;
			newProduct.setPriceRange(priceRange);
			return productDao.create(newProduct);
		}
		
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

	public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }
	public ProductDao getProductDao(){
		return this.productDao;
	}
	
	public BrandDao getBrandDao() {
		return brandDao;
	}

	public void setBrandDao(BrandDao brandDao) {
		this.brandDao = brandDao;
	}

	public StoreDao getStoreDao() {
		return storeDao;
	}

	public void setStoreDao(StoreDao storeDao) {
		this.storeDao = storeDao;
	}

	public StorePriceRangeDao getStorePriceRangeDao() {
		return storePriceRangeDao;
	}

	public void setStorePriceRangeDao(StorePriceRangeDao storePriceRangeDao) {
		this.storePriceRangeDao = storePriceRangeDao;
	}

}
