<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ page import="com.mongodb.Mongo"
%><%@ page import="com.cyberlink.core.BeanLocator"
%><%@ page import="com.mongodb.DB"
%><%@ page import="org.springframework.data.mongodb.core.MongoFactoryBean"
%><%
try {
    final Mongo mongo = BeanLocator.getBean("core.mongo");
    final DB db = mongo.getDB("test");
    db.command("serverStatus");
    out.print("success");
} catch (Throwable e) {
    e.printStackTrace();
    out.print("fail");
}
%>