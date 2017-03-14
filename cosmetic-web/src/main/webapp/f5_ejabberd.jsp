<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=utf-8"%>
<%@ page language="java" import="com.cyberlink.core.BeanLocator"%>
<%@ page language="java" import="com.cyberlink.cosmetic.XmppCommandExecutor" %>

<html>
<head>
<title>test</title>
</head>
<body>
<%
XmppCommandExecutor d = BeanLocator.getBean("xmpp.xmppCommandExecutor");
if (d.isAlive()) {
    out.print("success");
} else {
    out.print("fail");
}
%>
</body>
</html>