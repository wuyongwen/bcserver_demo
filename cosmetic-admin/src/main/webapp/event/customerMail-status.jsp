<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ page import="javax.sql.DataSource"
%><%@ page import="com.cyberlink.core.BeanLocator"
%><%@ page import="java.sql.PreparedStatement"
%><%@ page import="com.cyberlink.cosmetic.action.backend.service.FreeSampleMailService"
%><%@ page import="java.sql.Connection"
%><%
try {
	FreeSampleMailService freeSampleMailService = BeanLocator.getBean("backend.FreeSampleMailService");
    out.print(freeSampleMailService.getStatus());
} catch (Throwable e) {
    out.print(e.getMessage());
}
%>