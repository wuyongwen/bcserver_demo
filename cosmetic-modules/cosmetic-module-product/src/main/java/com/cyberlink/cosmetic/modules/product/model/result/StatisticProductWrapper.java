package com.cyberlink.cosmetic.modules.product.model.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.product.model.Brand;
import com.cyberlink.cosmetic.modules.product.model.Product;
import com.cyberlink.cosmetic.modules.product.model.ProductAttribute;
import com.cyberlink.cosmetic.modules.product.model.ProductComment;
import com.cyberlink.cosmetic.modules.product.model.ProductCommentAttr;
import com.cyberlink.cosmetic.modules.product.model.RelProductType;
import com.fasterxml.jackson.annotation.JsonView;

public class StatisticProductWrapper implements Serializable {
	private static final long serialVersionUID = -1053626489497368077L;

	private final Product product;
	
	public StatisticProductWrapper(Product product) {
        this.product = product;
    }
	
	@JsonView(Views.Public.class)
	public String getLocale(){
		return product.getLocale();
	}
	
	@JsonView(Views.Public.class)
    public Long getProductId() {
		return product.getId();
	}
	
	@JsonView(Views.Public.class)
	public String getStore() {
		return product.getStore().getStoreName();
	}

	@JsonView(Views.Public.class)
	public String getProductName() {
		return product.getProductTitle();
	}
	
	@JsonView(Views.Public.class)
	public int getCommentCount(){
		List<ProductComment> pCommentList = product.getProdCommentList() ;
		if( pCommentList.size() == 0 ){
			return 0;
		}
		else{
			int commentCount = 0;
			for( ProductComment pComment : pCommentList ){
				if( pComment.getComment().length() > 0 ){
					commentCount++;
				}
			}
			return commentCount;
		}
	}
	
	@JsonView(Views.Public.class)
	public Long getRatingCount() {
		return Long.valueOf(product.getProdCommentList().size());
	}

	@JsonView(Views.Public.class)
	public float getAverageRating() {
		float ratedValueSum = Float.valueOf(0);
		Long ratedCount = Long.valueOf(product.getProdCommentList().size());
		if( ratedCount.longValue() == 0 )
			return Float.valueOf(0) ;
		for( ProductComment pComment : product.getProdCommentList() ){
			ratedValueSum += pComment.getRating();
		}
		return ratedValueSum / ratedCount ;
	}
}
