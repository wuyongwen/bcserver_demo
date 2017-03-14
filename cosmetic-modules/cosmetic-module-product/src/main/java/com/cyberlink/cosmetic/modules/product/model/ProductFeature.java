package com.cyberlink.cosmetic.modules.product.model;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.slf4j.LoggerFactory;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.utils.AppVersion;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@Table(name = "BC_PRODUCT_FEATURE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class ProductFeature extends AbstractCoreEntity<Long>{

	private static final long serialVersionUID = 6602127187477344520L;

	public static class InApiView extends Views.Public {
    }
	
    private Long userId;
    private User user;
    private String locale;
	private String extProductId;
	private Long productIndex;
	private String productType;
	private Long typeIndex;
	private String productTitle;
	private String productDescription;
	private String imgOriginal;
	private String imgUrl;
	private Float price;
	private String priceString ;
	private Date startDate;
	private Date endDate;
	private String metadataValue;
	private ProductFeatureMetadata metadata;
	private Long version;

    @Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @Column(name = "ID", unique = true, nullable = false)
	@JsonView(Views.Public.class)   
	public Long getId() {
		return id;
	}
	
	@Column(name = "USER_ID")
    @JsonView(Views.Public.class)   
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", insertable=false, updatable=false)
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }    
    
    @Column(name = "LOCALE")
    @JsonView(Views.Public.class)   
    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
    
	@Column(name = "EXT_PRODUCT_ID")
	@JsonView(InApiView.class)   
    public String getExtProductId() {
        return extProductId;
    }

    public void setExtProductId(String extProductId) {
        this.extProductId = extProductId;
    }

    @Column(name = "PRODUCT_INDEX")
    @JsonView(InApiView.class)   
    public Long getProductIndex() {
        return productIndex;
    }

    public void setProductIndex(Long productIndex) {
        this.productIndex = productIndex;
    }

    @Column(name = "PRODUCT_TYPE") 
	@JsonView(Views.Public.class)   
    public String getProductType() {
        return productType;
    }
    
    public void setProductType(String productType) {
        this.productType = productType;
    }
    
    @Column(name = "TYPE_INDEX")
    @JsonView(InApiView.class)   
    public Long getTypeIndex() {
        return typeIndex;
    }

    public void setTypeIndex(Long typeIndex) {
        this.typeIndex = typeIndex;
    }
    
    @Column(name = "PRODUCT_TITLE")	
	@JsonView(Views.Public.class)   
	public String getProductTitle() {
		return productTitle;
	}
	
	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

    @Column(name = "PRODUCT_DESCRIPTION")
    @JsonView(Views.Public.class)   
	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

    @Column(name = "IMG_ORIGINAL")
    @JsonView(Views.Public.class)   
	public String getImgOriginal() {
		return imgOriginal;
	}

	public void setImgOriginal(String imgOriginal) {
		this.imgOriginal = imgOriginal;
	}

    @Column(name = "IMG_URL")
    @JsonView(Views.Public.class)   
	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

    @Column(name = "PRICE")	
    @JsonView(InApiView.class)   
	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}	

	@Column(name = "PRICE_STRING")
    public String getPriceString() {
        return priceString;
    }

    public void setPriceString(String priceString) {
        this.priceString = priceString;
    }
	
    @Column(name = "START_DATE")  
    @JsonView(InApiView.class)   
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Column(name = "END_DATE")  
    @JsonView(InApiView.class)   
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
	@Column(name = "META_DATA")  
	@JsonView(InApiView.class)   
    public String getMetadataValue() {
        return metadataValue;
    }

    public void setMetadataValue(String metadataValue) {
        this.metadataValue = metadataValue;
    }

    @Column(name = "VERSION")  
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
    
    @Transient
    @JsonView(InApiView.class)   
    public String getAppVersion() {
        return AppVersion.getAppVersion(version);
    }
    
    @Transient
    @JsonView(Views.Public.class)   
    public ProductFeatureMetadata getMetadata() {
        if(metadata != null)
            return metadata;
        if (StringUtils.isBlank(metadataValue))
            return null;
      
        try {
            ObjectMapper m = BeanLocator.getBean("web.objectMapper");
            ProductFeatureMetadata result = m.readValue(metadataValue,
                    new TypeReference<ProductFeatureMetadata>() {
                    });
            return result;
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("", e);
        }
        return null;
    }

}
