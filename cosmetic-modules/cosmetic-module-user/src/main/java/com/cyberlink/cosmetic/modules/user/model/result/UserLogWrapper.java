package com.cyberlink.cosmetic.modules.user.model.result;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.user.dao.DeviceDao;
import com.cyberlink.cosmetic.modules.user.model.GenderType;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;

public class UserLogWrapper implements Serializable {
	private static final long serialVersionUID = -5526826966656223086L;
	private static DeviceDao deviceDao = 
			BeanLocator.getBean("user.DeviceDao");

	private final User user;
	
	public UserLogWrapper(User user) {
        this.user = user;
    }
	
	@JsonView(Views.Public.class)
    public Long getId() {
		return user.getId();
	}
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss", timezone="GMT+00")
	@JsonView(Views.Public.class)
	public Date getCreatedTime() {
		return user.getCreatedTime();
	}
	
	@JsonView(Views.Public.class)
	public String getDisplayName(){
		return user.getDisplayName();
	} 

	@JsonView(Views.Public.class)
	public GenderType getGender() {
		return user.getGender();
	}
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="GMT+00")
	@JsonView(Views.Public.class)
	public Date getBirthDay() {
		return user.getBirthDay();
	}

	@JsonView(Views.Public.class)
	public UserType getUserType() {
		return user.getUserType();
	}

	@JsonView(Views.Public.class)
	public String getRegion() {
		return user.getRegion();
	}

	@JsonView(Views.Public.class)
	public String getAttribute() {
		return user.getAttribute();
	}

	@JsonView(Views.Public.class)
	public List<String> getDeviceIds() {
		return deviceDao.findByUserId(user.getId());
	}

}
