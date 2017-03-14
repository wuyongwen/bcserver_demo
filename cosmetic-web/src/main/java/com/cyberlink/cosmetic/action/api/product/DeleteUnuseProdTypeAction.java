package com.cyberlink.cosmetic.action.api.product;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductTypeDao;
import com.cyberlink.cosmetic.modules.product.dao.StoreDao;
import com.cyberlink.cosmetic.modules.product.dao.StorePriceRangeDao;
import com.cyberlink.cosmetic.modules.product.model.ProductType;
import com.cyberlink.cosmetic.modules.product.model.Store;

@UrlBinding("/api/product/deleteUnusedProdType.action")
public class DeleteUnuseProdTypeAction extends AbstractAction{
	
	private List<ProductType> unuseProdType = new ArrayList<ProductType>();
	
	@SpringBean("product.StoreDao")
	protected StoreDao storeDao ;
	
	@SpringBean("product.ProductTypeDao")
	protected ProductTypeDao productTypeDao;
	
	@SpringBean("product.ProductDao")
	protected ProductDao productDao;
	
	@SpringBean("product.StorePriceRangeDao")
    protected StorePriceRangeDao storePriceRangeDao;
	
	@DefaultHandler
    public Resolution route() {
		List<Store> currentStoreList = storeDao.findAll();
		List<ProductType> currentProdTypeList = productTypeDao.findAll();
		for( ProductType type : currentProdTypeList ){
			int usedLocales = 0;
			for( Store storeItem: currentStoreList){
				if(productDao.findProdByParameters(storeItem.getLocale(), null, Long.valueOf(type.getId())
						, null, Long.valueOf(0), Long.valueOf(10),
						null, storePriceRangeDao.listAllPriceRangeByLocale(storeItem.getLocale()) ).getTotalSize() > 0){
					usedLocales++;
				} ;
			}
			if(usedLocales == 0){
				unuseProdType.add(type);
				productTypeDao.delete(type);
			}
		}
		
		for(ProductType deletedType: unuseProdType){
			logger.info("Type ID " + deletedType.getId() + " with name " + deletedType.getTypeName()
					+" was deleted");
		}
		
		return new StreamingResolution("text/plain","Total deleted " + unuseProdType.size() + " types");
	}

	public List<ProductType> getUnuseProdType() {
		return unuseProdType;
	}

	public void setUnuseProdType(List<ProductType> unuseProdType) {
		this.unuseProdType = unuseProdType;
	}

}
