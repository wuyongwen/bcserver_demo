<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.sql.DataSource"%>
<%@ page import="com.cyberlink.core.BeanLocator"%>
<%@ page import="java.sql.PreparedStatement"%>
<%@ page import="com.cyberlink.cosmetic.action.backend.service.AutoCheckAndImportNewProductsService"%>
<%@ page import="java.sql.Connection"%>
<%
try {
	AutoCheckAndImportNewProductsService autoCheckAndImportNewProductsService = BeanLocator.getBean("backend.AutoCheckAndImportNewProductsService");
	out.println("Control Paremeters :<BR /> isRun = {true,false,} , null will show status ,Default value = false;<BR /> isOnShelf = {true,false} ,Default value = offShelf");
	out.println("<BR /><BR />");
	if(request.getParameter("isRun") != null){
		if(request.getParameter("isRun").equals("true")){
			autoCheckAndImportNewProductsService.start();
			out.println("AutoCheckAndImportNewProductsService start success");
		} else if (request.getParameter("isRun").equals("false")){
			autoCheckAndImportNewProductsService.stop();
			out.println("AutoCheckAndImportNewProductsService stop success");
		}	
	}else {
		out.println(autoCheckAndImportNewProductsService.getStatus());
	}
	out.println("<BR />");
	if(request.getParameter("isOnShelf") != null){
		if(request.getParameter("isOnShelf").equals("true")){
			autoCheckAndImportNewProductsService.onShelf();
			out.println("Set product info onshelf success");
		} else if (request.getParameter("isOnShelf").equals("false")){
			autoCheckAndImportNewProductsService.offShelf();
			out.println("Set product info offshelf success");
		}
	}
	
	if(request.getParameter("execNow") != null){
		if(request.getParameter("execNow").equals("true")){
			autoCheckAndImportNewProductsService.exec();
		}
	}
	
	if(autoCheckAndImportNewProductsService.getStatus().equals("AutoCheckAndImportNewProductsService isn't running")){
		out.println("<input type=\"button\" value=\"Set isRun true\" onclick=\"window.location.assign('./ImportNewProduct.jsp?isRun=true');\" >");
	}
	if(autoCheckAndImportNewProductsService.getStatus().equals("AutoCheckAndImportNewProductsService is running")){
		out.println("<input type=\"button\" value=\"Set isRun false\" onclick=\"window.location.assign('./ImportNewProduct.jsp?isRun=false');\" >");
	}
	if(autoCheckAndImportNewProductsService.getStatus().equals("AutoCheckAndImportNewProductsService is running")){
		out.println("<input type=\"button\" value=\"Execute now\" onclick=\"window.location.assign('./ImportNewProduct.jsp?execNow=true');\" >");
	}
	out.println("<BR />");
	out.println("<BR />");
	if(autoCheckAndImportNewProductsService.getOnShelfStatus().equals("true")){
		out.println("<input type=\"button\" value=\"Set isOnShelf false\" onclick=\"window.location.assign('./ImportNewProduct.jsp?isOnShelf=false');\" >");
	} else if (autoCheckAndImportNewProductsService.getOnShelfStatus().equals("false")){
		out.println("<input type=\"button\" value=\"Set isOnShelf true\" onclick=\"window.location.assign('./ImportNewProduct.jsp?isOnShelf=true');\" >");
	}
} catch (Throwable e) {}
%>