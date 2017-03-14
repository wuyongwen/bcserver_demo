package com.cyberlink.cosmetic.action.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.common.model.Locale;
import com.cyberlink.cosmetic.modules.common.model.Locale.DiscoverTabType;
import com.cyberlink.cosmetic.modules.common.model.Locale.TrendingTabType;
import com.cyberlink.cosmetic.modules.common.service.GeoIPService;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.google.common.collect.ImmutableList;

import org.apache.commons.lang.StringUtils;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/init")
public class InitAction extends AbstractAction {
    private String ap;
    private String version = "1.0.0";
    private String versionType;
    private String buildNumber;
    private String locale;
    private String uuid = "";
	private String model;
    private String vender;
    private String resolution;
    private String apiVersion;
    private String apnsToken;
    private String apnsType;
    private Long userId;
	private String userAgent;
	private Locale userInputLocale;
	private String writeServer = "";
	private String readTokenServer;
	private String aid = "";
	
	@SpringBean("common.localeDao")
    private LocaleDao localeDao;

	@SpringBean("user.UserDao")
    private UserDao userDao;
	
	@SpringBean("common.geoIPService")
	private GeoIPService geoIPService;
	
    @DefaultHandler
    public Resolution list() {
        if(locale == null) {
            locale = LocaleType.getDefaultInputLocale();
        }
        String localeInput = LocaleType.getDefaultSourceLocale();
        Set<String> availableInputLocales = localeDao.getAvailableLocaleByType(LocaleType.INPUT_LOCALE);
        if(availableInputLocales.contains(locale))
            localeInput = locale;
        userInputLocale = localeDao.getAvailableInputLocale(localeInput);
    	/*if (apiVersion == null || Double.valueOf(apiVersion) < 5.0) {
            if (userId != null) {
            	if (Constants.getWebsiteIsWritable().equalsIgnoreCase("true"))
            		updateUserDevice(userId);
            	else
            		updateUserDeviceByUrl(userId);
            }
    	}*/
        return json(getAPIs());
    }

    public Map<String, Object> getAPIs() {
    	final String domain = Constants.getWebsiteDomain();
    	final String writeDomain = Constants.getWebsiteWrite();
    	String server;
    	
    	String protocol = "https://";
    	if (!Constants.getWebsiteSupportHttps().equals("true"))
    	    protocol = "http://";
		server = protocol + domain;
		writeServer = protocol + writeDomain;
		readTokenServer = server;
		if(Constants.getWebsiteReadToken() != null) {
		    readTokenServer = protocol + Constants.getWebsiteReadToken();
		}
		
    	final String ipAddress = geoIPService.getIpAddr(getContext().getRequest());
    	final String countryCode = geoIPService.getCountryCode(ipAddress);
    	
        final Map<String, Object> results = new LinkedHashMap<String, Object>();
        results.put("user", getUserAPIs(server, writeServer, ipAddress, countryCode));
        results.put("file", getFileAPIs(server, writeServer));
        results.put("message", getMessageAPIs(server));
        results.put("post", getPostAPIs(server, writeServer));
        results.put("actionCode", getActionCode());
        return results;
    }
    
    private Map<String, String> getUserAPIs(final String server, final String writeServer, final String ipAddress, final String countryCode) {
        final Map<String, String> results = new LinkedHashMap<String, String>();
        String nonsServer = server.replace("https://", "http://");
        
        results.put("signIn", writeServer  + "/api/user/sign-in.action");
        results.put("signOut", writeServer + "/api/user/sign-out.action");
        results.put("updateUser", writeServer + "/api/user/update.action");
        results.put("userInfo", readTokenServer + "/api/user/info.action");
        results.put("listBlockedUser", nonsServer + "/api/user/list-blocked-user.action");
        results.put("blockUser", writeServer + "/api/user/block-user.action");
        results.put("unblockUser", writeServer + "/api/user/unblock-user.action");
        results.put("followUser", writeServer + "/api/user/follow.action");
        results.put("unfollowUser", writeServer + "/api/user/unfollow.action");
        results.put("listFollower", nonsServer + "/api/user/list-follower.action");
        results.put("listFollowing", nonsServer + "/api/user/list-following.action");
        results.put("listByType", nonsServer + "/api/user/list-user-byType.action");
        results.put("updateAdvancedInfo", writeServer + "/api/user/update-advanced-info.action");
        results.put("listActiveUser", nonsServer + "/api/user/list-active-user.action");
        results.put("registerPhone", writeServer +"/api/sms/register-phone.action");
        results.put("verifyPhone", writeServer +"/api/sms/verify-phone.action");
        return results;
    }

    private Map<String, String> getMessageAPIs(final String server) {
        final Map<String, String> results = new LinkedHashMap<String, String>();
        results.put("host", server);
        return results;
    }
    
    private Map<String, String> getFileAPIs(final String server, final String writeServer) {
        final Map<String, String> results = new LinkedHashMap<String, String>();
        String encoded = versionType;
        try {
            if (StringUtils.isNotBlank(versionType)) {
                encoded = URLEncoder.encode(versionType, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
        
        String nonsServer = server.replace("https://", "http://");
        
        results.put("uploadFile", writeServer + "/api/file/upload-file.action");
        results.put("downloadFile", nonsServer + "/api/file/download-file.action");
        return results;
    }
    
    private Map<String, String> getPostAPIs(final String server, final String writeServer) {
        final Map<String, String> results = new LinkedHashMap<String, String>();
        String encoded = versionType;
        try {
            if (StringUtils.isNotBlank(versionType)) {
                encoded = URLEncoder.encode(versionType, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
        
        String nonsServer = server.replace("https://", "http://");
        
        results.put("like", writeServer + "/api/post/like.action");
        results.put("unlike", nonsServer + "/api/post/unlike.action");
        results.put("listLikedTarget", nonsServer + "/api/post/list-liked-target.action");
        results.put("createPosts", writeServer + "/api/post/create-posts.action");
        results.put("deletePost", writeServer + "/api/post/delete-post.action");
        results.put("listLookPostByUser", nonsServer + "/api/post/list-look-post-by-user.action");
        results.put("queryCompletePostById", nonsServer + "/api/post/query-complete-post.action");
        results.put("updatePosts", writeServer + "/api/post/update-posts.action");
        return results;
    }
    
    private String getUserLocale(String requestLocale){
    	if( requestLocale == null ){
    		return "en_US" ;
    	}
    	String dbLocale ;
    	switch( requestLocale ){
	    	case "en_US" :
			case "en-US" :
			case "US" :
				dbLocale = "en_US";
				break;
			case "en_CA" :
			case "en-CA" :
			case "CA" :
				dbLocale = "en_CA";
				break;			
			case "en_GB" :
			case "en-GB" :
			case "GB" :
				dbLocale = "en_GB";
				break;
    		case "ja_JP" :
    		case "ja-JP" :
    		case "JP" :
    			dbLocale = "ja_JP";
    			break;
    		case "de_DE" :
    		case "de-DE" :
    		case "DE" :
    			dbLocale = "de_DE";
    			break;
    		case "fr_FR" :
    		case "fr-FR" :
    		case "FR" :
    			dbLocale = "fr_FR";
    			break;
    		case "zh_TW" :
    		case "zh-TW" :
    		case "TW" :
    			dbLocale = "zh_TW";
    			break;
    		case "zh_CN" :
    		case "zh-CN" :
    		case "CN" :
    		case "zh_HK" :
    		case "zh-HK" :
    		case "HK" :
    			dbLocale = "zh_CN";
    			break;
    		case "ru_RU" :
    		case "ru-RU" :
    		case "RU" :
    			dbLocale = "ru_RU";
    			break;
    		default:
    			if (apiVersion != null && apiVersion.equalsIgnoreCase("1.0")) {
    				dbLocale = "cl_CL";
    			} else if (requestLocale.length() < 5) {
    				if (requestLocale.length() == 2)
    					dbLocale = requestLocale + "_" + requestLocale.toUpperCase();
    				else
    					dbLocale = "en_US";
    			} else {
    				dbLocale = requestLocale;
    			}
    			break;
    	}
    	return dbLocale;
    }
    
    public String getAp() {
    	if (ap != null) {
			ap = ap.replaceAll(" ", "");
    	}
		return ap;
	}

	public void setAp(String ap) {
		this.ap = ap;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersionType() {
		return versionType;
	}

	public void setVersionType(String versionType) {
		this.versionType = versionType;
	}

	public String getBuildNumber() {
		return buildNumber;
	}

	public void setBuildNumber(String buildNumber) {
		this.buildNumber = buildNumber;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}
	
    public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getVender() {
		return vender;
	}

	public void setVender(String vender) {
		this.vender = vender;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public String getApnsToken() {
		return apnsToken;
	}

	public void setApnsToken(String apnsToken) {
		this.apnsToken = apnsToken;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	
    public String getApnsType() {
		return apnsType;
	}

	public void setApnsType(String apnsType) {
		this.apnsType = apnsType;
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getActionCode() {
		return Long.toHexString(AppActionMap.getActionValue());
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}
}
