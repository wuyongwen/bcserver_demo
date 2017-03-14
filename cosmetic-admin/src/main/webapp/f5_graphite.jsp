<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>
<%@ page import="net.sourceforge.stripes.util.Base64"%>
<%@ page import="org.jsoup.Connection"%>
<%@ page import="org.jsoup.Jsoup"%>
<%@ page import="org.jsoup.nodes.Document"%>
<%@ page import="org.jsoup.nodes.Element"%>
<%@ page import="com.cyberlink.core.BeanLocator"%>
<%@ page import="com.cyberlink.cosmetic.Constants"%>
<%@ page import="com.fasterxml.jackson.core.type.TypeReference"%>
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper"%>

<%!

	public void graphiteTest(List<String> targets, Double threshold) throws Exception {
	    String username = "cosmetic";
	    String password = "clt#1";
	    String login = username + ":" + password;
	    String base64login = new String(Base64.encodeBytes(login.getBytes()));
	    String statsdHost = Constants.getStatSDHost();
	    
        String url = "http://" + statsdHost +"/render?";
        for(String t : targets) {
            url += "target=sumSeries(production."+t+".collectd.memory.memory-cached,production."+t+".collectd.memory.memory-buffered,production."+t+".collectd.memory.memory-free)&";
        }
        
        url += "format=json&from=-2min&until=-1min";
		Connection conn = Jsoup.connect(url).ignoreContentType(true).header("Authorization", "Basic " + base64login);
        Document document = conn.get();
        if(document == null)
            throw new Exception("Null response document");
        Element body = document.body();
        if(body == null)
            throw new Exception("Null response body");
        String returnJson = body.text();
        if(returnJson == null || returnJson.length() <= 0)
            throw new Exception("Empty result");
        ObjectMapper objectMapper = BeanLocator.getBean("web.objectMapper");
        List<Map<String, Object>> resultList = objectMapper.readValue(returnJson, new TypeReference<List<Map<String, Object>>>() {});
        if(resultList == null || resultList.size() <= 0)
            throw new Exception("Empty result list");
        for(Map<String, Object> result : resultList) {
            List<List<Double>> dataPoints = (List<List<Double>>)result.get("datapoints");
            if(dataPoints == null || dataPoints.size() <= 0)
                throw new Exception("Empty data points");
            List<Double> dataPoint = dataPoints.get(0);
            if(dataPoint == null || dataPoint.size() <= 0)
                throw new Exception("Empty data point");
            Double data = dataPoint.get(0);
            if(data == null || data < threshold)
                throw new Exception("Alert !!!");
        }
	}

%>

<%
try {
	if(!Constants.getIsStatsdEnable()) {
		out.print("success");
		return;
	}

	List<String> targets = new ArrayList<String>();
	targets.add("cosmetic-redis-005");
	targets.add("cosmetic-redis-006");
	targets.add("cosmetic-redis-007");
	targets.add("cosmetic-redis-004");
    graphiteTest(targets, 1000000000D);
	out.print("success");
} catch (Throwable e) {
    e.printStackTrace();
    out.print("fail");
}
%>
