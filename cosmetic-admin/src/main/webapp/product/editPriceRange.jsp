<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<div>Edit Price Range</div>
<div>Please enter both Max and Min for a valid range</div>
<s:errors/>
<s:form id="table" beanclass="com.cyberlink.cosmetic.action.backend.product.PriceRangeManageAction">
	<table>
		<tr>
			<td>Locale</td>
			<td><s:text name="locale" value="${actionBean.locale}" readonly="true" /></td>
		</tr>
	</table>
	<table>
		<c:set var="rownumber" value="0" />
		<c:forEach var="priceRange" items="${actionBean.priceRangeList.results}">
			<tr>
				<td>(${rownumber+1})</td>
				<td>Range Name</td>
				<td><s:text name="rangeName" value="${priceRange.rangeName}"/></td>
				<td>Min</td>
				<td><s:text name="priceMin" value="${priceRange.priceMin}"/></td>
				<td>Max</td>
				<td><s:text name="priceMax" value="${priceRange.priceMax}"/></td>
			</tr>
			<c:set var="rownumber" value="${rownumber+1}" />
		</c:forEach>
	<c:forEach var="i" begin="${actionBean.priceRangeList.totalSize}" end="19" >
		<tr>
			<td>(${i+1})</td>
			<td>Range Name</td>
			<td><s:text name="rangeName" /></td>
			<td>Min</td>
			<td><s:text name="priceMin" /></td>
			<td>Max</td>
			<td><s:text name="priceMax" /></td>
			</tr>
	</c:forEach>
	</table>
	<s:submit name="submitPriceRangeUpdates" value="submit"/>
	<s:submit name="cancel" value="cancel"/>
</s:form>