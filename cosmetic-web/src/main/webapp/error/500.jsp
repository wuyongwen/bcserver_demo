<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="java.io.StringWriter"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import="java.util.Enumeration"%>
<%@ page import="org.slf4j.LoggerFactory"%>
<%@ page import="org.slf4j.Logger"%>
<%
    String mailContent = "Http Status Code: Internal Server Error (500)\r\n";
    mailContent += "Reqeust URI: " + pageContext.getErrorData().getRequestURI() + "\r\n";
    mailContent += "Servlet Name: " + pageContext.getErrorData().getServletName() + "\r\n";
    mailContent += "HeaderInfo:\r\n";
    Enumeration<String> headers = request.getHeaderNames();
    while (headers.hasMoreElements()) {
        String header = headers.nextElement();
        mailContent += header + " = " + request.getHeader(header) + "\r\n";
    }
    Enumeration<String> names = request.getParameterNames();
    mailContent += "=====\r\n";
    mailContent += "Parameters:\r\n";
    while (names.hasMoreElements()) {
        String n = names.nextElement();
        mailContent += n + " = " + request.getParameter(n) + "\r\n";
    }
    mailContent += "=====\r\n";
    StringWriter errWriter = new StringWriter();
    exception.printStackTrace(new PrintWriter(errWriter));
    mailContent += "Stack Trace: " + errWriter.toString() + "\r\n";
    final Logger logger = LoggerFactory.getLogger("http.500");
    logger.error(mailContent);
%>
<c:set var="isErrorPage" value="${true}" scope="request"/><jsp:include page="message.jsp"/>
