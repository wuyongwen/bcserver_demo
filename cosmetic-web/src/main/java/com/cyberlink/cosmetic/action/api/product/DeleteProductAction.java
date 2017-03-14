package com.cyberlink.cosmetic.action.api.product;

import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import net.sourceforge.stripes.action.DefaultHandler;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/product/deleteProduct.action")
public class DeleteProductAction extends AbstractProductAction{
	private Long[] productId;
	
	@SpringBean("product.ProductDao")
	protected ProductDao productDao;
	
	@DefaultHandler
    public Resolution route() {
		if(productId == null){
			return new ErrorResolution(ErrorDef.InvalidProductId);
		}
		for( Long PID : productId ){
			if( !productDao.exists(PID) ){
				return new ErrorResolution(ErrorDef.InvalidProductId);
			}
			productDao.delete(PID);
		}
		
		return success();
	}	

	public Long[] getProductId() {
		return productId;
	}

	public void setProductId(Long[] productId) {
		this.productId = productId;
	}
	
}
