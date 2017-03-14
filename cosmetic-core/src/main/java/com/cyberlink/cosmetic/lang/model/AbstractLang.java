package com.cyberlink.cosmetic.lang.model;

import java.util.ResourceBundle;

import com.cyberlink.cosmetic.lang.ResourceBundleController;

public class AbstractLang {

	protected ResourceBundle resBundle;

	public AbstractLang(String locale) {
		this.resBundle = ResourceBundleController.getResourceBundle(locale);
	}
}