package com.cyberlink.cosmetic.action.api.post;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import com.cyberlink.cosmetic.action.api.AbstractAction;

@UrlBinding("/api/post/SeePostOnYPC.action")
public class SeePostOnYPCAction extends AbstractAction{
	
	private String appUrl = "ypc://" ;
	private static final String jspFilePath = "/api/post/seePostOnYPC.jsp" ;
	
	@DefaultHandler
	public Resolution route(){
		
		return forward(jspFilePath);
	}

	public String getAppUrl() {
		return appUrl;
	}

	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl;
	}
}
