<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ page import="javax.sql.DataSource"
%><%@ page import="com.cyberlink.core.BeanLocator"
%><%@ page import="java.sql.PreparedStatement"
%><%@ page import="java.sql.Connection"
%><%@ page import="com.cyberlink.cosmetic.*"
%><%
if (!Constants.isInitialized()) {
    throw new RuntimeException("not initialized");
}

final DataSource ds = BeanLocator.getBean("core.dataSource");
final Connection con = ds.getConnection();
final PreparedStatement pstmt = con.prepareStatement("select 1");
pstmt.execute();
pstmt.close();
con.close();

out.print("success");
%>