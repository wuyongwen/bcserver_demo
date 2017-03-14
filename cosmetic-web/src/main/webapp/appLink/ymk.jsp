<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.cyberlink.core.BeanLocator"%>
<%@ page import="com.cyberlink.core.web.utl.URLContentReader"%>
<%@ page import="com.cyberlink.cosmetic.Constants"%>
<%@ page import="com.cyberlink.cosmetic.modules.post.model.Post"%>
<%@ page import="com.cyberlink.cosmetic.modules.post.dao.PostDao"%>
<%@ page import="java.net.URLDecoder"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.regex.Matcher"%>
<%@ page import="java.util.regex.Pattern"%>
<%@ page import="com.restfb.json.JsonObject"%>
<%@ include file="/common/taglibs.jsp"%>
<%
String appUrl = request.getParameter("appUrl") ;
if( appUrl == null ){
	appUrl = "ymk://";
} else {
	try {
		appUrl = URLDecoder.decode(appUrl, "UTF-8");
	} catch (Exception e) {
	    e.printStackTrace();
	    appUrl = "ymk://";
	}
}

String contestURL = "";
if (appUrl != null && !appUrl.isEmpty()) {
	if (appUrl.contains("post/")) {
		try {
			Pattern pattern = Pattern.compile("[0-9]+");
			Matcher matcher = pattern.matcher(appUrl);
			
			if (matcher.find()) {
				Long postId = Long.parseLong(matcher.group());		
				PostDao postDao = BeanLocator.getBean("post.PostDao");
				if (postId != null && postDao.exists(postId)) {
					Post post = postDao.findById(postId);
					String postSource = post.getPostSource();
					
					if (postSource != null && postSource.equalsIgnoreCase("contest")) {
						URLContentReader urlContentReader = BeanLocator.getBean("web.urlContentReader.noCache");
			    		Map<String, String> params = new HashMap<String, String>();
			    		params.put("postId", postId.toString());
			    		String contestInfoUrl =  "http://" + Constants.getContestDomain() + "/prog/contest/init.do";
			    		String resultJson = urlContentReader.post(contestInfoUrl, params);
			    		JsonObject jsonObj = new JsonObject(resultJson);
			    		JsonObject result = jsonObj.getJsonObject("result");
			    		contestURL = result.getString("postURL");
					}
				}
			}
		} catch (Exception e) {
    		contestURL = "";
    	}
	}
}
%>
<html>
<head>
	<title>redirect</title>
	<c:set var="randVer"><%=contestURL%></c:set>
    <c:if test="${randVer eq ''}">
		<meta property="al:ios:app_store_id" content="863844475"/>
		<meta property="al:ios:app_name" content="YouCam Makeup" />
		<meta property="al:ios:url" content="<%=appUrl%>" />
		<meta property="al:web:url"
		          content="http://www.perfectcorp.com/" />
		<meta property="al:android:url" content="<%=appUrl%>" />
		<meta property="al:android:app_name" content="YouCam Makeup" />
	    <meta property="al:android:package" content="com.cyberlink.youcammakeup" />
		<meta property="al:android:app_name" content="YouCam Perfect" />
		<meta property="al:android:package" content="com.cyberlink.youperfect" />
		<meta property="al:android:app_name" content="YouCam Nail" />
		<meta property="al:android:package" content="com.perfectcorp.ycn" />
		<meta property="al:web:should_fallback" content="false" />
	</c:if> 
</head>
<body>
<script>
	var contestURL = "<%= contestURL%>";
	
	if (contestURL == "")
		window.location = 'http://www.perfectcorp.com/';
	else
		window.location = contestURL;
</script>
</body>
</html>