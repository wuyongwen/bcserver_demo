package com.cyberlink.cosmetic.modules.product.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;


@Entity
@Table(name = "BC_PRODUCT")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class BackendProduct extends AbstractCoreEntity<Long>{
	private static final long serialVersionUID = 5004392607353194873L;

	private Long id;
	private String locale;
	private Long typeGroupId;
	private String productName;
	private String productTitle;
	private String productDescription;
	private String img_thumbnail;
	private String img_original;
	private Long barCode;
	private String TrialOnYCMakeUp;
	private Brand brand;
	private Store store;
	private String productStoreLink ;
	private Float price;
	private List<RelProductType> relProductType = new ArrayList<RelProductType>();
	private List <ProductProductEffect> productProductEffect = new ArrayList<ProductProductEffect>(0);
	private String extProdID;
	private boolean OnShelf;
	private StorePriceRange priceRange;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)   
    @Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
    @JoinColumn(name = "BRAND_ID")
	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_ID")
	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	@JsonView(Views.Public.class)   
    @Column(name = "TYPEGROUP_ID")	
	public Long getTypeGroupId() {
		return typeGroupId;
	}
	
	public void setTypeGroupId(Long typeGroupId) {
		this.typeGroupId = typeGroupId;
	}

	@JsonView(Views.Public.class)   
    @Column(name = "PRODUCT_NAME")	
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	@JsonView(Views.Public.class)   
    @Column(name = "PRODUCT_TITLE")	
	public String getProductTitle() {
		return productTitle;
	}
	
	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	@JsonView(Views.Public.class)   
    @Column(name = "PRODUCT_DESCRIPTION")	
	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	@JsonView(Views.Public.class)   
    @Column(name = "IMG_THUMBNAIL")	
	public String getImg_thumbnail() {
		return img_thumbnail;
	}

	public void setImg_thumbnail(String img_thumbnail) {
		this.img_thumbnail = img_thumbnail;
	}

	@JsonView(Views.Public.class)   
    @Column(name = "IMG_ORIGINAL")	
	public String getImg_original() {
		return img_original;
	}

	public void setImg_original(String img_original) {
		this.img_original = img_original;
	}

	@JsonView(Views.Public.class)   
    @Column(name = "BARCODE")	
	public Long getBarCode() {
		return barCode;
	}

	public void setBarCode(Long barCode) {
		this.barCode = barCode;
	}

	@JsonView(Views.Public.class)   
    @Column(name = "TRIAL_ON_YMK")	
	public String getTrialOnYCMakeUp() {
		return TrialOnYCMakeUp;
	}
	public void setTrialOnYCMakeUp(String trialOnYCMakeUp) {
		TrialOnYCMakeUp = trialOnYCMakeUp;
	}

	@JsonView(Views.Public.class)   
    @Column(name = "PRICE")	
	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}	

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "productId", cascade={CascadeType.ALL})
    @JsonView(Views.Public.class)
	public List<RelProductType> getRelProductType() {
		return relProductType;
	}

	public void setRelProductType(List<RelProductType> relProductType) {
		this.relProductType = relProductType;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
    @JsonView(Views.Public.class)
    public List<ProductProductEffect> getProductProductEffect() {
        return productProductEffect;
    }

    public void setProductProductEffect(List<ProductProductEffect> productProductEffect) {
        this.productProductEffect = productProductEffect;
    }

	@JsonView(Views.Public.class)   
    @Column(name = "LOCALE")
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	@JsonView(Views.Public.class)   
    @Column(name = "PROD_STORE_LINK")	
	public String getProductStoreLink() {
		return productStoreLink;
	}

	public void setProductStoreLink(String productStoreLink) {
		this.productStoreLink = productStoreLink;
	}

    @Column(name = "EXT_PROD_ID")	
	public String getExtProdID() {
		return extProdID;
	}

	public void setExtProdID(String extProdID) {
		this.extProdID = extProdID;
	}

    @Column(name = "ON_SHELF")
	public boolean getOnShelf() {
		return OnShelf;
	}

	public void setOnShelf(boolean onShelf) {
		OnShelf = onShelf;
	}

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRICE_RANGE_ID")
	public StorePriceRange getPriceRange() {
		return priceRange;
	}

	public void setPriceRange(StorePriceRange priceRange) {
		this.priceRange = priceRange;
	}

	
	
}
