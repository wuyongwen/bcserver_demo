<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page import="com.cyberlink.core.BeanLocator"%>
<%@ page import="java.sql.PreparedStatement"%>
<%@ page import="com.cyberlink.cosmetic.modules.product.service.SolrProductUpdater"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.dao.UserDao"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.model.User"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.model.Account"%>
<%@ page import="java.sql.Connection"%>
<h2 class=ico_mug>Detail User Info</h2>
<%
UserDao userDao = BeanLocator.getBean("user.UserDao");
Long creatorId = Long.valueOf( request.getParameter("creatorId") );
User curUser = userDao.findById(creatorId);
out.print("<table class=\"displaytag\" style=\"width:600px;\">");
out.print("<tr>");
out.print("<td width=\"130\"><img src=\"" + curUser.getAvatarUrl() + "\" width=\"120\" height=\"120\" ></td>");
out.print("<td>");
out.print("<table>");
out.print("<tr>");
out.print("<td>User&nbsp;ID:&nbsp;</td>");
out.print("<td>" + creatorId + "</td>");
out.print("</tr>");
out.print("<tr>");
out.print("<td>Name:&nbsp;</td>");
out.print("<td>" + curUser.getDisplayName() + "</td>");
out.print("</tr>");
out.print("<tr>");
out.print("<td>Email:&nbsp;</td>");
out.print("<td>");
Boolean noMail = Boolean.TRUE;
for( Account a : curUser.getAllEmailAccountList() ){
	if( a.getEmail() != null ){
		out.print("<div>" + a.getEmail() + "</div>");
		noMail = Boolean.FALSE;
	}
	else if(a.getAccount() != null){
		out.print("<div>" + a.getAccount() + "</div>");
		noMail = Boolean.FALSE;
	}
}
if( noMail ){
	out.print("<div>" + "no avaiable email address" + "</div>");
}
out.print("</td>");
out.print("</tr>");
out.print("</table>");
out.print("</td>");
out.print("</tr>");
out.print("</table>");
%>
<table class="displaytag" style="width:600px; ">
<tr>
<td>
<s:link beanclass="com.cyberlink.cosmetic.action.backend.product.ReportedProdCommentManageAction">
	<s:param name="userId" value="<%=creatorId%>" />
	<s:param name="showOnlyReportedComments" value="true" />
	All reported comments
</s:link>
&nbsp;|&nbsp;
<s:link beanclass="com.cyberlink.cosmetic.action.backend.product.ReportedProdCommentManageAction">
	<s:param name="userId" value="<%=creatorId%>" />
	All comments from this user
</s:link> 
</td>
</tr>
</table>