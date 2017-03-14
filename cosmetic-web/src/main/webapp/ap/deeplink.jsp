<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.cyberlink.core.BeanLocator"%>
<%@ page import="com.cyberlink.core.web.utl.URLContentReader" %>
<%@ page import="com.cyberlink.cosmetic.Constants"%>
<%@ page import="com.cyberlink.cosmetic.modules.product.model.DeeplinkLog" %>
<%@ page import="com.cyberlink.cosmetic.modules.product.dao.DeeplinkLogDao" %>
<%@ page import="com.cyberlink.cosmetic.modules.post.model.Post" %>
<%@ page import="com.cyberlink.cosmetic.modules.post.dao.PostDao" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.io.UnsupportedEncodingException" %>
<%@ page import="java.net.MalformedURLException" %>
<%@ page import="java.lang.NumberFormatException" %>
<%@ page import="com.restfb.json.JsonObject" %>
<%@ include file="/common/taglibs.jsp"%>
<%
String appUrl = request.getParameter("appUrl");
try {
	if (appUrl != null)
		appUrl = URLDecoder.decode(appUrl, "UTF-8");
	if (appUrl.contains("?&"))
		appUrl = appUrl.replace("?&", "?");
} catch (UnsupportedEncodingException e) {
    // TODO Auto-generated catch block
    //e.printStackTrace();
}
if( appUrl == null ){
    appUrl = "ybc://feed";
}
String appName = request.getParameter("appName");
if( appName == null ){
    appName = "YBC";
} else {
	appName.toUpperCase();
}

String storeName, storeId, storePackage, path;
if (appName.equals("YBC")) {
	storeName = "Beauty Circle";
	storeId = "1066152001";
	storePackage = "com.perfectcorp.beautycircle";
	path = "ybc";
}
else if (appName.equals("YMK")) {
    storeName = "YouCam Makeup";
    storeId = "863844475";
    storePackage = "com.cyberlink.youcammakeup";
    path = "ymk";
}
else if (appName.equals("YCN")) {
	storeName = "YouCam Nail";
    storeId = "1051710880";
    storePackage = "com.perfectcorp.ycn";
    path = "ycn";
}
else {
    storeName = "YouCam Perfect";
    storeId = "768469908";
    storePackage = "com.cyberlink.youperfect";
    path = "ypa";
}

String referrer = request.getParameter("referrer");
String fry = request.getParameter("fry");

String platform;
String browser;
String userAgent = request.getHeader("user-agent");
if (userAgent.indexOf("iPhone") != -1 || userAgent.indexOf("iPad") != -1 || userAgent.indexOf("iPod") != -1) {
    platform = "iOS";
}
else if (userAgent.indexOf("Android") != -1) {
    platform = "Android";
}
else {
    platform = "Desktop";
}

if (userAgent.indexOf("Chrome/") != -1) {
    browser = "Chrome";
}
else if (userAgent.indexOf("Safari/") != -1) {
    browser = "Safari";
}
else {
    browser = "Others";
}

//System.out.println(platform + " on " + browser);
String redirectUrl = "https://" + Constants.getPftServiceDomain() + "/ap/" + path + "/deeplink.jsp";
DeeplinkLogDao deeplinkDao = BeanLocator.getBean("product.DeeplinkLogDao");
DeeplinkLog newLog = new DeeplinkLog();
newLog.setPlatform(platform);
newLog.setBrowser(browser);
if (appName != null) {
    newLog.setAppName(appName);
    try {
    	redirectUrl += "?appName=" + URLEncoder.encode(appName);
    } catch (Exception e) {}
}
if (appUrl != null) {
    newLog.setAppUrl(appUrl);
    try {
    	redirectUrl += "&appUrl=" + URLEncoder.encode(appUrl);
    } catch (Exception e) {}
}
if (referrer != null) {
    newLog.setReferrer(referrer);
    try {
    	redirectUrl += "&referrer=" + URLEncoder.encode(referrer);
    } catch (Exception e) {}
}
if (fry != null) {
	newLog.setFry(fry);
	try {
    	redirectUrl += "&fry=" + URLEncoder.encode(fry);
    } catch (Exception e) {}
}
deeplinkDao.create(newLog); 

Long postId = null;
try {
    postId = Long.parseLong(request.getParameter("postId"));
} catch (NumberFormatException e) {
    // TODO Auto-generated catch block
    //e.printStackTrace();
}

String locale = null;
try {
	locale = request.getParameter("locale");
} catch (Exception e) {
	
}

String title = "PERFECT - A FUSION OF BEAUTY";
String description = "YouCam Perfect Selfie beautify app, YouCam Makeup virtual makeup app and Beauty Circle beauty community with mobile ecommerce integration.";
String imageUrl = "http://www.perfectcorp.com/stat/product/CyberLink_app/Perfect_Corp/enu/img/slider_img_m.jpg";
String contestURL = "";
String bcWebsite = "https://" + Constants.getBCWebsiteDomain();

PostDao postDao = BeanLocator.getBean("post.PostDao");
if (postId != null && postDao.exists(postId)) {
    Post post = postDao.findById(postId);
    title = post.getTitle();
    description = post.getContent();
    imageUrl = post.getAttachments().get(0).getAttachmentFile().getFileItems().get(0).getOriginalUrl();

    if (description == null) {
        description = "";
    }
    
    String postSource = post.getPostSource();
    if (postSource != null && postSource.equalsIgnoreCase("contest")) {
    	try {
    		URLContentReader urlContentReader = BeanLocator.getBean("web.urlContentReader.noCache");
    		Map<String, String> params = new HashMap<String, String>();
    		params.put("postId", postId.toString());
    		String contestDomain = null;
    		if("zh_CN".equalsIgnoreCase(locale))
        	    contestDomain = Constants.getCnContestDomain();
        	else
        	    contestDomain = Constants.getContestDomain();
    		String contestInfoUrl =  "http://" + contestDomain + "/prog/contest/init.do";
    		String resultJson = urlContentReader.post(contestInfoUrl, params);
    		JsonObject jsonObj = new JsonObject(resultJson);
    		JsonObject result = jsonObj.getJsonObject("result");
    		contestURL = result.getString("postURL");
    	} catch (Exception e) {
    		contestURL = "";
    	}
    }
}

if (contestURL == null || contestURL.isEmpty()) {
	int idx = appUrl.indexOf("://");
	if (idx > 0) {
		String tmpUrl = appUrl.substring(idx + "://".length());
		if (tmpUrl.contains("?"))
			tmpUrl += "&appName=" + appName;
		else
			tmpUrl += "?appName=" + appName;
		if (referrer != null) {
		    try {
		    	tmpUrl += "&referrer=" + URLEncoder.encode(referrer);
		    } catch (Exception e) {}
		}
		if (fry != null) {
			try {
				tmpUrl += "&fry=" + URLEncoder.encode(fry);
		    } catch (Exception e) {}
		}
		bcWebsite = bcWebsite + "/" + tmpUrl;
	}
}

%>
<html>

<head>

    <!-- Facebook -->
    <c:set var="randVer"><%=contestURL%></c:set>
    <c:if test="${randVer eq ''}">
    	<meta property="al:ios:url" content="<%=appUrl%>" />
    	<meta property="al:ios:app_store_id" content="<%=storeId%>"/>
    	<meta property="al:ios:app_name" content="<%=storeName%>" />
    	<meta property="al:android:url" content="<%=appUrl%>" />
    	<meta property="al:android:app_name" content="<%=storeName%>" />
	    <meta property="al:android:package" content="<%=storePackage%>" />
	    <meta property="al:web:url" content="http://www.perfectcorp.com/" />
	    <meta property="al:web:should_fallback" content="false" />
    </c:if>

    <!-- Facebook -->
    <meta property="og:title" content="<%=title%>" />
    <meta property="og:description" content="<%=description%>" />
    <meta property="og:type" content="article" />
    <!-- Image url cannot be empty string, otherwise facebook will return error -->
    <meta property="og:image" content="<%=imageUrl%>" />

    <!-- Twitter, Line, Weibo, etc -->
    <title><%=title%></title>

    <link rel="stylesheet" type="text/css" href="./deeplink.css">
    <script src="/common/jquery-1.11.3.min.js"></script>
    <script>
    var hidden, visibilityChange, supportVisiblityApi;
    var YBC_APP_NAME = "YBC"
    var YMK_APP_NAME = "YMK"
    var YCP_APP_NAME = "YPA"
    var YCN_APP_NAME = "YCN"

    var YBC_PACKAGE_NAME = "com.perfectcorp.beautycircle"
    var YMK_PACKAGE_NAME = "com.cyberlink.youcammakeup"
    var YCP_PACKAGE_NAME = "com.cyberlink.youperfect"
    var YCN_PACKAGE_NAME = "com.perfectcorp.ycn"

    var PARAM_ITUNES = "?pt=117886073&mt=8";
    var YBC_ITUNES = "itms-apps://itunes.apple.com/app/id1066152001";
    var YMK_ITUNES = "itms-apps://itunes.apple.com/app/id863844475";
    var YCP_ITUNES = "itms-apps://itunes.apple.com/app/id768469908";
    var YCN_ITUNES = "itms-apps://itunes.apple.com/app/id1051710880";
    var PARAM_MARKET = "utm_source=partner&utm_medium=QR";
    var YBC_MARKET = "market://details?id=com.perfectcorp.beautycircle&referrer=";
    var YMK_MARKET = "market://details?id=com.cyberlink.youcammakeup&referrer=";
    var YCP_MARKET = "market://details?id=com.cyberlink.youperfect&referrer=";
    var YCN_MARKET = "market://details?id=com.perfectcorp.ycn&referrer=";
    var FLURRY = "https://ad.apps.fm/";
    var WEBSITE = "http://www.perfectcorp.com/"

    // Set the name of the "hidden" property and the change event for visibility        
    if (typeof document.hidden !== "undefined") {
      hidden = "hidden";
      visibilityChange = "visibilitychange";
    } else if (typeof document.mozHidden !== "undefined") { // Firefox up to v17
      hidden = "mozHidden";
      visibilityChange = "mozvisibilitychange";
    } else if (typeof document.webkitHidden !== "undefined") { // Chrome up to v32, Android up to v4.4, Blackberry up to v10
      hidden = "webkitHidden";
      visibilityChange = "webkitvisibilitychange";
    }

    var handleVisibilityChange = function() {
    }

    if (typeof document.addEventListener === "undefined" || typeof document[hidden] === "undefined") {
    	supportVisiblityApi = false;
    } else { 
    	supportVisiblityApi = true;  
      	document.addEventListener(visibilityChange, handleVisibilityChange, false);
    }
        
    $(document).ready(function() {

        var redirect = function(location) {
            $('body').append($('<iframe></iframe>').attr('src', location).css({
                width: 1,
                height: 1,
                position: 'absolute',
                top: 0,
                left: 0
            }));
        };

		var getIOSVersion = function(userAgent) {
			var versions = String(userAgent.match(/[0-9]+_[0-9]+/)).split('_');
			var iosVersion = parseFloat(String(versions[0] + '.' + versions[1]));
			return iosVersion;
		};

		var fallbackFunc = function(fallbackUrl) {
			if(!supportVisiblityApi) {
				setTimeout(function() {
                    if (!document.webkitHidden) {
                        window.location.replace(fallbackUrl);
                    }
                }, 300);         
			}
			else {
				setTimeout(function() {
					if (!document[hidden]) {
                        window.location.replace(fallbackUrl);
                    }
				}, 1500);    
			}
		};
		
        var appUrl = "<%= appUrl%>";
        var appName = "<%= appName%>";
        var referrer = "<%= referrer%>";
        var fry = "<%= fry%>";
        var contestURL = "<%= contestURL%>";
        var bcWebsite = "<%= bcWebsite%>";
        var redirectUrl = "<%= redirectUrl%>";
        // var parts = appUrl.match(/^([^:]+):(.*)/);
        // var scheme = parts[1];
        // var action = parts[2];

        if (navigator.userAgent.match(/iPhone|iPad|iPod/)) {
            // iOS
            if(navigator.userAgent.toLowerCase().match(/MicroMessenger/i) == "micromessenger") {
                // Weixin
                $("#popweixin").find("img").attr("src", "./hint_iOS.png")
                $("#popweixin").css("display", "block");           
            }
            else if(navigator.userAgent.toLowerCase().match(/Weibo/i) == "weibo") {
                // Weixin
                $("#popweixin").find("img").attr("src", "./hint_weibo_iOS.png")
                $("#popweixin").css("display", "block");           
            }
            else {
            	if (contestURL == "") {
	                var fallback = YBC_ITUNES;
	
	                if (appName == YBC_APP_NAME) {
	                    // YBC
	                    fallback = YBC_ITUNES;
	                } else if (appName == YMK_APP_NAME) {
	                    // YMK
	                    fallback = YMK_ITUNES;
	                } else if (appName == YCP_APP_NAME) {
	                    // YCP
	                    fallback = YCP_ITUNES;
	                } else if (appName == YCN_APP_NAME) {
	                    // YCN
	                    fallback = YCN_ITUNES;
	                } else {
	                    // DEFAULT
	                }
	                
	                if (referrer != null) {
	                	PARAM_ITUNES += "&ct=" + referrer;
	                	fallback += PARAM_ITUNES;
	                }
	
					if(getIOSVersion(navigator.userAgent) >= 9)
						window.location.replace(redirectUrl);
					else
						redirect(appUrl);
	
					// Workaround : Spec not ready, only YBC fallback to bcWebsite.
					if (appName == YBC_APP_NAME) {
	                    // YBC
	                    fallback = bcWebsite;
	                }
					fallbackFunc(fallback);
            	} else {
            		window.location.replace(contestURL);
            	}
            }
        } else if (navigator.userAgent.match(/Android/)) {
            // Android

            if(navigator.userAgent.toLowerCase().match(/MicroMessenger/i) == "micromessenger") {
                // Weixin
                $("#popweixin").find("img").attr("src", "./hint_android.png")
                $("#popweixin").css("display", "block");           
            }
            else if(navigator.userAgent.toLowerCase().match(/Weibo/i) == "weibo") {
                // Weixin
                $("#popweixin").find("img").attr("src", "./hint_weibo_android.png")
                $("#popweixin").css("display", "block");           
            }
            else if (navigator.userAgent.match(/Chrome/)) {
                
            	if (contestURL == "") {
	                var fallback = YBC_MARKET;
	
	                if (appName == YBC_APP_NAME) {
	                    // YBC
	                    fallback = YBC_MARKET;
	                } else if (appName == YMK_APP_NAME) {
	                    // YMK
	                    fallback = YMK_MARKET;
	                } else if (appName == YCP_APP_NAME) {
	                    // YCP
	                    fallback = YCP_MARKET;
	                } else if (appName == YCN_APP_NAME) {
	                    // YCN
	                    fallback = YCN_MARKET;
	                } else {
	                    // DEFAULT
	                }
	
	                if (fry == "null") {
	                	if (referrer != null) {
	                		PARAM_MARKET += "&utm_campaign=" + referrer;
	                	}
	                	fallback += encodeURIComponent(PARAM_MARKET);
	                } else {
	                	fallback = FLURRY + fry;
	                }
	
	                window.location.replace(appUrl);
	                
	             	// Workaround : Spec not ready, only YBC fallback to bcWebsite.
					if (appName == YBC_APP_NAME) {
	                    // YBC
	                    fallback = bcWebsite;
	                }
	
	                fallbackFunc(fallback);
            	} else {
            		window.location.replace(contestURL);
            	}
            } else {
                
            	if (contestURL == "") {
	                var fallback = YBC_MARKET;
	
	                if (appName == YBC_APP_NAME) {
	                    // YBC
	                    fallback = YBC_MARKET;
	                } else if (appName == YMK_APP_NAME) {
	                    // YMK
	                    fallback = YMK_MARKET;
	                } else if (appName == YCP_APP_NAME) {
	                    // YCP
	                    fallback = YCP_MARKET;
	                } else if (appName == YCN_APP_NAME) {
	                    // YCN
	                    fallback = YCN_MARKET;
	                } else {
	                    // DEFAULT
	                }
	
	                if (fry == "null") {
	                	if (referrer != null) {
	                		PARAM_MARKET += "&utm_campaign=" + referrer;
	                	}
	                	fallback += encodeURIComponent(PARAM_MARKET);
	                } else {
	                	fallback = FLURRY + fry;
	                }
	
	                redirect(appUrl);
	                
	             	// Workaround : Spec not ready, only YBC fallback to bcWebsite.
					if (appName == YBC_APP_NAME) {
	                    // YBC
	                    fallback = bcWebsite;
	                }
	                
	                fallbackFunc(fallback);
            	} else {
            		window.location.replace(contestURL);
            	}
            }

            //		window.location.replace("intent://feed/#Intent;package=com.cyberlink.youcammakeup;scheme=ybc;end;");
            //		window.location.replace(URL);
        } else {
            // Not mobile
            if (contestURL == "")
            	window.location.replace(bcWebsite);
            else
        		window.location.replace(contestURL);
        }
    });
    </script>
</head>

<body>
    <!-- Twitter, Line, Weibo, etc -->
	<br><p style="font-size:40pt" align="center">You’re about to be redirected.</p>

    <div id='popweixin'>
        <div class='tip top2bottom animate-delay-1'>
            <img src='./hint_iOS.png'/>
        </div>
    </div>
</body>

</html>
