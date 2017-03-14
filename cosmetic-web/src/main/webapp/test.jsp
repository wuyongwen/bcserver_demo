<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=utf-8"%>
<%@ page language="java" import="com.cyberlink.core.BeanLocator"%>
<%@ page language="java" import="com.cyberlink.cosmetic.modules.user.repository.FollowRepository"%>
<%@ page language="java" import="com.cyberlink.cosmetic.modules.circle.repository.CircleFollowRepository"%>
<%@ page language="java" import="com.cyberlink.cosmetic.modules.circle.repository.CircleRepository"%>
<%@ page language="java" import="java.util.Date" %>
<%@ page language="java" import="java.util.*" %>
<%@ page language="java" import="com.cyberlink.cosmetic.event.*" %>

<html>
<head>
<title>test</title>
</head>
<body>
<%
//FollowRepository fr = BeanLocator.getBean("user.followRepository");
//fr.updateAll();
CircleFollowRepository cfr = BeanLocator.getBean("circle.circleFollowRepository");
cfr.updateAll();
//CircleRepository cr = BeanLocator.getBean("circle.circleRepository");
//cr.updateAll();

%>
</body>
</html>