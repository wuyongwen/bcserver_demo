package com.cyberlink.cosmetic.lang.model;

public class ApiPageLang extends AbstractLang {

	public ApiPageLang(String locale) {
		super(locale);
	}
	
	public String getCopyRight() {
		try {
			return resBundle.getString("email.copyRight");
		} catch (Exception e) {
			return "";
		}
	}
	
	// Reset Password
	public String getRsetpasswordExpired1() {
		try {
			return resBundle.getString("page.resetpassword.expired.content1");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getRsetpasswordExpired2() {
		try {
			return resBundle.getString("page.resetpassword.expired.content2");
		} catch (Exception e) {
			return "";
		}
	}
}