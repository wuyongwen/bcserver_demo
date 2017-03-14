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
@Table(name = "BC_USER_SESSION")
//@Cacheable
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class Session extends AbstractEntity<Long>{
    private static final long serialVersionUID = -6769787631811675733L;

    private Long userId;
    private String token;
    private User user;
    private SessionStatus status;
    
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
    
    @Column(name = "TOKEN")
    @JsonView(Views.Public.class)        
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", insertable=false, updatable=false)
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    @JsonView(Views.Public.class)    
    public SessionStatus getStatus() {
		return status;
	}
    
	public void setStatus(SessionStatus status) {
		this.status = status;
	}
}
