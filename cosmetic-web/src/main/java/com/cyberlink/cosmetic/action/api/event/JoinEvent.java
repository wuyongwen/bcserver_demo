package com.cyberlink.cosmetic.action.api.event;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.event.dao.BrandEventDao;
import com.cyberlink.cosmetic.modules.event.dao.EventUserDao;
import com.cyberlink.cosmetic.modules.event.model.BrandEvent;
import com.cyberlink.cosmetic.modules.event.model.EventUser;
import com.cyberlink.cosmetic.modules.event.model.EventType;
import com.cyberlink.cosmetic.modules.event.model.EventUserStatus;
import com.cyberlink.cosmetic.modules.event.model.ReceiveType;
import com.cyberlink.cosmetic.modules.event.model.ServiceType;
import com.cyberlink.cosmetic.modules.mail.service.MailJoinEventConsultationService;
import com.cyberlink.cosmetic.modules.mail.service.MailJoinEventCouponService;
import com.cyberlink.cosmetic.modules.mail.service.MailJoinEventHomeService;
import com.cyberlink.cosmetic.modules.mail.service.MailJoinEventStoreService;
import com.cyberlink.cosmetic.modules.notify.service.NotifyService;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.SessionStatus;
import com.restfb.json.JsonArray;

@UrlBinding("/api/event/join-event.action")
public class JoinEvent extends AbstractAction {
	
	@SpringBean("core.jdbcTemplate")
    private TransactionTemplate transactionTemplate;
	
	@SpringBean("user.UserDao")
    private UserDao userDao;
	
	@SpringBean("event.BrandEventDao")
	private BrandEventDao brandEventDao;
	
	@SpringBean("event.EventUserDao")
	private EventUserDao eventUserDao;
	
	@SpringBean("mail.mailJoinEventHomeService")
	private MailJoinEventHomeService mailJoinEventHomeService;
	
	@SpringBean("mail.mailJoinEventStoreService")
	private MailJoinEventStoreService mailJoinEventStoreService;
	
	@SpringBean("mail.mailJoinEventCouponService")
	private MailJoinEventCouponService mailJoinEventCouponService;
	
	@SpringBean("mail.mailJoinEventConsultationService")
	private MailJoinEventConsultationService mailJoinEventConsultationService;
	
	@SpringBean("notify.NotifyService")
    private NotifyService notifyService;
	
	private Long brandEventId;
	private String name;
	private String phone;
	private String address;
	private String email;
	
	private Boolean bSendMail = Boolean.FALSE;
	private EventUser eventUser = null;
	private BrandEvent brandEvent  = null;
	
	public Long getBrandEventId() {
		return brandEventId;
	}
	public void setBrandEventId(Long brandEventId) {
		this.brandEventId = brandEventId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@DefaultHandler
    public Resolution route() {  		
		RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
		
		if (!authenticate()) {
    		return new ErrorResolution(authError); 
    	} else if (getSession().getStatus() == SessionStatus.Invalied)
        	return new ErrorResolution(ErrorDef.AccountEmailDeleted);
			
		if (!brandEventDao.exists(brandEventId))
			return new ErrorResolution(ErrorDef.InvalidBrandEventId);	
		brandEvent = brandEventDao.findById(brandEventId);
		
		// Coupon only need name & email.
		if (ReceiveType.Coupon.equals(brandEvent.getReceiveType())) {
			if ((name == null || email == null))
				return new ErrorResolution(ErrorDef.InvalidJoinUser);
		} else if (name == null || phone == null || address == null || email == null) {
			return new ErrorResolution(ErrorDef.InvalidJoinUser);
		}
		
		
		Resolution resolution = null;
		try {
			resolution = transactionTemplate.execute(new TransactionCallback<Resolution>() {
				@Override
				public Resolution doInTransaction(TransactionStatus status) {
					eventUser = eventUserDao.findByUserIdAndEventId(getCurrentUserId(), brandEventId, null);
					if (eventUser != null && !eventUser.getUserStatus().equals(EventUserStatus.NonJoin))
						return new ErrorResolution(ErrorDef.DuplicatedJoinEvent);
					if (brandEvent != null && brandEvent.getEventType() != null) {
						Long joinNum = brandEvent.getJoinNum();
						if (brandEvent.getEventType().equals(EventType.LimitProdNum) && brandEvent.getQuantity() <= joinNum)
							return new ErrorResolution(ErrorDef.OutOfStock);
						joinNum++;
						brandEvent.setJoinNum((long) (joinNum));
						brandEvent = brandEventDao.update(brandEvent);
						if (!brandEvent.getJoinNum().equals(joinNum))
							throw new UndeclaredThrowableException(new Exception(), "doInTransaction threw undeclared checked exception");
						eventUser = new EventUser();
						eventUser.setUserId(getCurrentUserId());
						eventUser.setEventId(brandEventId);
						eventUser.setName(name);
						eventUser.setAddress(address);
						eventUser.setPhone(phone);
						eventUser.setMail(email);
						if (brandEvent.getEventType().equals(EventType.LimitProdNum)) {
							eventUser.setUserStatus(EventUserStatus.Selected);
							// set Coupon Code
							if (ReceiveType.Coupon.equals(brandEvent.getReceiveType())) {
								String codeStr = brandEvent.getCouponCode();
								try {
									JsonArray couponCode = new JsonArray(codeStr);
									eventUser.setCode(couponCode.getString((int) (joinNum - 1)));
								} catch (Exception e) {
									return new ErrorResolution(ErrorDef.OutOfStock);
								}
							}
						}
						else
							eventUser.setUserStatus(EventUserStatus.Joined);
						eventUser = eventUserDao.create(eventUser);	
						if (eventUser == null || !eventUserDao.exists(eventUser.getId()))
							throw new UndeclaredThrowableException(new Exception(), "doInTransaction threw undeclared checked exception");
						else {
							if (brandEvent.getEventType().equals(EventType.LimitProdNum))
								bSendMail = Boolean.TRUE;
							return success();	
						}
					}
					return new ErrorResolution(ErrorDef.InvalidBrandEventId);	
				}			
			});
			
		} catch (Exception e) {
			return new ErrorResolution(ErrorDef.ServerBusy);	
		}
		if (resolution == null)
			return new ErrorResolution(ErrorDef.ServerBusy);
		
		if (bSendMail) {
			try {
				notifyService.sendEventNotify(Arrays.asList(eventUser), brandEvent);
				if (brandEvent.getServiceType().equals(ServiceType.CONSULTATION))
					mailJoinEventConsultationService.send(eventUser.getId(), brandEvent.getId());
				else if (brandEvent.getReceiveType().equals(ReceiveType.Home))
					mailJoinEventHomeService.send(eventUser.getId(), brandEvent.getId());
				else if (brandEvent.getReceiveType().equals(ReceiveType.Store))
					mailJoinEventStoreService.send(eventUser.getId(), brandEvent.getId());
				else if (brandEvent.getReceiveType().equals(ReceiveType.Coupon))
					mailJoinEventCouponService.send(eventUser.getId(), brandEvent.getId());
					
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return resolution;
	}
}