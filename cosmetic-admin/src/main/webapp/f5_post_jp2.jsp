<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ page import="javax.sql.DataSource"
%><%@ page import="com.cyberlink.core.BeanLocator"
%><%@ page import="java.sql.PreparedStatement"
%><%@ page import="com.cyberlink.core.web.utl.URLContentReader"
%><%@ page import="com.fasterxml.jackson.databind.ObjectMapper"
%><%@ page import="com.cyberlink.cosmetic.Constants"
%><%@ page import="java.util.*"
%><%@ page import="java.sql.Connection"
%><%@ page import="java.util.HashMap"
%><%@ page import="java.util.Locale"
%><%@ page import="java.util.Map"

%><%
String websiteDomain = "bc-api-jp2.cyberlink.com"; 
try {
	URLContentReader urlContentReader = BeanLocator.getBean("web.urlContentReader.redirect");
	ObjectMapper objectMapper = BeanLocator.getBean("web.objectMapper");
	Map<String, Object> contentMap;
	Map<String, String> params;
	List<Map<String, Object>> mapArray;
	String returnJson;
	List<String> userIds = new ArrayList<String>();
	Map<String, String> circleIds = new HashMap<String, String>();
	
	if (websiteDomain.equalsIgnoreCase("bc-api-eu.cyberlink.com")) {
		userIds.add("46001");
		userIds.add("48001");
		//userIds.add("50001");
	} else if (websiteDomain.equalsIgnoreCase("bc-api-jp.cyberlink.com")) {
		userIds.add("40001");
		userIds.add("42001");
		userIds.add("44001");
	} else {
		userIds.add("38001");
	}
	/*for (String userId : userIds) {
		params = new HashMap<String, String>();
		params.put("method", "GET");
		params.put("userId", userId);
		returnJson = urlContentReader.post("http://" + websiteDomain + "/api/post/list-post-by-user.action", params);
		contentMap = objectMapper.readValue(returnJson, Map.class);
		mapArray = (List<Map<String, Object>>) contentMap.get("results");
		if (!mapArray.get(0).containsKey("postId")) {
			out.print("fail");
			return;		
		}
	}*/

	params = new HashMap<String, String>();
	params.put("method", "GET");
	params.put("postId", "114875777366885389");
	returnJson = urlContentReader.post("http://" + websiteDomain + "/api/post/list-sub-post.action", params);
	contentMap = objectMapper.readValue(returnJson, Map.class);
	mapArray = (List<Map<String, Object>>) contentMap.get("results");
	if (!mapArray.get(0).containsKey("subPostId")) {	
		out.print("fail");
		return;
	}

	params = new HashMap<String, String>();
	params.put("method", "GET");
	params.put("userId", "12001");
	params.put("locale", "en_US");
	returnJson = urlContentReader.post("http://" + websiteDomain + "/api/feed/list-my-feed.action", params);
	contentMap = objectMapper.readValue(returnJson, Map.class);
	mapArray = (List<Map<String, Object>>) contentMap.get("results");
	if (!mapArray.get(0).containsKey("postId")) {	
		out.print("fail");
		return;
	}
	circleIds.put("143", "en_US");
	circleIds.put("164","zh_TW");
	circleIds.put("171","zh_CN");
	circleIds.put("178","ja_JP");
	circleIds.put("185","fr_FR");
	circleIds.put("192","de_DE");

	for (String circleId : circleIds.keySet()) {
		params = new HashMap<String, String>();
		params.put("method", "GET");
		params.put("circleId", circleId);
		params.put("locale", circleIds.get(circleId));
		params.put("postStatus", "Published");
		params.put("sortBy", "Date");
		returnJson = urlContentReader.post("http://" + websiteDomain + "/api/post/list-post-by-circle.action", params);
		contentMap = objectMapper.readValue(returnJson, Map.class);
		mapArray = (List<Map<String, Object>>) contentMap.get("results");
		if (!mapArray.get(0).containsKey("postId")) {	
			out.print("fail");
			return;
		}
	}

	/*for (String circleId : circleIds.keySet()) {
		params = new HashMap<String, String>();
		params.put("method", "GET");
		params.put("circleId", circleId);
		params.put("locale", circleIds.get(circleId));
		params.put("postStatus", "Published");
		params.put("sortBy", "Popularity");
		returnJson = urlContentReader.post("http://" + websiteDomain + "/api/post/list-post-by-circle.action", params);
		contentMap = objectMapper.readValue(returnJson, Map.class);
		if (!contentMap.get("totalSize").toString().equalsIgnoreCase("0")) {
			mapArray = (List<Map<String, Object>>) contentMap.get("results");
			if (!mapArray.get(0).containsKey("postId")) {	
				out.print("fail");
				return;
			}
		}
	}*/
	
	params = new HashMap<String, String>();
	params.put("method", "GET");
	params.put("targetId", "116254909736682496");
	params.put("targetType", "Post");
	params.put("curUserId", "38001");

	returnJson = urlContentReader.post("http://" + websiteDomain + "/api/post/list-comment.action", params);
	contentMap = objectMapper.readValue(returnJson, Map.class);
	mapArray = (List<Map<String, Object>>) contentMap.get("results");
	if (!mapArray.get(0).containsKey("commentId")) {	
		out.print("fail");
		return;
	}

	out.print("success");
} catch (Throwable e) {
    out.print("fail");
}
%>