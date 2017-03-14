package com.cyberlink.cosmetic.lang.model;

public class FreeSampleLang extends AbstractLang {

	public FreeSampleLang(String locale) {
		super(locale);
	}
	
	//upcoming event
	public String getUpcomingEventDescription() {
		try {
			return resBundle.getString("freeSample.upcomingEvent.description");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getUpcomingEventRemainDaysDescription() {
		try {
			return resBundle.getString("freeSample.upcomingEvent.remainDays.description");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getUpcomingEventButton() {
		try {
			return resBundle.getString("freeSample.upcomingEvent.button");
		} catch (Exception e) {
			return "";
		}
	}
	
	//ongoing event
	public String getOngoingEventDescription() {
		try {
			return resBundle.getString("freeSample.ongoingEvent.description");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getOngoingEventJoinNumberDescription() {
		try {
			return resBundle.getString("freeSample.ongoingEvent.joinNumber.description");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getOngoingEventNonJoinButton() {
		try {
			return resBundle.getString("freeSample.ongoingEvent.nonJoin.button");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getOngoingEventButton() {
		try {
			return resBundle.getString("freeSample.ongoingEvent.button");
		} catch (Exception e) {
			return "";
		}
	}
	
	//expired event
	public String getExpiredEventDescription() {
		try {
			return resBundle.getString("freeSample.expiredEvent.description");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getExpiredEventSelectedButton() {
		try {
			return resBundle.getString("freeSample.expiredEvent.selected.button");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getExpiredEventButton() {
		try {
			return resBundle.getString("freeSample.expiredEvent.button");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getDrawingEventButton() {
		try {
			return resBundle.getString("freeSample.drawingEvent.button");
		} catch (Exception e) {
			return "";
		}
	}
	
	//event details
	public String getEventUnsupported() {
		try {
			return resBundle.getString("freeSample.event.unsupported");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getEventProductInfo() {
		try {
			return resBundle.getString("freeSample.event.product.info");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getEventProductDuration() {
		try {
			return resBundle.getString("freeSample.event.product.duration");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getEventProductName() {
		try {
			return resBundle.getString("freeSample.event.product.name");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getEventProductQuantity() {
		try {
			return resBundle.getString("freeSample.event.product.quantity");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getEventProductQuantityUnit() {
		try {
			return resBundle.getString("freeSample.event.product.quantity.unit");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getEventProductDescription() {
		try {
			return resBundle.getString("freeSample.event.product.description");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getEventProductDetail() {
		try {
			return resBundle.getString("freeSample.event.product.detail");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getEventDescription() {
		try {
			return resBundle.getString("freeSample.event.Description");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getEventApplyDescription() {
		try {
			return resBundle.getString("freeSample.event.applyDescription");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getEventReceiveDescription() {
		try {
			return resBundle.getString("freeSample.event.receiveDescription");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getEventComment() {
		try {
			return resBundle.getString("freeSample.event.comment");
		} catch (Exception e) {
			return "";
		}
	}
	
	//others
	public String getFollowersDescription() {
		try {
			return resBundle.getString("freeSample.followers.description");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getParticipantsDescription() {
		try {
			return resBundle.getString("freeSample.participants.description");
		} catch (Exception e) {
			return "";
		}
	}

	// Coupon
	public String getCouponReceiveTitle() {
		try {
			return resBundle.getString("freeSample.coupon.receive.title");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getCouponReceiveDescription() {
		try {
			return resBundle.getString("freeSample.coupon.receive.description");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getCouponReceiveEndTime(String receiveEndTime) {
		try {
			return String.format(resBundle.getString("freeSample.coupon.receive.endTime"), receiveEndTime);
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getCouponUsingbtn() {
		try {
			return resBundle.getString("freeSample.coupon.receive.usingbtn");
		} catch (Exception e) {
			return "";
		}
	}
}