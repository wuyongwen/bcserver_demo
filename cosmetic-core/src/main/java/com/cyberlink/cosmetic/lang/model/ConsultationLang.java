package com.cyberlink.cosmetic.lang.model;

public class ConsultationLang extends AbstractLang {

	public ConsultationLang(String locale) {
		super(locale);
	}
	
	//title
	public String getTitle() {
		try {
			return resBundle.getString("Consultation.title");
		} catch (Exception e) {
			return "";
		}
	}
	
	//upcoming event
	public String getUpcomingEvent() {
		try {
			return resBundle.getString("Consultation.upcomingEvent");
		} catch (Exception e) {
			return "";
		}
	}
	
	//ongoing event
	public String getOngoingEventJoined() {
		try {
			return resBundle.getString("Consultation.ongoingEvent.joined");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getOngoingEventFull() {
		try {
			return resBundle.getString("Consultation.ongoingEvent.full");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getOngoingEventNonJoin() {
		try {
			return resBundle.getString("Consultation.ongoingEvent.nonJoin");
		} catch (Exception e) {
			return "";
		}
	}
	
	//expired event
	public String getExpiredEvent() {
		try {
			return resBundle.getString("Consultation.expiredEvent");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getExpiredEventSelected() {
		try {
			return resBundle.getString("Consultation.expiredEvent.selected");
		} catch (Exception e) {
			return "";
		}
	}
	
	//event details
	public String getEventUnsupported() {
		try {
			return resBundle.getString("Consultation.event.unsupported");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getEventDescription() {
		try {
			return resBundle.getString("Consultation.event.description");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getEventProductDuration() {
		try {
			return resBundle.getString("Consultation.event.product.duration");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getEventApplyDescription() {
		try {
			return resBundle.getString("Consultation.event.applyDescription");
		} catch (Exception e) {
			return "";
		}
	}
}