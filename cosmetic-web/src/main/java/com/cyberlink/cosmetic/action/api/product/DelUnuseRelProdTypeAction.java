package com.cyberlink.cosmetic.action.api.product;

import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.product.dao.RelProductTypeDao;
import com.cyberlink.cosmetic.modules.product.model.RelProductType;

@UrlBinding("/api/product/delUnuseRelProdType.action")
public class DelUnuseRelProdTypeAction extends AbstractAction{
	
	@SpringBean("product.ProductDao")
	protected ProductDao productDao;
	
	@SpringBean("product.RelProductTypeDao")
	protected RelProductTypeDao relProductTypeDao ;

	@DefaultHandler
    public Resolution route() {
		List<RelProductType> relProdTypeList = relProductTypeDao.findAll() ;
		for( RelProductType relProdType : relProdTypeList ){
			if( !productDao.exists(relProdType.getProductId()) ){
				relProductTypeDao.delete(relProdType);
			}
		}
		return success();
	}
}
