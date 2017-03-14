<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ page import="javax.sql.DataSource"
%><%@ page import="com.cyberlink.core.BeanLocator"
%><%@ page import="java.sql.PreparedStatement"
%><%@ page import="com.cyberlink.cosmetic.modules.post.service.AutoPostService"
%><%@ page import="java.sql.Connection"
%><%@ page import="org.springframework.util.ReflectionUtils"
%><%@ page import="java.lang.reflect.Field"
%><%
try {
	AutoPostService autoPostService = BeanLocator.getBean("post.AutoPostService");
	final Field field = ReflectionUtils.findField(autoPostService.getClass(), "postExecuteThread");
    field.setAccessible(Boolean.TRUE);
    ReflectionUtils.setField(field, autoPostService, null);
    field.setAccessible(Boolean.FALSE);
	autoPostService.stopAutoPostThread();
    out.print("AutoPostService stop success");
} catch (Throwable e) {
    out.print("AutoPostService stop fail");
}
%>