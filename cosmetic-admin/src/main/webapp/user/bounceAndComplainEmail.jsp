<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.sql.DataSource"%>
<%@ page import="com.cyberlink.core.BeanLocator"%>
<%@ page import="java.sql.PreparedStatement"%>
<%@ page import="com.cyberlink.cosmetic.action.backend.service.BounceAndComplainEmailService"%>
<%@ page import="java.sql.Connection"%>
<%
try {
	BounceAndComplainEmailService bounceAndComplainEmailService = BeanLocator.getBean("backend.BounceAndComplainEmailService");
	out.println("Control Paremeters :<BR /> isRun = {true,false,} , null will show status ,Default value = false;<BR />");

	if(request.getParameter("isReady") != null){
		if(request.getParameter("isReady").equals("true")){
			bounceAndComplainEmailService.start();
		} else if (request.getParameter("isReady").equals("false")){
			bounceAndComplainEmailService.stop();
		}	
	}
	if(request.getParameter("isDebugMode") != null){
		if(request.getParameter("isDebugMode").equals("true")){
			bounceAndComplainEmailService.setIsDebugMode();
		} else if (request.getParameter("isDebugMode").equals("false")){
			bounceAndComplainEmailService.setIsNotDebugMode();
		}	
	}
	
	String serviceStatus = bounceAndComplainEmailService.getStatus();
	Boolean isReady = bounceAndComplainEmailService.IsReady();
	Boolean isRunning = bounceAndComplainEmailService.IsReady();
	Boolean isDebugMode = bounceAndComplainEmailService.IsDebugMode();
	out.println("<BR />");
	out.println(serviceStatus);
	out.println("<BR />");
	
	if(request.getParameter("execNow") != null){
		if(request.getParameter("execNow").equals("true")){
			bounceAndComplainEmailService.exec();
		}
	}
	
	if(!isReady){
		out.println("<input type=\"button\" value=\"Set isReady true\" onclick=\"window.location.assign('./bounceAndComplainEmail.jsp?isReady=true');\" >");
	}
	if(isReady){
		out.println("<input type=\"button\" value=\"Set isReady false\" onclick=\"window.location.assign('./bounceAndComplainEmail.jsp?isReady=false');\" >");
	}
	if(isReady && isRunning){
		out.println("<input type=\"button\" value=\"Execute now\" onclick=\"window.location.assign('./bounceAndComplainEmail.jsp?execNow=true');\" >");
	}
	out.println("<BR />");
	out.println("<BR />");
	out.println("Debug mode is " + isDebugMode);
	out.println("<BR />");
	if(isDebugMode){
		out.println("<input type=\"button\" value=\"Set debug mode false\" onclick=\"window.location.assign('./bounceAndComplainEmail.jsp?isDebugMode=false');\" >");
	}else{
		out.println("<input type=\"button\" value=\"Set debug mode true\" onclick=\"window.location.assign('./bounceAndComplainEmail.jsp?isDebugMode=true');\" >");
	}
} catch (Throwable e) {}
%>