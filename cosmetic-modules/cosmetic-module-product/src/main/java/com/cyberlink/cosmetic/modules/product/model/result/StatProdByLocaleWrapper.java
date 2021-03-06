package com.cyberlink.cosmetic.modules.product.model.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.product.dao.StoreDao;
import com.cyberlink.cosmetic.modules.product.model.Brand;
import com.cyberlink.cosmetic.modules.product.model.Product;
import com.cyberlink.cosmetic.modules.product.model.ProductAttribute;
import com.cyberlink.cosmetic.modules.product.model.ProductComment;
import com.cyberlink.cosmetic.modules.product.model.ProductCommentAttr;
import com.cyberlink.cosmetic.modules.product.model.RelProductType;
import com.cyberlink.cosmetic.modules.product.model.Store;
import com.fasterxml.jackson.annotation.JsonView;

public class StatProdByLocaleWrapper implements Serializable {
	private static final long serialVersionUID = -1053626489497368077L;
	private final Product product;
	
	public StatProdByLocaleWrapper(Product product) {
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
	
	//@JsonView(Views.Public.class)
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
	
	//@JsonView(Views.Public.class)
	public Long getRatingCount() {
		return Long.valueOf(product.getProdCommentList().size());
	}

	//@JsonView(Views.Public.class)
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
	
	@JsonView(Views.Public.class)
	public RegionStatistic getRegionStatistic(){
		RegionStatistic result = new RegionStatistic();
		List<ProdRatingCounter> ratingCountList = new ArrayList<ProdRatingCounter> () ;
		List<ProdCommentCounter> commentCountList = new ArrayList<ProdCommentCounter> () ;

		for( ProductComment comment : product.getProdCommentList() ){
			if( comment.getUser().getRegion() == null ){//ignore null region since no meaning to handle this part.
				continue;
			}
			boolean foundSameRegionInRatingList = Boolean.FALSE;
			//add new rating data to list
			for( ProdRatingCounter rCounter : ratingCountList ){
				if( rCounter.getRegion().equals(comment.getUser().getRegion()) ){
					rCounter.setCount(rCounter.getCount() + 1);
					rCounter.setRating(comment.getRating() + rCounter.getRating() );
					foundSameRegionInRatingList = Boolean.TRUE;
				}
			}
			if( !foundSameRegionInRatingList ){
				ProdRatingCounter newRatingCounter = 
						new ProdRatingCounter( comment.getUser().getRegion(), comment.getRating() );
				ratingCountList.add(newRatingCounter) ;
			}
			//add new comment data to list
			boolean foundSameRegionInCommentList = Boolean.FALSE;
			if( comment.getComment().length() >0 ){
				for( ProdCommentCounter cCounter : commentCountList ){
					if( cCounter.getRegion().equals(comment.getUser().getRegion()) ){
						cCounter.setCount(cCounter.getCount() + 1);
						foundSameRegionInCommentList = Boolean.TRUE ;
					}
				}
				if( !foundSameRegionInCommentList ){
					ProdCommentCounter newCommentCounter = 
							new ProdCommentCounter( comment.getUser().getRegion() );
					commentCountList.add(newCommentCounter);
				}
			}
		}
		result.setCommentCount(commentCountList);
		result.setRatingCount(ratingCountList);
		return result;
	}
}
