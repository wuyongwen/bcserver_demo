<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ page import="javax.sql.DataSource"
%><%@ page import="com.cyberlink.core.BeanLocator"
%><%@ page import="java.sql.PreparedStatement"
%><%@ page import="java.lang.reflect.Field"
%><%@ page import="com.cyberlink.cosmetic.modules.post.service.AutoPostService"
%><%@ page import="com.cyberlink.cosmetic.modules.post.service.AutoPostService.PostTask"
%><%@ page import="java.sql.Connection"
%><%@ page import="org.springframework.util.ReflectionUtils"
%><%@ page import="java.util.Queue"
%><%@ page import="java.util.LinkedList"
%><%@ page import="java.util.List"
%><%@ page import="java.util.ArrayList"
%><%@ page import="java.util.Iterator"
%><%@ page import="com.restfb.json.JsonArray"
%><%@ page import="com.restfb.json.JsonObject"
%><%@ page import="java.text.SimpleDateFormat"
%><%@ page import="com.cyberlink.cosmetic.modules.post.service.ArticleData"
%><%@ page import="java.util.HashMap"
%><%@ page import="java.util.Map"
%><%@ page import="java.io.FileWriter"
%><%@ page import="java.io.IOException"
%><%@ page import="java.io.File"
%><%@ page import="com.cyberlink.cosmetic.Constants"
%><%
	AutoPostService autoPostService = BeanLocator.getBean("post.AutoPostService");
	final Field field = ReflectionUtils.findField(autoPostService.getClass(), "postQueue");
    field.setAccessible(Boolean.TRUE);
    Queue<PostTask> postQueue = new LinkedList<PostTask>();
    postQueue = (Queue<PostTask>)field.get(autoPostService);
    Iterator<PostTask> iterator = postQueue.iterator();
    int fileIdx = 1;
    while(iterator.hasNext()) {
    	PostTask task = iterator.next();
    	JsonObject jsonObj = new JsonObject();
    	// startTime
    	SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	jsonObj.put("startTime", dateFormatGmt.format(task.getStartTime()));
    	jsonObj.put("requestTime", dateFormatGmt.format(task.getRequestTime()));
    	jsonObj.put("postRegion", task.getPostRegion());
    	jsonObj.put("postDuration", task.getPostDuration());
    	jsonObj.put("postNumber", task.getPostNumber());
    	jsonObj.put("postCircles", task.getPostCircles().get(0));
    	jsonObj.put("articleSelNumber", task.getArticleSelNumber());
    	
    	JsonArray userArray = new JsonArray();
    	for (Long l : task.getUserList()) {
    		userArray.put(l);
    	}
    	jsonObj.put("userList", userArray);

    	JsonArray articleArray = new JsonArray();
    	for (ArticleData art : task.getArticleList()) {
    		JsonObject subObj = new JsonObject();
    		subObj.put("title", art.getTitle());
    		subObj.put("link", art.getUrl());
    		subObj.put("content", art.getContent());
    		subObj.put("image", art.getImage());
    		subObj.put("articleType", art.getArticleType().toString());
    		subObj.put("articleId", art.getArticleId());
    		subObj.put("importFile", art.getImportFile());
    		subObj.put("checked", art.getChecked());
    		subObj.put("index", art.getIndex());
    		subObj.put("order", art.getOrder());
    		
    		articleArray.put(subObj);
    	}
    	jsonObj.put("articleList", articleArray);
    	
    	try {
    		FileWriter file;
        	file = new FileWriter(Constants.getLoggingPath() + String.format("/task(%d).json", fileIdx));
        	file.write(jsonObj.toString()); 
        	file.flush();
            file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	fileIdx++;
    }
    field.setAccessible(Boolean.FALSE);
    out.print("AutoPostService output success");
%>