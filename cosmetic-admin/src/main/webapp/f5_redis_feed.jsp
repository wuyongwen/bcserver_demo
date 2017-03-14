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
try {
	if(!Constants.getIsRedisFeedEnable()) {
		out.print("success");
		return;
	}
	
	URLContentReader urlContentReader = BeanLocator.getBean("web.urlContentReader.redirect");
	ObjectMapper objectMapper = BeanLocator.getBean("web.objectMapper");
	Map<String, String> params = new HashMap<String, String>();
	params.put("method", "GET");
	params.put("userId", "1");
	params.put("offset", "1");
	params.put("limit", "1");

	String returnJson = urlContentReader.post("http://" + Constants.getWebsiteDomain() + "/api/v4.4/feed/list-my-feed.action", params);
	Map<String, Object> contentMap = objectMapper.readValue(returnJson, Map.class);
	List<Map<String, Object>> mapArray = (List<Map<String, Object>>) contentMap.get("results");
	if (!mapArray.get(0).containsKey("postId")) {	
		out.print("fail");
		return;
	}

	out.print("success");
} catch (Throwable e) {
    out.print("fail");
}
%>