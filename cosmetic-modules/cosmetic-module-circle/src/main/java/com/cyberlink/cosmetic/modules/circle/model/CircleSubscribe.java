package com.cyberlink.cosmetic.modules.circle.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.cyberlink.cosmetic.core.model.AbstractEntity;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "BC_CIRCLE_SUBSCRIBE")
@DynamicUpdate
public class CircleSubscribe extends AbstractEntity<Long>{

    private static final long serialVersionUID = 5462727348183576134L;
    
    private Long userId;
    private Long circleId;
    private User user;
    private Circle circle;
    
    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "USER_ID")
    @JsonView(Views.Public.class)            
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "CIRCLE_ID")
    @JsonView(Views.Public.class)            
    public Long getCircleId() {
        return circleId;
    }
    
    public void setCircleId(Long circleId) {
        this.circleId = circleId;
    }

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", insertable=false, updatable=false)    
    public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CIRCLE_ID", insertable=false, updatable=false)    
    public Circle getCircle() {
        return circle;
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
    }
	
}
