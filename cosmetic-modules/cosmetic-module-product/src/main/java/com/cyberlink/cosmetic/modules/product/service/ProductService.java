package com.cyberlink.cosmetic.modules.product.service;

import com.cyberlink.cosmetic.modules.product.model.Product;

public interface ProductService {
	public Product createOrUpdate( String locale, long BrandID, long storeID, long typeGroupID,
			String ProductName, String ProductTitle, String Description, String Img_original, 
			String Img_thumbnail, long barcode, String productStoreLink, float price,
			String ExtProdID, boolean OnShelf, String TrialOnYMK, Long typeID[]);
    //public Product isUserExist(Long ID);
	
    

}
