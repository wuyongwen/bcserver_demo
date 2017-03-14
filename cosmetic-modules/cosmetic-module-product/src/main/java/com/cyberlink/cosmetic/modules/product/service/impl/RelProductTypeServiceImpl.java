package com.cyberlink.cosmetic.modules.product.service.impl;

import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.modules.product.dao.ProductTypeDao;
import com.cyberlink.cosmetic.modules.product.model.RelProductType;
import com.cyberlink.cosmetic.modules.product.dao.RelProductTypeDao;
import com.cyberlink.cosmetic.modules.product.service.RelProductTypeService;

public class RelProductTypeServiceImpl implements RelProductTypeService{

	@SpringBean("product.ProductTypeDao")
	private ProductTypeDao productTypeDao ;
	
	@SpringBean("product.RelProductTypeDao")
	private RelProductTypeDao relProductTypeDao ;
	
	
	public RelProductType createOrUpdate(Long ProdID, long typeID) {
		if(relProductTypeDao.findByProdIDTypeID(ProdID, typeID) != null)//there's alreay existing same prod-ype relation
			return null;
		else
			return create( ProdID, typeID );
	}
	
	public RelProductType create(Long ProdID, long typeID) {
		RelProductType relProductType = new RelProductType();
		relProductType.setProductId(ProdID);
		relProductType.setProductType(productTypeDao.findById(typeID));
		return relProductTypeDao.create(relProductType);
	}

	public void setProductTypeDao(ProductTypeDao productTypeDao){
		this.productTypeDao = productTypeDao;
	}
	
	public ProductTypeDao getProductTypeDao(){
		return this.productTypeDao;
	}
	
	public void setRelProductTypeDao(RelProductTypeDao relProductTypeDao){
		this.relProductTypeDao = relProductTypeDao;
	}
	
	public RelProductTypeDao getRelProductTypeDao(){
		return this.relProductTypeDao;
	} 
}
