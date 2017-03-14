<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ page import="javax.sql.DataSource"
%><%@ page import="com.cyberlink.core.BeanLocator"
%><%@ page import="java.sql.PreparedStatement"
%><%@ page import="com.cyberlink.cosmetic.modules.notify.service.NotifyService"
%><%@ page import="java.sql.Connection"
%><%
try {
	NotifyService u = BeanLocator.getBean("notify.NotifyService");
    u.deleteOldNotify();
    //u.realDelete();
    out.print("success");
} catch (Throwable e) {
    out.print("fail"+e.getMessage());
}
%>