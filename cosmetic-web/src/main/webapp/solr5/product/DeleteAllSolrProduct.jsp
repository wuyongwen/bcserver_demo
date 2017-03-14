<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.sql.DataSource"%>
<%@ page import="com.cyberlink.core.BeanLocator"%>
<%@ page import="java.sql.PreparedStatement"%>
<%@ page import="com.cyberlink.cosmetic.modules.product.service.SolrProductUpdater"%>
<%@ page import="java.sql.Connection"%>
<%
try {
	SolrProductUpdater u = BeanLocator.getBean("product.solrProductUpdater");
	u.deleteAll();
	out.print("success");
} catch (Throwable e) {
	out.print("fail");
}
%>