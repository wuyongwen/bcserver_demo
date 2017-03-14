package com.cyberlink.cosmetic.modules.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.cyberlink.cosmetic.core.model.AbstractEntity;
import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "BC_USER_SUBSCRIBE")
//@Cacheable
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class Subscribe extends AbstractEntity<Long>{
    private static final long serialVersionUID = 2876869626078673443L;

    public enum SubscribeType {
        User, Circle, Normal, CL, Blogger, Expert, Master, Brand, Publisher, Celebrity, LiveBrand, Anchor;
        
        public boolean isUser() {
            return User == this;
        }
        
        public boolean isCircle() {
            return Circle == this;
        }
        
        public static SubscribeType getType(UserType userType) {
        	SubscribeType targetType = SubscribeType.Normal;
        	if (UserType.Anchor.equals(userType))
            	targetType = SubscribeType.Anchor;
            else if (UserType.LiveBrand.equals(userType))
            	targetType = SubscribeType.LiveBrand;
            else if (UserType.Normal.equals(userType))
            	targetType = SubscribeType.Normal;
            else if (UserType.Blogger.equals(userType))
            	targetType = SubscribeType.Blogger;
            else if (UserType.Expert.equals(userType))
            	targetType = SubscribeType.Expert;
            else if (UserType.Master.equals(userType))
            	targetType = SubscribeType.Master;
            else if (UserType.Brand.equals(userType))
            	targetType = SubscribeType.Brand;
            else if (UserType.Publisher.equals(userType))
            	targetType = SubscribeType.Publisher;
            else if (UserType.Celebrity.equals(userType))
            	targetType = SubscribeType.Celebrity;
        	return targetType;
        }
    }
    
    private Long subscriberId;
    private Long subscribeeId;
    private User subscriber;
    private User subscribee;
    private SubscribeType subscribeType;
    private String subscriberName;
    private String subscribeeName;
    
    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "SUBSCRIBER_ID")
    @JsonView(Views.Public.class)            
    public Long getSubscriberId() {
        return subscriberId;
    }
    public void setSubscriberId(Long subscriberId) {
        this.subscriberId = subscriberId;
    }

    @Column(name = "SUBSCRIBEE_ID")
    @JsonView(Views.Public.class)            
    public Long getSubscribeeId() {
        return subscribeeId;
    }
    
    public void setSubscribeeId(Long subscribeeId) {
        this.subscribeeId = subscribeeId;
    }

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBSCRIBER_ID", insertable=false, updatable=false)    
    public User getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(User subscriber) {
		this.subscriber = subscriber;
	}

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBSCRIBEE_ID", insertable=false, updatable=false)
	public User getSubscribee() {
		return subscribee;
	}

	public void setSubscribee(User subscribee) {
		this.subscribee = subscribee;
	}

	
	@Column(name = "SUBSCRIBE_TYPE")
	@Enumerated(EnumType.STRING)
    public SubscribeType getSubscribeType() {
        return subscribeType;
    }
    
    public void setSubscribeType(SubscribeType subscribeType) {
        this.subscribeType = subscribeType;
    }

    @Column(name = "SUBSCRIBER_NAME")
	public String getSubscriberName() {
		return subscriberName;
	}

	public void setSubscriberName(String subscriberName) {
		this.subscriberName = subscriberName;
	}

	@Column(name = "SUBSCRIBEE_NAME")
	public String getSubscribeeName() {
		return subscribeeName;
	}

	public void setSubscribeeName(String subscribeeName) {
		this.subscribeeName = subscribeeName;
	}
}
