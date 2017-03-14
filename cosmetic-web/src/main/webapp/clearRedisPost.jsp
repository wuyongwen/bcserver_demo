<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ page import="javax.sql.DataSource"
%><%@ page import="com.cyberlink.core.BeanLocator"
%><%@ page import="java.sql.PreparedStatement"
%><%@ page import="com.cyberlink.cosmetic.modules.post.repository.PostRepository"
%><%@ page import="java.sql.Connection"
%><%
try {
	PostRepository u = BeanLocator.getBean("post.postRepository");
    u.clearAll();
    out.print("success");
} catch (Throwable e) {
    out.print("fail");
}
%>