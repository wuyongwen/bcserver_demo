package com.cyberlink.cosmetic.modules.user.model;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.cyberlink.core.model.AbstractMongoEntity;
import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

@Document(collection = "BC_USER_SUBSCRIBE")
public class TestSubscribe extends AbstractMongoEntity<String>{
	private static final long serialVersionUID = 7129560470627925554L;

	private Long subscriberId;
    private Long subscribeeId;
    
	@Id
    @Field("_id")
    @JsonView(Views.Public.class)
    public String getId() {
    	return id;
    }
    
    @Field("subscriberId")
    @JsonView(Views.Public.class)
	public Long getSubscriberId() {
		return subscriberId;
	}
	public void setSubscriberId(Long subscriberId) {
		this.subscriberId = subscriberId;
	}

	@Field("subscribeeId")
    @JsonView(Views.Public.class)
	public Long getSubscribeeId() {
		return subscribeeId;
	}
	
	public void setSubscribeeId(Long subscribeeId) {
		this.subscribeeId = subscribeeId;
	}	
}
