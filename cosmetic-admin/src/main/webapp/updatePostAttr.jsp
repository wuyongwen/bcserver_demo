<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ page import="javax.sql.DataSource"
%><%@ page import="com.cyberlink.core.BeanLocator"
%><%@ page import="java.sql.PreparedStatement"
%><%@ page import="com.cyberlink.cosmetic.modules.post.dao.PostAttributeDao"
%><%@ page import="java.sql.Connection"
%><%
try {
    PostAttributeDao u = BeanLocator.getBean("post.PostAttributeDao");
    u.updatePostLikeCount();
    u.updatePostCommentCount();
    u.updateCommentLikeCount();
    out.print("success");
} catch (Throwable e) {
    out.print("fail");
}
%>