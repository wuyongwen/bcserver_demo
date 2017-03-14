package com.cyberlink.cosmetic.modules.product.dao;

import java.util.Date;
import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.model.Product;
import com.cyberlink.cosmetic.modules.product.model.StorePriceRange;

public interface ProductDao extends GenericDao<Product, Long>{
	PageResult<Product> findByText(String text, String locale, Long offset, Long limit);
	List<Product> findByProdIDLocale(String locale, Long... prodID);
	List<Product> findByProductId(Long... prodID);
	PageResult<Product> findByBarcodeLocale(String locale, Long offset, Long limit, Long... Barcode);
	Product findByBrandIdExtProdID_StoreID(Long BrandId, String ExtProdID, Long StoreID, Long typeId[]);
	PageResult<Product> findProdByParameters(String locale, Long brandId, Long typeId, String brandName,
			Long offset, Long limit, Boolean onShelf, List<StorePriceRange> storePriceRange);

	PageResult<Product> findProdByParams( String locale, Long brandId, Long typeId, String brandName,
			Long offset, Long limit, Boolean onShelf,Long priceRangeId );
	
    Long countUndeleted();
    List<Product> findUndeleted(Integer pageIndex, Integer pageSize);
    
    List<Product> listProdByLocale(String locale);
    List<Product> listProdByLocalePriceRangeId(String locale, Long priceRangeId);
    
    //for backend
    PageResult<Product> findAllProduct(PageLimit pageLimit);
    List<Long> findProdByCommentTime(Date startTime, Date endTime);
}
