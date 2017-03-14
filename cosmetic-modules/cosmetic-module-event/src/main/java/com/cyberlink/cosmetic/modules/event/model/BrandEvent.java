package com.cyberlink.cosmetic.modules.event.model;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.slf4j.LoggerFactory;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.event.model.Stores.Store;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@Table(name = "BC_BRAND_EVENT")
//@Cacheable
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class BrandEvent extends AbstractCoreEntity<Long>{
	public static class ListBrandEventView extends Views.Public {
    }
    public static class InfoBrandEventView extends ListBrandEventView {
    }
    public static class  InfoBrandEventView_v4 extends InfoBrandEventView {
    }
    public static class  InfoBrandEventView_v4_1 extends InfoBrandEventView {
    }
    
	private static final long serialVersionUID = -638304621617894988L;

	private String locale;
	private Long brandId;
	private String imageUrl;
	private String eventLink;
	private String title;
	private String description;
	private String attribute;
	private Long quantity = 0L;
	private Long joinNum = 0L;

	private String prodName;
	private String prodDescription;
	private String prodDetail;
	private String prodAttribute;
	
	private ApplyType applyType;
	private EventType eventType;
	private ReceiveType receiveType;
	private String receiveTemplate;
	private String storesValue;
	private List<Stores> stores;
	private String storeAddress;
	private String comment;
	private String pipedaLink;
	private Integer priority;
	private ServiceType serviceType;
	private EventAttr eventAttr;
	private ProductAttr productAttr;
	private String notifyTime;
	private String couponCode;
	private String websiteUrl;
	private String metadata;
	
	private Double apiVersion = null;
	
	@Transient
	private EventUserStatus userStatus = EventUserStatus.NonJoin;
	
	@Transient
    private Boolean isFollowed = Boolean.FALSE;
	
	
	@Id
    @GenericGenerator(name = "customeIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.CustomizedIdGenerator")
    @GeneratedValue(generator = "customeIdGenerator")
    @JsonView(Views.Public.class)
    @Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
        return id;
    }    
	
	@JsonView(Views.Public.class)
	@Column(name = "BRAND_ID")	
    public Long getBrandId() {
		return brandId;
	}


	public void setBrandId(Long brandId) {
		this.brandId = brandId;
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
	@Column(name = "IMAGE_URL")	
	public String getImageUrl() {
		return imageUrl;
	}


	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@JsonView(Views.Public.class)
	@Column(name = "EVENT_LINK")	
	public String getEventLink() {
		return eventLink;
	}


	public void setEventLink(String eventLink) {
		this.eventLink = eventLink;
	}

	@JsonView(InfoBrandEventView.class)
	@Column(name = "TITLE")	
	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}

	@JsonView(InfoBrandEventView.class)
	@Column(name = "DESCRIPTION")	
	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}
	
    @Column(name = "ATTRIBUTE")   
    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }	

    @Transient 
	@JsonView(Views.Public.class)
	public Date getStartTime() {
		EventAttr attr = getEventAttrJNode();
		if (attr != null) 
			return attr.getStartTime();
		return null;
	}

	@Transient 
	@JsonView(Views.Public.class)
	public Date getEndTime() {
		EventAttr attr = getEventAttrJNode();
		if (attr != null) 
			return attr.getEndTime();
		return null;
	}
	
	@Transient 
    public Date getDrawTime() {
		EventAttr attr = getEventAttrJNode();
		if (attr != null) 
			return attr.getDrawTime();
        return null;
    }
    
	@JsonView(Views.Public.class)
	@Column(name = "QUANTITY")
	public Long getQuantity() {
		return quantity;
	}


	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	@JsonView(Views.Public.class)
	@Column(name = "JOIN_NUM")
	public Long getJoinNum() {
		return joinNum;
	}


	public void setJoinNum(Long joinNum) {
		this.joinNum = joinNum;
	}

	@JsonView(InfoBrandEventView.class)
	@Column(name = "PROD_NAME")
	public String getProdName() {
		return prodName;
	}


	public void setProdName(String prodName) {
		this.prodName = prodName;
	}

	@JsonView(InfoBrandEventView.class)
	@Column(name = "PROD_DESCRIPTION")
	public String getProdDescription() {
		return prodDescription;
	}


	public void setProdDescription(String prodDescription) {
		this.prodDescription = prodDescription;
	}

	@JsonView(InfoBrandEventView.class)
	@Column(name = "PROD_DETAIL")
	public String getProdDetail() {
		return prodDetail;
	}


	public void setProdDetail(String prodDetail) {
		this.prodDetail = prodDetail;
	}
	
	@JsonView(InfoBrandEventView.class)
	@Column(name = "PROD_ATTR")
	public String getProdAttribute() {
		return prodAttribute;
	}

	public void setProdAttribute(String prodAttribute) {
		this.prodAttribute = prodAttribute;
	}

	@JsonView(InfoBrandEventView.class)
	@Enumerated(EnumType.STRING)
	@Column(name = "APPLY_TYPE")
	public ApplyType getApplyType() {
		return applyType;
	}

	public void setApplyType(ApplyType applyType) {
		this.applyType = applyType;
	}
	
	@JsonView(Views.Public.class)
	@Enumerated(EnumType.STRING)
	@Column(name = "EVENT_TYPE")
	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	@JsonView(InfoBrandEventView.class)
	@Enumerated(EnumType.STRING)
	@Column(name = "RECEIVE_TYPE")
	public ReceiveType getReceiveType() {
		if (apiVersion != null && apiVersion < 4.6 && ReceiveType.Coupon.equals(receiveType)) {
			return ReceiveType.Home;
		}
		return receiveType;
	}

	public void setReceiveType(ReceiveType receiveType) {
		this.receiveType = receiveType;
	}

	@JsonView(InfoBrandEventView.class)
	@Column(name = "RECEIVE_TEMPLATE")
	public String getReceiveTemplate() {
		return receiveTemplate;
	}

	public void setReceiveTemplate(String receiveTemplate) {
		this.receiveTemplate = receiveTemplate;
	}
	
	@Column(name = "STORES")
	public String getStoresValue() {
		return storesValue;
	}

	public void setStoresValue(String storesValue) {
		this.storesValue = storesValue;
	}
	
	@JsonView(InfoBrandEventView.class)
	@Column(name = "COMMENT")
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
    
    @JsonView(InfoBrandEventView.class)
    @Column(name = "PIPEDA_LINK")
    public String getPipedaLink() {
        return pipedaLink;
    }

    public void setPipedaLink(String pipedaLink) {
        this.pipedaLink = pipedaLink;
    }
    
    @JsonView(Views.Public.class)
	@Column(name = "PRIORITY")
	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	@JsonView(Views.Public.class)
	@Enumerated(EnumType.STRING)
	@Column(name = "SERVICE_TYPE")
	public ServiceType getServiceType() {
		return serviceType;
	}

	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}
	
	@Column(name = "NOTIFY_TIME")
	public String getNotifyTime() {
		return notifyTime;
	}

	public void setNotifyTime(String notifyTime) {
		this.notifyTime = notifyTime;
	}

	@Column(name = "COUPON_CODE")
	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	@Column(name = "WEBSITE_URL")
	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	@JsonView(Views.Public.class)
	@Column(name = "META_DATA")
	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}
	
	@Transient 
    @JsonView(ListBrandEventView.class)
	public EventUserStatus getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(EventUserStatus userStatus) {
		this.userStatus = userStatus;
	}
	
	@Transient 
    @JsonView(InfoBrandEventView.class)
	public Boolean getIsFollowed() {
		return isFollowed;
	}

	public void setIsFollowed(Boolean isFollowed) {
		this.isFollowed = isFollowed;
	}

	@Transient 
	public EventAttr getEventAttrJNode() {
	    if(eventAttr != null)
	        return eventAttr;
	    try {
            ObjectMapper m = BeanLocator.getBean("web.objectMapper");
            eventAttr = m.readValue(attribute, EventAttr.class);
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("", e);
        }
        return eventAttr;
	}
	
	public void setEventAttrJNode(EventAttr eventAttr) {
	    this.eventAttr = eventAttr;
	    ObjectMapper m = BeanLocator.getBean("web.objectMapper");
        try {
            attribute = m.writer((PrettyPrinter)null).withView(Views.Public.class).writeValueAsString(eventAttr);
        } catch (JsonProcessingException e) {
            LoggerFactory.getLogger(getClass()).error("", e);
        }
	}
	
	@Transient 
	public ProductAttr getProductAttrJNode() {
	    if(productAttr != null)
	        return productAttr;
	    try {
            ObjectMapper m = BeanLocator.getBean("web.objectMapper");
            productAttr = m.readValue(prodAttribute, ProductAttr.class);
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("", e);
        }
        return productAttr;
	}
	
	public void setProductAttrJNode(ProductAttr productAttr) {
	    this.productAttr = productAttr;
	    ObjectMapper m = BeanLocator.getBean("web.objectMapper");
        try {
        	prodAttribute = m.writer((PrettyPrinter)null).withView(Views.Public.class).writeValueAsString(productAttr);
        } catch (JsonProcessingException e) {
            LoggerFactory.getLogger(getClass()).error("", e);
        }
	}
	
	@Transient 
	@JsonView(InfoBrandEventView.class)
	public String getApplyDesc() {
		EventAttr attr = getEventAttrJNode();
		if (attr != null) 
			return attr.getApplyDesc();
		return null;
	}
	
	@Transient 
	@JsonView(InfoBrandEventView.class)
	public String getOrganizerName() {
		EventAttr attr = getEventAttrJNode();
		if (attr != null)
			return attr.getOrganizerName();
		return null;
	}
	
	@Transient 
	@JsonView(InfoBrandEventView.class)
	public String getOrganizerLogo() {
		EventAttr attr = getEventAttrJNode();
		if (attr != null)
			return attr.getOrganizerLogo();
		return null;
	}
	
	@Transient 
	@JsonView(InfoBrandEventView_v4_1.class)
	public List<Stores> getStores() {
	    if(storesValue == null || storesValue.length() <= 0)
            return null;
	    if(stores != null)
	        return stores;
	    try {
            ObjectMapper m = BeanLocator.getBean("web.objectMapper");
            stores = m.readValue(storesValue, new TypeReference<List<Stores>>() {});
        }
        catch(Exception e) {
        }
	    return stores;
	}
	
	@Transient 
	@JsonView(InfoBrandEventView_v4.class)
    public String getStoreAddress() {
	    if(storeAddress != null)
	        return storeAddress;
	    List<Stores> tmp = getStores();
	    if(tmp == null)
            return null;
	    
	    try {
    	    ObjectMapper m = BeanLocator.getBean("web.objectMapper");
    	    Map<String, List<Store>> tmpMap = new LinkedHashMap<String, List<Store>>();
    	    for(Stores t : tmp) {
	            tmpMap.put(t.getCity(), t.getStores());
    	    }
    	    storeAddress = m.writer((PrettyPrinter)null).withView(Views.Public.class).writeValueAsString(tmpMap);
	    }
	    catch(Exception e) {
	    }
        return storeAddress;
    }

	public void setApiVersion(Double apiVersion) {
		this.apiVersion = apiVersion;
	}
	
	@Transient 
	public String getProdDesc() {
		if (prodDescription == null || prodDescription.isEmpty())
			return "";
		
		try {
			return prodDescription.replaceAll("\r\n", "<br>").replaceAll("\n", "<br>");
		} catch (Exception e) {
			return "";
		}
	}
}
