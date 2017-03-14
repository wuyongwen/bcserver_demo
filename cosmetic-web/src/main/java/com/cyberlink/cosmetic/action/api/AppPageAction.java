package com.cyberlink.cosmetic.action.api;

import java.io.DataOutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

@UrlBinding("/api/gotoapp.action")
public class AppPageAction extends AbstractAction{
	
	private static final String jspFilePath = "/redirect/redirectToYMK.jsp" ;
	
	private Long postId = Long.valueOf(1776545) ;
	private String iOsAppId = "863844475";
	private String iOsAppName = "YouCam Makeup" ;
	private String iOsAppUrl = "ymk://" ;
	private String androidAppUrl = "ymk://" ;
	private String androidAppName = "YouCam Makeup" ;
	private String androidAppPackage = "com.cyberlink.youcammakeup" ;
	
	private String updateFBApiUrl = "http://developers.facebook.com/tools/debug/og/object?q=";
	
	@DefaultHandler
	public Resolution route(){
		
		return forward(jspFilePath);
	}

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public String getiOsAppId() {
		return iOsAppId;
	}

	public void setiOsAppId(String iOsAppId) {
		this.iOsAppId = iOsAppId;
	}

	public String getiOsAppName() {
		return iOsAppName;
	}

	public void setiOsAppName(String iOsAppName) {
		this.iOsAppName = iOsAppName;
	}

	public String getiOsAppUrl() {
		return iOsAppUrl;
	}

	public void setiOsAppUrl(String iOsAppUrl) {
		this.iOsAppUrl = iOsAppUrl;
	}

	public String getAndroidAppUrl() {
		return androidAppUrl;
	}

	public void setAndroidAppUrl(String androidAppUrl) {
		this.androidAppUrl = androidAppUrl;
	}

	public String getAndroidAppName() {
		return androidAppName;
	}

	public void setAndroidAppName(String androidAppName) {
		this.androidAppName = androidAppName;
	}

	public String getAndroidAppPackage() {
		return androidAppPackage;
	}

	public void setAndroidAppPackage(String androidAppPackage) {
		this.androidAppPackage = androidAppPackage;
	}
	
}
