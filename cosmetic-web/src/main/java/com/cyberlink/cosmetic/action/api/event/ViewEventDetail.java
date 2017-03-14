package com.cyberlink.cosmetic.action.api.event;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.lang.LanguageCenter;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.event.dao.BrandEventDao;
import com.cyberlink.cosmetic.modules.event.dao.EventUserDao;
import com.cyberlink.cosmetic.modules.event.model.BrandEvent;
import com.cyberlink.cosmetic.modules.event.model.BrandEventStatus;
import com.cyberlink.cosmetic.modules.event.model.EventAttr;
import com.cyberlink.cosmetic.modules.event.model.EventUserStatus;
import com.cyberlink.cosmetic.modules.event.model.ProductAttr;
import com.cyberlink.cosmetic.modules.event.model.ReceiveType;
import com.cyberlink.cosmetic.modules.event.model.ServiceType;
import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.product.model.Product;
import com.cyberlink.cosmetic.modules.product.model.result.ProductWrapper;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;

@UrlBinding("/api/event/view-event-detail.action")
public class ViewEventDetail extends AbstractAction {
	
	@SpringBean("event.BrandEventDao")
	private BrandEventDao brandEventDao;
	
	@SpringBean("event.EventUserDao")
	private EventUserDao eventUserDao;
	
	@SpringBean("user.UserDao")
	private UserDao userDao;
	
	@SpringBean("user.SubscribeDao")
    private SubscribeDao subscribeDao;
	
	@SpringBean("common.localeDao")
    private LocaleDao localeDao;
	
	@SpringBean("product.ProductDao")
	protected ProductDao productDao;
	
	private Long templateId = 1L;
	private Long curUserId;
	private Long brandEventId;
	private BrandEvent event;
	private BrandEventStatus eventStatus;
	private String durationString;
	private String receiveEndTime ="";
	private int remainDays;
	private int remainHours;
	private int remainMinutes;
	private Boolean isSupportedLocale = true;
	private String curUserLocale;
	private UserType userType;
    private PageResult<User> followers = new PageResult<User>();
	private PageResult<User> participants = new PageResult<User>();
	private String couponCode = "";
	private String upcomingEventDescription;
	private String upcomingEventRemainDaysDescription;
	private String upcomingEventButton;
	private String expiredEventDescription;
	private String expiredEventSelectedButton;
	private String expiredEventButton;
	private String ongoingEventDescription;
	private String ongoingEventJoinNumberDescription;
	private String ongoingEventNonJoinButton;
	private String ongoingEventButton;
	private String drawingEventButton;
	private String eventUnsupported;
	private String eventProductInfo;
	private String eventProductDuration;
	private String eventProductName;
	private String eventProductQuantity;
	private String eventProductQuantityUnit;
	private String eventProductDescription;
	private String eventProductDetail;
	private String eventDescription;
	private String eventApplyDescription;
	private String eventReceiveDescription;
	private String eventComment;
	private String followersDescription;
	private String participantsDescription;

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
	
	public String getDurationString() {
		return durationString;
	}

	public int getRemainDays() {
		return remainDays;
	}
	
	public int getRemainHours() {
		return remainHours;
	}
	
	public int getRemainMinutes() {
		return remainMinutes;
	}
	
	public Boolean getIsSupportedLocale() {
		return isSupportedLocale;
	}
	
	public String getCurUserLocale() {
		return curUserLocale;
	}
	
    public UserType getUserType() {
            return userType;
    }
    
	public PageResult<User> getFollowers() {
		return followers;
	}
	
	public PageResult<User> getParticipants() {
        return participants;
    }
	
	public String getCouponCode() {
		return couponCode;
	}

	public String getUpcomingEventDescription() {
		upcomingEventDescription = LanguageCenter.getFreeSampleLang(event.getLocale()).getUpcomingEventDescription();
		return upcomingEventDescription;
	}
	
	public String getUpcomingEventRemainDaysDescription() {
		upcomingEventRemainDaysDescription = LanguageCenter.getFreeSampleLang(event.getLocale()).getUpcomingEventRemainDaysDescription();
		return upcomingEventRemainDaysDescription;
	}
	
	public String getUpcomingEventButton() {
		upcomingEventButton = LanguageCenter.getFreeSampleLang(event.getLocale()).getUpcomingEventButton();
		return upcomingEventButton;
	}
	
	public String getExpiredEventDescription() {
		expiredEventDescription = LanguageCenter.getFreeSampleLang(event.getLocale()).getExpiredEventDescription();
		return expiredEventDescription;
	}
	
	public String getExpiredEventSelectedButton() {
		expiredEventSelectedButton = LanguageCenter.getFreeSampleLang(event.getLocale()).getExpiredEventSelectedButton();
		return expiredEventSelectedButton;
	}
	
	public String getExpiredEventButton() {
		expiredEventButton = LanguageCenter.getFreeSampleLang(event.getLocale()).getExpiredEventButton();
		return expiredEventButton;
	}
	
	public String getOngoingEventDescription() {
		ongoingEventDescription = String.format(LanguageCenter.getFreeSampleLang(event.getLocale()).getOngoingEventDescription(), this.remainDays, this.remainHours, this.remainMinutes);
		return ongoingEventDescription;
	}
	
	public String getOngoingEventJoinNumberDescription() {
		ongoingEventJoinNumberDescription = String.format(LanguageCenter.getFreeSampleLang(event.getLocale()).getOngoingEventJoinNumberDescription(), this.event.getJoinNum());
		return ongoingEventJoinNumberDescription;
	}
	
	public String getOngoingEventNonJoinButton() {
		ongoingEventNonJoinButton = LanguageCenter.getFreeSampleLang(event.getLocale()).getOngoingEventNonJoinButton();
		return ongoingEventNonJoinButton;
	}
	
	public String getOngoingEventButton() {
		ongoingEventButton = LanguageCenter.getFreeSampleLang(event.getLocale()).getOngoingEventButton();
		return ongoingEventButton;
	}
	
	public String getDrawingEventButton() {
		drawingEventButton = LanguageCenter.getFreeSampleLang(event.getLocale()).getDrawingEventButton();
		return drawingEventButton;
	}
	
	public String getEventUnsupported() {
		eventUnsupported = LanguageCenter.getFreeSampleLang(event.getLocale()).getEventUnsupported();
		return eventUnsupported;
	}
	
	public String getEventProductInfo() {
		eventProductInfo = LanguageCenter.getFreeSampleLang(event.getLocale()).getEventProductInfo();
		return eventProductInfo;
	}
	
	public String getEventProductDuration() {
		eventProductDuration = LanguageCenter.getFreeSampleLang(event.getLocale()).getEventProductDuration();
		return eventProductDuration;
	}
	
	public String getEventProductName() {
		eventProductName = LanguageCenter.getFreeSampleLang(event.getLocale()).getEventProductName();
		return eventProductName;
	}
	
	public String getEventProductQuantity() {
		eventProductQuantity = LanguageCenter.getFreeSampleLang(event.getLocale()).getEventProductQuantity();
		return eventProductQuantity;
	}
	
	public String getEventProductQuantityUnit() {
		eventProductQuantityUnit = LanguageCenter.getFreeSampleLang(event.getLocale()).getEventProductQuantityUnit();
		return eventProductQuantityUnit;
	}
	
	public String getEventProductDescription() {
		eventProductDescription = LanguageCenter.getFreeSampleLang(event.getLocale()).getEventProductDescription();
		return eventProductDescription;
	}
	
	public String getEventProductDetail() {
		eventProductDetail = LanguageCenter.getFreeSampleLang(event.getLocale()).getEventProductDetail();
		return eventProductDetail;
	}
	
	public String getEventDescription() {
		eventDescription = LanguageCenter.getFreeSampleLang(event.getLocale()).getEventDescription();
		return eventDescription;
	}
	
	public String getEventApplyDescription() {
		eventApplyDescription = LanguageCenter.getFreeSampleLang(event.getLocale()).getEventApplyDescription();
		return eventApplyDescription;
	}
	
	public String getEventReceiveDescription() {
		eventReceiveDescription = LanguageCenter.getFreeSampleLang(event.getLocale()).getEventReceiveDescription();
		return eventReceiveDescription;
	}
	
	public String getEventComment() {
		eventComment = LanguageCenter.getFreeSampleLang(event.getLocale()).getEventComment();
		return eventComment;
	}
	
	public String getFollowersDescription() {
		followersDescription = LanguageCenter.getFreeSampleLang(event.getLocale()).getFollowersDescription();
		return followersDescription;
	}
	
	public String getParticipantsDescription() {
		participantsDescription = LanguageCenter.getFreeSampleLang(event.getLocale()).getParticipantsDescription();
		return participantsDescription;
	}
	
	public String getCouponReceiveTitle() {
		return LanguageCenter.getFreeSampleLang(event.getLocale()).getCouponReceiveTitle();
	}
	
	public String getCouponReceiveDescription() {
		return LanguageCenter.getFreeSampleLang(event.getLocale()).getCouponReceiveDescription();
	}
	
	public String getCouponReceiveEndTime() {
		return LanguageCenter.getFreeSampleLang(event.getLocale()).getCouponReceiveEndTime(receiveEndTime);
	}
	
	public String getCouponUsingbtn() {
		return LanguageCenter.getFreeSampleLang(event.getLocale()).getCouponUsingbtn();
	}
	
	@DefaultHandler
    public Resolution route() {
		if (brandEventId == null)
			return new ErrorResolution(ErrorDef.InvalidBrandEventId);
		
		if (!brandEventDao.exists(brandEventId))
			return new ErrorResolution(ErrorDef.InvalidBrandEventId);
		
		event = brandEventDao.findById(brandEventId);
		if (event.getServiceType() != ServiceType.FREE_SAMPLE) {
			return new ErrorResolution(ErrorDef.InvalidBrandEventId);
		}
		
		event.setUserStatus(EventUserStatus.NonJoin);
		
		Date startTime = event.getEventAttrJNode().getStartTime();	
		Date endTime = event.getEventAttrJNode().getEndTime();
		durationString = getDurationFormat(event.getLocale(), startTime, endTime);
		
		Date currentTime = new Date();
		
		if (event.getIsDeleted()) {
			eventStatus = BrandEventStatus.Deleted;	
		}
		else if (startTime == null || endTime == null) {
			eventStatus = BrandEventStatus.Unknown;
		}
		else if (currentTime.before(startTime)) {
			eventStatus = BrandEventStatus.Upcoming;
			DateTime dt1 = new DateTime(currentTime);
			DateTime dt2 = new DateTime(startTime);
			remainDays = Days.daysBetween(dt1, dt2).getDays();
			
			// Spec. If remain days less than 1 day, UI should show 1 day.
			if (remainDays < 1)
				remainDays = 1;
		}
		else if (currentTime.before(endTime)) {
			eventStatus = BrandEventStatus.Ongoing;
			DateTime dt1 = new DateTime(currentTime);
			DateTime dt2 = new DateTime(endTime);
			remainDays = Days.daysBetween(dt1, dt2).getDays();
			remainHours = Hours.hoursBetween(dt1, dt2).getHours() % 24;
			remainMinutes = Minutes.minutesBetween(dt1, dt2).getMinutes() % 60;
			
			// Spec. If remain time less than 1 minute, UI should show 1 minute.
			if (remainDays < 1 && remainHours < 1 && remainMinutes <1)
				remainMinutes = 1;
		}
		else if (currentTime.before(event.getEventAttrJNode().getDrawTime())){
			eventStatus = BrandEventStatus.Drawing;
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
			
			// eCoupon
			if (ReceiveType.Coupon.equals(event.getReceiveType()) && EventUserStatus.Selected.equals(event.getUserStatus())) {
				couponCode = eventUserDao.getCouponCode(brandEventId, curUserId);
				try {
					SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd");
					receiveEndTime = sdFormat.format(event.getEventAttrJNode().getReceiveEndDate());
				} catch (Exception e) {
					receiveEndTime = "";
				}
			}
		}
		
		Long brandUserId = event.getBrandId();
        User brandUser = userDao.findById(brandUserId);
        userType = brandUser.getUserType();
        if(userType != null && !userType.equals(UserType.CL)) {
            PageResult<Long> subscriberIds = subscribeDao.findBySubscribee(brandUserId, null, new BlockLimit(0, 5));
            List<User> followerList = userDao.findByIds(subscriberIds.getResults().toArray(new Long[subscriberIds.getResults().size()]));
            followers.setResults(followerList);
            followers.setTotalSize(subscriberIds.getTotalSize());
        }
        else {
            PageResult<Long> participantIds = eventUserDao.findUserIdsByEventId(brandEventId, null, new BlockLimit(0, 5));
            List<User> participantList = userDao.findByIds(participantIds.getResults().toArray(new Long[participantIds.getResults().size()]));
            participants.setResults(participantList);
            participants.setTotalSize(participantIds.getTotalSize());
        }
                
        EventAttr eventAttr = event.getEventAttrJNode();
        if (eventAttr != null) {
            eventAttr.setApplyDesc(eventAttr.getApplyDesc().replaceAll("\r\n", "<br>").replaceAll("\n", "<br>"));
            eventAttr.setEventTypeDesc(eventAttr.getEventTypeDesc().replaceAll("\r\n", "<br>").replaceAll("\n", "<br>"));
            eventAttr.setReceiveDesc(eventAttr.getReceiveDesc().replaceAll("\r\n", "<br>").replaceAll("\n", "<br>"));
        }
        
        ProductAttr productAttr = event.getProductAttrJNode();
        if (productAttr != null) {
        	Long bcProductId = productAttr.getBcProductId();
        	if (productDao.exists(bcProductId)) {
        		Product product = productDao.findById(bcProductId);
        		ProductWrapper productWrapper = new ProductWrapper(product);
        		productAttr.setBrandName(productWrapper.getBrandName());
        		productAttr.setCategory(productWrapper.getTypeName().get(0));
        		productAttr.setName(productWrapper.getProductName());
        		productAttr.setPrice(productWrapper.getRecommendedPrice());
        		productAttr.setProductLink("ybc://product/" + bcProductId.toString() + "?Button=freeSample");
        		productAttr.setThumbnailUrl(productWrapper.getImgThumbnail());
        		productAttr.setRating(productWrapper.getRating());
        		productAttr.setCommentCount(productWrapper.getCommentCount());
        	}
        }
        
		return new ForwardResolution(String.format("/page/brandevent/view-event-detail-%d.jsp", templateId));
	}
	
	private String getDurationFormat(String locale, Date start, Date end) {
		if ("ja_JP".equalsIgnoreCase(event.getLocale())) {
			SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy年MM月dd日");
			String startTimeStr = sdFormat.format(start);
			String endTimeStr = sdFormat.format(end);
			return String.format("%s～%s", startTimeStr, endTimeStr);
		} else {
			SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd");
			String startTimeStr = sdFormat.format(start);
			String endTimeStr = sdFormat.format(end);
			return String.format("%s~%s", startTimeStr, endTimeStr);
		}
	}
}