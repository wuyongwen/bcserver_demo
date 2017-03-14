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
	String token;
	String productId;
	
	params = new HashMap<String, String>();
	params.put("method", "GET");
	returnJson = urlContentReader.post("http://" + websiteDomain + "/api/prod/listtypes.action?locale=en_US", params);
	contentMap = objectMapper.readValue(returnJson, Map.class);
	mapArray = (List<Map<String, Object>>) contentMap.get("results");
	if (!mapArray.get(0).containsKey("typeId")) {	
		out.print("fail");
		return;
	}

	params = new HashMap<String, String>();
	params.put("method", "GET");
	returnJson = urlContentReader.post("http://" + websiteDomain + "/api/prod/listbrands.action?locale=en_US", params);
	contentMap = objectMapper.readValue(returnJson, Map.class);
	mapArray = (List<Map<String, Object>>) contentMap.get("results");
	if (!mapArray.get(0).containsKey("id")) {	
		out.print("fail");
		return;
	}

	params = new HashMap<String, String>();
	params.put("method", "GET");
    returnJson = urlContentReader.post("http://" + websiteDomain + "/api/store/listPriceRangAndName.action?locale=en_US", params);
	contentMap = objectMapper.readValue(returnJson, Map.class);
	mapArray = (List<Map<String, Object>>) contentMap.get("results");
	if (!mapArray.get(0).containsKey("id")) {	
		out.print("fail");
		return;
	}

	params = new HashMap<String, String>();
	params.put("method", "GET");
    returnJson = urlContentReader.post("http://" + websiteDomain + "/api/product/ListProduct.action?locale=en_US", params);
	contentMap = objectMapper.readValue(returnJson, Map.class);
	mapArray = (List<Map<String, Object>>) contentMap.get("results");
	productId = (mapArray.get(0).get("productId")).toString();
	
	params = new HashMap<String, String>();
	params.put("productId", productId);
    returnJson = urlContentReader.post("http://" + websiteDomain + "/api/product/QueryProductInfo.action?locale=en_US", params);
	contentMap = objectMapper.readValue(returnJson, Map.class);
	mapArray = (List<Map<String, Object>>) contentMap.get("results");
	if (!mapArray.get(0).containsKey("productId")) {	
		out.print("fail");
		return;
	}

	params = new HashMap<String, String>();
	params.put("productId", String.valueOf(22223));
    returnJson = urlContentReader.post("https://" + websiteDomain + "/api/product/ListComment.action?uuid=3_xnuz7y96C803LcshgeFXBVOen3D-bJVhoYhdKfPvw~", params);
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