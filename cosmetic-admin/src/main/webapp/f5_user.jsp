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
	URLContentReader urlContentReader = BeanLocator.getBean("web.urlContentReader.redirect");
	ObjectMapper objectMapper = BeanLocator.getBean("web.objectMapper");
	Map<String, Object> contentMap;
	Map<String, String> params;
	Map<String, Object> result;
	List<Map<String, Object>> mapArray;
	String returnJson;
	String token;
	
	params = new HashMap<String, String>();
	params.put("method", "GET");
    params.put("email", "ivon_chang@gocyberlink.com");
    params.put("password", "le3849281");
	returnJson = urlContentReader.post("https://" + Constants.getWebsiteDomain() + "/api/user/sign-in-BC.action?uuid=3_xnuz7y96C803LcshgeFXBVOen3D-bJVhoYhdKfPvw~&locale=en_US&apnsType=apns&apnsToken=null", params);
	contentMap = objectMapper.readValue(returnJson, Map.class);
	if (!contentMap.containsKey("token")) {	
		out.print("fail");
		return;
	} else {
		token = (String)contentMap.get("token");
	}

	params = new HashMap<String, String>();
	params.put("method", "GET");
    params.put("userId", "4001");
	returnJson = urlContentReader.post("https://" + Constants.getWebsiteDomain() + "/api/user/info.action?uuid=3_xnuz7y96C803LcshgeFXBVOen3D-bJVhoYhdKfPvw~", params);
	contentMap = objectMapper.readValue(returnJson, Map.class);
	result = (Map<String, Object>)contentMap.get("result");
	if (!result.containsKey("id")) {	
		out.print("fail");
		return;
	}

	params = new HashMap<String, String>();
	params.put("method", "GET");
    params.put("token", token);
    params.put("description", "test");
    returnJson = urlContentReader.post("https://" + Constants.getWebsiteDomain() + "/api/user/update.action", params);
	contentMap = objectMapper.readValue(returnJson, Map.class);
	if (!contentMap.containsKey("userId")) {	
		out.print("fail");
		return;
	}

	params = new HashMap<String, String>();
	params.put("method", "GET");
    params.put("userType", "CL");
    returnJson = urlContentReader.post("http://" + Constants.getWebsiteDomain() + "/api/user/list-user-byType.action?locale=en_US", params);
	contentMap = objectMapper.readValue(returnJson, Map.class);
	mapArray = (List<Map<String, Object>>) contentMap.get("results");
	if (!mapArray.get(0).containsKey("id")) {
		out.print("fail");
		return;		
	}
	
	params = new HashMap<String, String>();
	params.put("method", "GET");
    returnJson = urlContentReader.post("http://" + Constants.getWebsiteDomain() + "/api/user/list-default-cover.action", params);
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