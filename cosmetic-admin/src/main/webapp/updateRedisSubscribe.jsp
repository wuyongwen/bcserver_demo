<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ page import="javax.sql.DataSource"
%><%@ page import="com.cyberlink.core.BeanLocator"
%><%@ page import="java.sql.PreparedStatement"
%><%@ page import="com.cyberlink.cosmetic.modules.user.repository.SubscribeRepository"
%><%@ page import="java.sql.Connection"
%><%
try {
	SubscribeRepository u = BeanLocator.getBean("user.subscribeRepository");
    u.updateAll();
    out.print("success");
} catch (Throwable e) {
    out.print("fail");
}
%>