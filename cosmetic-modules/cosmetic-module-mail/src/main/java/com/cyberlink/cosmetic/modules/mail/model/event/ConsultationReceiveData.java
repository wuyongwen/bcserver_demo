package com.cyberlink.cosmetic.modules.mail.model.event;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.cyberlink.cosmetic.modules.event.model.BrandEvent;
import com.cyberlink.cosmetic.modules.event.model.EventUser;

public class ConsultationReceiveData extends EventReceiveData {

	public ConsultationReceiveData(BrandEvent brandEvent, EventUser eventUser) {
		super(brandEvent, eventUser);
	}

	public String getBirthday() {
		if (eventUser != null)
			return eventUser.getBirthDayString();
		return "";
	}

	public String getDescription() {
		if (brandEvent != null && brandEvent.getDescription() != null)
			return brandEvent.getDescription();
		return "";
	}

	public String getEndTime() {
		if (brandEvent != null) {
			try {
				SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
						"yyyy/MM/dd");
				dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
				return dateFormatGmt.format(brandEvent.getEndTime());
			} catch (Exception e) {
				return "";
			}
		}
		return "";
	}

	// template string
	public String getConsultationSubject() {
		return getMailLang().getConsultationSubject(getTitle());
	}

	public String getConsultationHello() {
		return getMailLang().getConsultationHello(getName());
	}

	public String getConsultationDescription() {
		return getMailLang().getConsultationDescription(getEndTime(),
				getDescription());
	}

	public String getConsultationYourInfo() {
		return getMailLang().getConsultationYourInfo();
	}

	public String getConsultationUserName() {
		return getMailLang().getConsultationUserName(getName());
	}

	public String getConsultationBirhday() {
		return getMailLang().getConsultationBirhday(getBirthday());
	}

	public String getConsultationPhone() {
		return getMailLang().getConsultationPhone(getPhone());
	}

	public String getConsultationEmail() {
		return getMailLang().getConsultationEmail(getMail());
	}

	public String getConsultationReceiveStore() {
		return getMailLang().getConsultationReceiveStore(getStoreLocation(),
				getStoreName());
	}

	public String getConsultationEnd() {
		return getMailLang().getConsultationEnd();
	}

	public String getConsultationProblem() {
		return getMailLang().getConsultationProblem();
	}

}