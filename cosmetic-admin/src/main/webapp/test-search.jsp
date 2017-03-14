<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ page import="javax.sql.DataSource"
%><%@ page import="com.cyberlink.core.BeanLocator"
%><%@ page import="java.sql.PreparedStatement"
%><%@ page import="com.cyberlink.cosmetic.modules.product.dao.SolrProductDao"
%><%@ page import="com.cyberlink.cosmetic.modules.product.model.result.ProductWrapper"
%><%@ page import="com.cyberlink.cosmetic.modules.product.model.ProductSearchParam"
%><%@ page import="java.util.*"
%><%@ page import="java.sql.Connection"
%><%
try {
    final ProductSearchParam param = new ProductSearchParam();
    param.setKeyword("水");
    param.setLocale("zh_TW");
    SolrProductDao u = BeanLocator.getBean("product.solrProductDao");
    for (final ProductWrapper p : u.search(param).getResults()) {
        out.print(p.getProductName() + "<br/>");
    }
    out.print("success");
} catch (Throwable e) {
    out.print("fail");
}
%>