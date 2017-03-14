package com.cyberlink.cosmetic.action.api.event;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.text.SimpleDateFormat;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.lang.LanguageCenter;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.event.dao.BrandEventDao;
import com.cyberlink.cosmetic.modules.event.dao.EventUserDao;
import com.cyberlink.cosmetic.modules.event.model.BrandEvent;
import com.cyberlink.cosmetic.modules.event.model.BrandEventStatus;
import com.cyberlink.cosmetic.modules.event.model.EventAttr;
import com.cyberlink.cosmetic.modules.event.model.EventUserStatus;
import com.cyberlink.cosmetic.modules.event.model.ServiceType;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;

@UrlBinding("/api/event/view-consultation-detail.action")
public class ViewConsultationDetail extends AbstractAction {
	
	@SpringBean("event.BrandEventDao")
	private BrandEventDao brandEventDao;
	
	@SpringBean("event.EventUserDao")
	private EventUserDao eventUserDao;
	
	@SpringBean("user.UserDao")
	private UserDao userDao;
	
	@SpringBean("common.localeDao")
    private LocaleDao localeDao;
	
	private Long templateId = 1L;
	private Long curUserId;
	private Long brandEventId;
	private BrandEvent event;
	private BrandEventStatus eventStatus;
	private String startTime;
	private String endTime;
	private Boolean isSupportedLocale = true;
	private String curUserLocale;
	private String title;
	private String upcomingEvent;
	private String ongoingEventJoined;
	private String ongoingEvenFull;
	private String ongoingEventNonJoin;
	private String expiredEvent;
	private String expiredEventSelected;
	private String eventUnsupported;
	private String eventDescription;
	private String eventProductDuration;
	private String eventApplyDescription;
	
	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}
	
	public Long getCurUserId() {
		return curUserId;
	}
	
	public void setCurUserId(Long curUserId) {
		this.curUserId = curUserId;
	}
	
	public Long getBrandEventId() {
		return brandEventId;
	}
	
	public void setBrandEventId(Long brandEventId) {
		this.brandEventId = brandEventId;
	}
	
	public BrandEvent getEvent() {
		return event;
	}
	
	public void setEvent(BrandEvent event) {
		this.event = event;
	}
	
	public BrandEventStatus getEventStatus() {
		return eventStatus;
	}
	
	public String getStartTime() {
		return startTime;
	}
	
	public String getEndTime() {
		return endTime;
	}
	
	public Boolean getIsSupportedLocale() {
		return isSupportedLocale;
	}
	
	public String getCurUserLocale() {
		return curUserLocale;
	}
	
	public String getTitle() {
		title = LanguageCenter.getConsultationLang(event.getLocale()).getTitle();
		return title;
	}
	
	public String getUpcomingEvent() {
		upcomingEvent = LanguageCenter.getConsultationLang(event.getLocale()).getUpcomingEvent();
		return upcomingEvent;
	}
	
	public String getOngoingEventJoined() {
		ongoingEventJoined = LanguageCenter.getConsultationLang(event.getLocale()).getOngoingEventJoined();
		return ongoingEventJoined;
	}
	
	public String getOngoingEventFull() {
		ongoingEvenFull = LanguageCenter.getConsultationLang(event.getLocale()).getOngoingEventFull();
		return ongoingEvenFull;
	}
	
	public String getOngoingEventNonJoin() {
		ongoingEventNonJoin = LanguageCenter.getConsultationLang(event.getLocale()).getOngoingEventNonJoin();
		return ongoingEventNonJoin;
	}
	
	public String getExpiredEvent() {
		expiredEvent = LanguageCenter.getConsultationLang(event.getLocale()).getExpiredEvent();
		return expiredEvent;
	}
	
	public String getExpiredEventSelected() {
		expiredEventSelected = LanguageCenter.getConsultationLang(event.getLocale()).getExpiredEventSelected();
		return expiredEventSelected;
	}
	
	public String getEventUnsupported() {
		eventUnsupported = LanguageCenter.getConsultationLang(event.getLocale()).getEventUnsupported();
		return eventUnsupported;
	}
	
	public String getEventDescription() {
		eventDescription = LanguageCenter.getConsultationLang(event.getLocale()).getEventDescription();
		return eventDescription;
	}
	
	public String getEventProductDuration() {
		eventProductDuration = LanguageCenter.getConsultationLang(event.getLocale()).getEventProductDuration();
		return eventProductDuration;
	}
	
	public String getEventApplyDescription() {
		eventApplyDescription = LanguageCenter.getConsultationLang(event.getLocale()).getEventApplyDescription();
		return eventApplyDescription;
	}
	
	@DefaultHandler
    public Resolution route() {
		if (brandEventId == null)
			return new ErrorResolution(ErrorDef.InvalidBrandEventId);
		
		if (!brandEventDao.exists(brandEventId))
			return new ErrorResolution(ErrorDef.InvalidBrandEventId);
		
		event = brandEventDao.findById(brandEventId);
		
		if (event.getServiceType() != ServiceType.CONSULTATION) {
			return new ErrorResolution(ErrorDef.InvalidBrandEventId);
		}
		
		event.setUserStatus(EventUserStatus.NonJoin);
		
		Date startTime = event.getEventAttrJNode().getStartTime();	
		Date endTime = event.getEventAttrJNode().getEndTime();
		
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd");
		this.startTime = sdFormat.format(startTime);
		this.endTime = sdFormat.format(endTime);
		
		Date currentTime = new Date();
		
		if (event.getIsDeleted()) {
			eventStatus = BrandEventStatus.Deleted;	
		}
		else if (startTime == null || endTime == null) {
			eventStatus = BrandEventStatus.Unknown;
		}
		else if (currentTime.before(startTime)) {
			eventStatus = BrandEventStatus.Upcoming;
		}
		else if (currentTime.before(endTime)) {
			eventStatus = BrandEventStatus.Ongoing;
		}
		else {
			eventStatus = BrandEventStatus.Expired;
		}
		
		if (curUserId != null) {
			/* Check if user locale matches event locale.
			if (userDao.exists(curUserId)) {
				User curUser = userDao.findById(curUserId);
				curUserLocale = localeDao.getLocaleByType(curUser.getRegion(), LocaleType.PRODUCT_LOCALE).iterator().next();
				if (!curUserLocale.equals(event.getLocale()))
					isSupportedLocale = false;
			}
			*/
			Map<Long, EventUserStatus> statusMap = eventUserDao.getEventUserStatusByEventIds(curUserId, Arrays.asList(brandEventId));
			if (statusMap.containsKey(brandEventId))
				event.setUserStatus(statusMap.get(brandEventId));
		}
                
        EventAttr eventAttr = event.getEventAttrJNode();
        if (eventAttr != null) {
            eventAttr.setApplyDesc(eventAttr.getApplyDesc().replaceAll("\r\n", "<br>").replaceAll("\n", "<br>"));
            eventAttr.setEventTypeDesc(eventAttr.getEventTypeDesc().replaceAll("\r\n", "<br>").replaceAll("\n", "<br>"));
            eventAttr.setReceiveDesc(eventAttr.getReceiveDesc().replaceAll("\r\n", "<br>").replaceAll("\n", "<br>"));
        }
        
		return new ForwardResolution(String.format("/page/consultation/view-consultation-detail-%d.jsp", templateId));
	}
}