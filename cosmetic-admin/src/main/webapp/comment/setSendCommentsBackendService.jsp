<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.sql.DataSource"%>
<%@ page import="com.cyberlink.core.BeanLocator"%>
<%@ page import="java.sql.PreparedStatement"%>
<%@ page import="com.cyberlink.cosmetic.action.backend.service.CommentsMailService"%>
<%@ page import="java.sql.Connection"%>
<%
try {
	CommentsMailService commentsMailService = BeanLocator.getBean("backend.CommentsMailService");
	out.println("Control Paremeters :<BR /> isRun = {true,false,} , null will show status");
	out.println("<BR /><BR />");
	if(request.getParameter("isRun") != null){
		if(request.getParameter("isRun").equals("true")){
			commentsMailService.start();
			out.println("CommentsMailService start success");
		} else if (request.getParameter("isRun").equals("false")){
			commentsMailService.stop();
			out.println("CommentsMailService stop success");
		}	
	}else {
		out.println(commentsMailService.getStatus());
	}
	out.println("<BR />");
	
	if(request.getParameter("execNow") != null){
		if(request.getParameter("execNow").equals("true")){
			commentsMailService.execNow();
		}
	}
	
	if(commentsMailService.getStatus().equals("CommentsMailService isn't running")){
		out.println("<input type=\"button\" value=\"Set isRun true\" onclick=\"window.location.assign('./setSendCommentsBackendService.jsp?isRun=true');\" >");
	}
	if(commentsMailService.getStatus().equals("CommentsMailService is running")){
		out.println("<input type=\"button\" value=\"Set isRun false\" onclick=\"window.location.assign('./setSendCommentsBackendService.jsp?isRun=false');\" >");
	}
	if(commentsMailService.getStatus().equals("CommentsMailService is running")){
		out.println("<input type=\"button\" value=\"Execute now\" onclick=\"window.location.assign('./setSendCommentsBackendService.jsp?execNow=true');\" >");
	}
} catch (Throwable e) {}
%>