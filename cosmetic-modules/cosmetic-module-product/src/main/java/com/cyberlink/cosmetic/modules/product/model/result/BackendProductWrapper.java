package com.cyberlink.cosmetic.modules.product.model.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.product.model.BackendProduct;
import com.cyberlink.cosmetic.modules.product.model.Brand;
import com.cyberlink.cosmetic.modules.product.model.Product;
import com.cyberlink.cosmetic.modules.product.model.ProductCommentAttr;
import com.cyberlink.cosmetic.modules.product.model.RelProductType;
import com.cyberlink.cosmetic.modules.user.dao.AttributeDao;
import com.cyberlink.cosmetic.modules.user.model.Attribute;
import com.cyberlink.cosmetic.modules.user.model.AttributeType;
import com.fasterxml.jackson.annotation.JsonView;

public class BackendProductWrapper implements Serializable {
	private static final long serialVersionUID = -1053626489497368077L;
    private static AttributeDao attributeDao = BeanLocator
            .getBean("user.AttributeDao");

	private final BackendProduct product;
	
	public BackendProductWrapper(BackendProduct product) {
        this.product = product;
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
	public float getRecommendedPrice() {
		Float price = product.getPrice();
		if (price != null)
			return product.getPrice().floatValue();
		else
			return 0;
	}

	@JsonView(Views.Public.class)
	public String getRecommendedPriceCurrency() {
		String region = product.getLocale();
		if (region == null)
			return "";
		else if (region.equalsIgnoreCase("zh_CN")) {
			return "CNT";
		}
		else if (region.equalsIgnoreCase("zh_TW")) {
			return "NT";
		}
		return "USD";
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
	public Long getCommentCount() {
		List<Attribute> attr = attributeDao.findByRefIdAndNames(AttributeType.Product, 
				product.getId(), ProductCommentAttr.RATING_COUNT);
		if (attr.size() == 0) {
			return Long.valueOf(0);
		}
		return Long.valueOf(attr.get(0).getAttrValue());
	}

	@JsonView(Views.Public.class)
	public float getRating() {
		List<Attribute> attrList = attributeDao.findByRefIdAndNames(AttributeType.Product, 
				product.getId(), ProductCommentAttr.RATING_COUNT, ProductCommentAttr.RATING_VALUE);
		if (attrList.size() != 2) {
			return 0;
		}
		Long count = Long.valueOf(0);
		Float value = Float.valueOf(0);

		for (Attribute attr : attrList) {
			if (attr.getAttrName().equals(ProductCommentAttr.RATING_COUNT)){
				count = Long.valueOf(attr.getAttrValue());
			} else if (attr.getAttrName().equals(ProductCommentAttr.RATING_VALUE)) {
				value = Float.valueOf(attr.getAttrValue());
			} 
		}
		if (count == 0) {
			return 0;
		}
		Float rating =  Float.valueOf(value /Float.valueOf(count));
		if (rating > 5.0)
			return Float.valueOf(5);
		else 
			return rating;
	}
	
	@JsonView(Views.Public.class)
	public String getExtPID(){
		return product.getExtProdID();
	}
	
	@JsonView(Views.Public.class)
	public Boolean getIsDeleted(){
		return product.getIsDeleted();
	}
}
