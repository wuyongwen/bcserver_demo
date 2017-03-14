package com.cyberlink.cosmetic.modules.mail.model.event;

import com.cyberlink.cosmetic.lang.LanguageCenter;
import com.cyberlink.cosmetic.lang.model.MailLang;
import com.cyberlink.cosmetic.modules.event.model.BrandEvent;
import com.cyberlink.cosmetic.modules.event.model.EventUser;

public class EventReceiveData {
	protected BrandEvent brandEvent;
	protected EventUser eventUser;
	protected MailLang mailLang = null;

	public EventReceiveData(BrandEvent brandEvent, EventUser eventUser) {
		this.brandEvent = brandEvent;
		this.eventUser = eventUser;
	}
	
	protected MailLang getMailLang() {
		if (mailLang == null)
			mailLang = LanguageCenter.getMailLang(getLocale());
		return mailLang;
	}

	public String getName() {
		if (eventUser != null && eventUser.getName() != null)
			return eventUser.getName();
		return "";
	}

	public String getPhone() {
		if (eventUser != null && eventUser.getPhone() != null)
			return eventUser.getPhone();
		return "";
	}

	public String getMail() {
		if (eventUser != null && eventUser.getMail() != null)
			return eventUser.getMail();
		return "";
	}

	public String getUserAddress() {
		if (eventUser != null)
			return eventUser.getUserAddress();
		return "";
	}

	public String getStoreAddress() {
		if (eventUser != null)
			return eventUser.getStoreAddress();
		return "";
	}

	public String getStoreName() {
		if (eventUser != null)
			return eventUser.getStoreName();
		return "";
	}

	public String getStoreLocation() {
		if (eventUser != null)
			return eventUser.getStoreLocation();
		return "";
	}

	public String getProdName() {
		if (brandEvent != null && brandEvent.getProdName() != null)
			return brandEvent.getProdName();
		return "";
	}

	public String getTitle() {
		if (brandEvent != null && brandEvent.getTitle() != null)
			return brandEvent.getTitle();
		return "";
	}

	public String getLocale() {
		if (brandEvent != null && brandEvent.getLocale() != null)
			return brandEvent.getLocale();
		return "";
	}

	// template string
	public String getFreeSampleSubject() {
		return getMailLang().getFreeSampleSubject();
	}

	public String getFreeSampleHello() {
		return getMailLang().getFreeSampleHello();
	}

	public String getFreeSampleDescriptionStore() {
		return getMailLang().getFreeSampleDescriptionStore(getTitle());
	}

	public String getFreeSampleDescriptionHome() {
		return getMailLang().getFreeSampleDescriptionHome(getTitle());
	}

	public String getFreeSampleYourInfo() {
		return getMailLang().getFreeSampleYourInfo();
	}

	public String getFreeSampleUserName() {
		return getMailLang().getFreeSampleUserName(getName());
	}

	public String getFreeSamplePhone() {
		return getMailLang().getFreeSamplePhone(getPhone());
	}

	public String getFreeSampleEmail() {
		return getMailLang().getFreeSampleEmail(getMail());
	}

	public String getFreeSampleReceiveStore() {
		return getMailLang().getFreeSampleReceiveStore(getStoreLocation(),
				getStoreName());
	}

	public String getFreeSampleUserAddress() {
		return getMailLang().getFreeSampleUserAddress(getUserAddress());
	}

	public String getFreeSampleProdName() {
		return getMailLang().getFreeSampleProdName(getProdName());
	}

	public String getFreeSampleThankForJoin() {
		return getMailLang().getFreeSampleThankForJoin();
	}

	public String getFreeSampleEnd() {
		return getMailLang().getFreeSampleEnd();
	}

	public String getFreeSampleAnyProblemStore() {
		return getMailLang().getFreeSampleAnyProblemStore();
	}

	public String getFreeSampleAnyProblemHome() {
		return getMailLang().getFreeSampleAnyProblemHome();
	}

	public String getCopyRight() {
		return getMailLang().getCopyRight();
	}
	
	// Coupon
	public String getFreeSampleCouponDescription() {
		return getMailLang().getFreeSampleCouponDescription(getTitle());
	}
	
	public String getFreeSampleCouponGetcode() {
		return getMailLang().getFreeSampleCouponGetcode();
	}
	
	public String getFreeSampleCouponEnd() {
		return getMailLang().getFreeSampleCouponEnd();
	}
	
	public String getFreeSampleCouponAnyProblem() {
		return getMailLang().getFreeSampleCouponAnyProblem();
	}
}