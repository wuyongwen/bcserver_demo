<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page import="javax.sql.DataSource"%>
<%@ page import="com.cyberlink.core.BeanLocator"%>
<%@ page import="java.sql.PreparedStatement"%>
<%@ page import="com.cyberlink.cosmetic.modules.post.dao.SolrPostDao"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="com.cyberlink.cosmetic.modules.common.model.SolrSearchParam"%>
<%@ page import="com.cyberlink.core.web.view.page.PageResult"%>
<%@ page import="com.cyberlink.cosmetic.modules.post.model.Post"%>
<%
try{
	String keyword = request.getParameter("keyword") ;
	String locale = request.getParameter("locale") ;
	String offset = request.getParameter("offset") ;
	String pageSize = request.getParameter("pageSize") ;
	if(keyword == null){
		keyword = "";
	}
	if(locale == null){
		locale = "en_US";//default value
	}
	if( offset == null ){
		offset = "0";
	}
	if( pageSize == null ){
		pageSize = "20";
	}
	SolrPostDao postDao = BeanLocator.getBean("post.solrPostDao");
	SolrSearchParam param = new SolrSearchParam (); 
	param.setKeyword(keyword);
	param.setLocale(locale);
	param.setOffset(Integer.valueOf(offset));
	param.setLimit(Integer.valueOf(pageSize));
	PageResult<Post> postSearchResult = postDao.search(param) ;
	
	out.print("<div>" + "found " + postSearchResult.getTotalSize() + " posts" + "</div>");
	int i = 1 ;
	for( Post p : postSearchResult.getResults() ){
		out.print("<div>" + i++ + ". [" + p.getTitle() + "] id: " + p.getId() + "</div>");
	}
} catch(Throwable e){
	out.print("fail");
}
	
%>