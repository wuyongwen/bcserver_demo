package com.cyberlink.cosmetic.action.api.user;

import java.util.Calendar;
import java.util.TimeZone;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.notify.dao.NotifyDao;
import com.cyberlink.cosmetic.modules.notify.model.NotifyType;
import com.cyberlink.cosmetic.modules.user.dao.DeviceDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Device;
import com.cyberlink.cosmetic.modules.user.model.DeviceType;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

@UrlBinding("/api/v5.0/user/update-device.action")
public class UpdateDeviceAction_v5 extends AbstractAction {
	@SpringBean("user.DeviceDao")
    private DeviceDao deviceDao;

	@SpringBean("user.UserDao")
    private UserDao userDao;

	@SpringBean("notify.NotifyDao")
	private NotifyDao notifyDao;

    private String apnsType;
    private Long userId;
	
	@Validate(maxlength = 256)
	private String apnsToken;
    
    @Validate(maxlength = 32)
    private String ap;
    
    @Validate(maxlength = 200)
	private String uuid = "";
    
	@DefaultHandler
    public Resolution route() {
		RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
    	if (!authenticate()) {
    		return new ErrorResolution(authError); 
    	} 
    	
    	userId = getCurrentUserId();
    	// Workaround: The correct solution is publishing event from checkNewNotify api.
    	notifyDao.updateUserSendTarget(userId, "OpenApp", NotifyType.getAllWithoutCommentType());
    	
    	DeviceType deviceType = DeviceType.Android;
    	String apnsToken = "";
    	if (this.apnsToken != null)
    		apnsToken = this.apnsToken;
    	String uuid = "";
    	if (this.uuid != null)
    		uuid  = this.uuid;
    	
    	if ((apnsToken.length() == 0 || apnsType == null) && (uuid == null || uuid.length() == 0))
    		return success();
    	
    	if (apnsType != null) {
    		if (apnsType.equalsIgnoreCase("gcm")) {
    			deviceType = DeviceType.Android;
    		} else if (apnsType.equalsIgnoreCase("apns")) {
    			deviceType = DeviceType.iOS;
    		} else {
    			apnsToken = "";
    		}
    	} else {
    		apnsToken = "";
    	}
    	Device device = deviceDao.findDeviceInfo(userId, uuid, deviceType, getAp());
    	if (device == null) {
    		device = new Device();
    		device.setShardId(userId);
    		device.setApnsToken(apnsToken);
    		device.setUserId(userId);
    		device.setDeviceType(deviceType);
    		device.setUuid(uuid);
    		device.setApp(getAp());
        	deviceDao.create(device);    		
    	} else {
    		Calendar cal = Calendar.getInstance();
    		cal.setTimeZone(TimeZone.getTimeZone("GMT+00"));
    		device.setApnsToken(apnsToken);
    		device.setApp(getAp());
    		device.setIsDeleted(Boolean.FALSE);
    		device.setLastModified(cal.getTime());
        	deviceDao.update(device);
    	}

		return success();
	}

	public String getApnsType() {
		return apnsType;
	}

	public void setApnsType(String apnsType) {
		this.apnsType = apnsType;
	}
	
	public String getApnsToken() {
		return apnsToken;
	}

	public void setApnsToken(String apnsToken) {
		this.apnsToken = apnsToken;
	}
    
	public String getAp() {
		return ap;
	}	

	public void setAp(String ap) {
		this.ap = ap;
	}
    
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}	
}