<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ page import="javax.sql.DataSource"
%><%@ page import="com.cyberlink.core.BeanLocator"
%><%@ page import="java.sql.PreparedStatement"
%><%@ page import="com.cyberlink.cosmetic.modules.post.service.AutoPostService"
%><%@ page import="java.sql.Connection"
%><%
try {
	AutoPostService autoPostService = BeanLocator.getBean("post.AutoPostService");
	autoPostService.startAutoPostThread();
    out.print("AutoPostService start success");
} catch (Throwable e) {
    out.print("AutoPostService start fail");
}
%>