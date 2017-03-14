<%@ include file="/common/taglibs.jsp"%>
<%@ page import="com.cyberlink.core.BeanLocator"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="com.cyberlink.cosmetic.modules.product.model.RedirectLog" %>
<%@ page import="com.cyberlink.cosmetic.modules.product.dao.RedirectLogDao" %>

<%
	String Link = request.getParameter("link") ;
	Integer testGroup = Integer.valueOf(request.getParameter("testGroup")) ;
	if( Link != null && testGroup != null ){
		RedirectLogDao redirectDao = BeanLocator.getBean("product.RedirectLogDao") ;
		RedirectLog newlog = new RedirectLog();
		newlog.setLink(Link);
		newlog.setTestGroup(testGroup);
		redirectDao.create(newlog);	
		response.sendRedirect(Link);
	}
%>