<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript" src="/backend/product/js/prototype.js"></script>
<h2 class=ico_mug>Product :: Price Range Management</h2>
<c:if test="${actionBean.currentUserAdmin == true}">
<div>
	<s:link beanclass="com.cyberlink.cosmetic.action.backend.product.PriceRangeManageAction" event="createNewPriceRangeRequest">
		*Create New Price Range
	</s:link>
</div>
<div>
	<s:form id="offsetLimit" beanclass="com.cyberlink.cosmetic.action.backend.product.PriceRangeManageAction">
		Locale
		<s:select name="locale" id="locale" onchange="changeLocale()">
			<s:options-collection collection="${actionBean.localeList}" />
		</s:select>
		&nbsp;
		<s:submit name="updatePriceRangeRequest" value="Update Price Range at this locale" />
		<br/>
		Go to page No.
		<s:select name="offset" id="offset">
			<c:forEach var="opts" begin="1" end="${actionBean.pages}">
				<s:option value="${(opts-1)*actionBean.limit}">${opts}</s:option>
			</c:forEach>
		</s:select>
		&nbsp;&nbsp;|&nbsp;&nbsp;Show&nbsp;
		<s:select name="limit" id="limit">
			<s:option value="10">10</s:option>
			<s:option value="20">20</s:option>
			<s:option value="50">50</s:option>
			<s:option value="100">100</s:option>
		</s:select>
		recs/page
		<s:submit name="submit" value="Go"/>
	</s:form>
</div>

<display:table id="priceRange" name="actionBean.priceRangeList.results" requestURI="" defaultsort ="1">
	<display:column title="ID" property="id" sortable="true" />
	<display:column title="Locale" property="locale" sortable="true" />
	<display:column title="Range Name" property="rangeName" sortable="true" />
	<display:column title="Range Minimun" property="priceMin" sortable="true" format="{0,number,0.00}" />
	<display:column title="Range Maximum" property="priceMax" sortable="true" format="{0,number,0.00}" />
</display:table>
</c:if>
<c:if test="${actionBean.currentUserAdmin == false}">
Need to login
</c:if>
<script type="text/javascript">
	function changeLocale() {
		document.getElementById('offset').value = 0 ;
		document.getElementById('limit').value = 20 ;
        document.querySelectorAll("input[value=Go]")[0].click();
    }
</script>