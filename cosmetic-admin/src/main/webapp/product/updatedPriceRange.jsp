<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<div>New Price Range for Locale ${actionBean.locale} is updated</div>

<display:table id="priceRange" name="actionBean.priceRangeList.results" requestURI="" defaultsort ="1">
	<display:column title="ID" property="id" sortable="true" />
	<display:column title="Locale" property="locale" sortable="true" />
	<display:column title="Range Name" property="rangeName" />
	<display:column title="Range Minimun" property="priceMin" sortable="true" />
	<display:column title="Range Maximum" property="priceMax" sortable="true" format="{0,number,0.00}" />
	<display:column title="Action">
	</display:column>
</display:table>