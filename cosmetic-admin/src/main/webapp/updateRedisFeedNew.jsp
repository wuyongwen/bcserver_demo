<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ page import="javax.sql.DataSource"
%><%@ page import="com.cyberlink.core.BeanLocator"
%><%@ page import="java.sql.PreparedStatement"
%><%@ page import="com.cyberlink.cosmetic.modules.post.repository.FeedRepository"
%><%@ page import="java.sql.Connection"
%><%
try {
	FeedRepository u = BeanLocator.getBean("post.feedRepository");
    u.updateNew();
    out.print("success");
} catch (Throwable e) {
    out.print("fail");
}
%>