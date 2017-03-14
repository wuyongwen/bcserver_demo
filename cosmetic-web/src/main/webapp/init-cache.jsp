<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=utf-8"%>
<%@ page language="java" import="com.cyberlink.core.BeanLocator"%>
<%@ page language="java" import="com.cyberlink.cosmetic.XmppCommandExecutor"%>
<%@ page language="java" import="com.cyberlink.cosmetic.GroupRepository" %>
<%@ page language="java" import="com.cyberlink.cosmetic.SessionRepository" %>
<%@ page language="java" import="com.cyberlink.cosmetic.UserRepository" %>
<%@ page language="java" import="com.cyberlink.cosmetic.FriendRepository" %>
<%@ page language="java" import="java.util.*" %>

<html>
<head>
<title>test</title>
</head>
<body>
<%
final GroupRepository gr = BeanLocator.getBean("group.groupRepository");
gr.addAll();

final SessionRepository sr = BeanLocator.getBean("user.sessionRepository");
sr.addAll();

final FriendRepository fr = BeanLocator.getBean("friend.friendRepository");
fr.addAll();

final UserRepository ur= BeanLocator.getBean("user.userRepository");
ur.updateAll();

out.println("OK... " + new Date().getTime());
%>
</body>
</html>