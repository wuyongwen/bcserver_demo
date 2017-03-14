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
	Integer postId;
	
	params = new HashMap<String, String>();
	params.put("method", "GET");	
	returnJson = urlContentReader.post("http://" + websiteDomain + "/api/campaign/list-group.action?locale=en_US", params);
	contentMap = objectMapper.readValue(returnJson, Map.class);
	mapArray = (List<Map<String, Object>>) contentMap.get("results");
	if (!mapArray.get(0).containsKey("id")) {
		out.print("fail");
		return;		
	}
	out.print("success");
} catch (Throwable e) {
    out.print("fail");
}
%>