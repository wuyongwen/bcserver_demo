package com.cyberlink.cosmetic.modules.product.model.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.product.dao.BrandDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductCommentDao;
import com.cyberlink.cosmetic.modules.product.model.Brand;
import com.cyberlink.cosmetic.modules.product.model.Product;
import com.cyberlink.cosmetic.modules.product.model.ProductAttribute;
import com.cyberlink.cosmetic.modules.product.model.ProductComment;
import com.cyberlink.cosmetic.modules.product.model.ProductCommentAttr;
import com.cyberlink.cosmetic.modules.product.model.RelProductType;
import com.fasterxml.jackson.annotation.JsonView;

public class ProductWithCommentWrapper implements Serializable {
	private static final long serialVersionUID = -1053626489497368077L;
	/*
	private static ProductCommentDao commentDao = 
			BeanLocator.getBean("product.ProductCommentDao");
	*/
	private final Product product;
	private Long userId ;
	/*private final ProductComment prodComment ;*/
	
	public ProductWithCommentWrapper(Product product, Long userId) {
        this.product = product;
        this.userId = userId ;
        /*this.prodComment = commentDao.findByProductIdAndUserId(userId, product.getId());*/
    }
	
	@JsonView(Views.Public.class)
    public Long getProductId() {
		return product.getId();
	}
	
	@JsonView(Views.Public.class)
	public String getRecommendedStore() {
		return product.getStore().getStoreName();
	}
	
	@JsonView(Views.Public.class)
	public String getProductStoreLink(){
		return product.getProductStoreLink();
	} 

	@JsonView(Views.Public.class)
	public String getRecommendedPrice() {
		if( product.getPriceString() != null ){
			return product.getPriceString() ;
		}
		Float price = product.getPrice();
		if (price != null){
			switch ( product.getLocale() ){
				case "de_DE":
					return "ab EUR " + String.format(Locale.GERMANY,"%,.2f", price ) ;
				case "fr_FR":
					return "à partir de EUR " + String.format(Locale.GERMANY,"%,.2f", price) ;
				case "en_GB":
					return "from \u00A3" + String.format(Locale.UK,"%.2f", price) ;
				case "ja_JP":
					return "\u00A5 " + String.format("%,.0f", price) + "より" ;
				case "zh_CN":
					return "\u00A5" + String.format("%.0f", price) ;
				case "zh_TW":
					return "$" + String.format("%,.0f", price) ;
				case "en_CA":
					return "from CDN$ " + String.format("%.2f", price) ;
				case "en_US":
				default:
					return "from $" + String.format("%.2f", price) ;
			}
		}
		else
			return "";
	}

	@JsonView(Views.Public.class)
	public String getRecommendedPriceCurrency() {
		String region = product.getLocale();
		switch (region){
			case "en_US":
				return "USD" ;
			case "en_CA":
				return "CAD" ;
			case "en_GB":
				return "GBP" ;
			case "ja_JP":
				return "JPY" ;
			case "de_DE":
			case "fr_FR":
				return "EUR" ;
			case "zh_TW":
				return "TWD";
			case "zh_CN":
				return "CNY";
			default:
				return "";	
		}
	}

	@JsonView(Views.Public.class)
	public List<String> getTypeName() {
		List<String> typeList = new ArrayList<String>();
		for (RelProductType pType : product.getRelProductType()){
			typeList.add(pType.getProductType().getTypeName());
		}
		return typeList;
	}
	
	@JsonView(Views.Public.class)
	public String getProductName() {
		return product.getProductName();
	}
	
	@JsonView(Views.Public.class)
	public String getDisplayTitle() {
		return product.getProductTitle();
	}
	
	@JsonView(Views.Public.class)
	public String getDescription() {
		return product.getProductDescription();
	}
	
	@JsonView(Views.Public.class)
	public String getImgThumbnail() {
		return product.getImg_thumbnail();
	}
	
	@JsonView(Views.Public.class)
	public String getImgOriginal() {
		return product.getImg_original();
	}
	
	@JsonView(Views.Public.class)
	public String getTrialOnYCMakeUp() {
		String text = product.getTrialOnYCMakeUp();
		if (text != null)
			return product.getTrialOnYCMakeUp();
		else
			return "";
	}
	
	@JsonView(Views.Public.class)
	public String getBrandName() {
		Brand brand = product.getBrand();
		if (brand == null) {
			return "";
		} else { 
			return product.getBrand().getBrandName();
		}
	}

	@JsonView(Views.Public.class)
	public Long getCommentCount() {//actally "TTL rated count" no change since app team using this name
		return Long.valueOf(product.getProdCommentList().size());
	}

	@JsonView(Views.Public.class)
	public float getRating() {
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
	public String getExtPID(){
		return product.getExtProdID();
	}
	
	@JsonView(Views.Public.class)
	public Boolean getisCommented(){
		//return prodComment != null?Boolean.TRUE:Boolean.FALSE;
		for( ProductComment pComment : product.getProdCommentList() ){
			if( pComment.getUser().getId().longValue() == userId.longValue() ){
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	/*
	public ProductComment getProdComment() {
		return prodComment;
	}
	*/

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
