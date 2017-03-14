package com.cyberlink.cosmetic.action.backend.misc;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.utils.EncrUtil;

@UrlBinding("/misc/encrUtil.action")
public class EncrUtilAction extends AbstractAction {
	private String encrString;
	private String decrString;
	private String result;

	@DefaultHandler
	public Resolution route() {
		if (!getCurrentUserAdmin()) {
			return new ErrorResolution(403, "Need to login");
		}

		return forward();
	}

	public Resolution decrypt() {
		if (!getCurrentUserAdmin()) {
			return new ErrorResolution(403, "Need to login");
		}

		result = EncrUtil.decrypt(encrString);

		return new ForwardResolution("/misc/encrUtil-route.jsp");
	}

	public Resolution encrypt() {
		if (!getCurrentUserAdmin()) {
			return new ErrorResolution(403, "Need to login");
		}

		result = EncrUtil.encrypt(decrString);

		return new ForwardResolution("/misc/encrUtil-route.jsp");
	}

	public String getEncrString() {
		return encrString;
	}

	public void setEncrString(String encrString) {
		this.encrString = encrString;
	}

	public String getDecrString() {
		return decrString;
	}

	public void setDecrString(String decrString) {
		this.decrString = decrString;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}