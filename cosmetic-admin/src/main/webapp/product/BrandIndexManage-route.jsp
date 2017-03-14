<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript" src="/backend/product/js/prototype.js"></script>
<h2 class=ico_mug>Product :: Brand Index Management</h2>
<c:if test="${actionBean.currentUserAdmin == true or actionBean.accessControl.productManagerAccess == true}">
<div>
	<s:link beanclass="com.cyberlink.cosmetic.action.backend.product.BrandIndexManageAction" event="createNewBrandIndexRequest">
		<s:param name="locale" value="${actionBean.locale}"/>
		*Create New Brand Index
	</s:link>
</div>
<div>
	<s:form id="offsetLimit" beanclass="com.cyberlink.cosmetic.action.backend.product.BrandIndexManageAction">
		Total <b>${actionBean.brandIndexList.totalSize}</b> Brand Index in result.
		<br/>
		Locale
		<s:select name="locale" id="locale" onchange="changeLocale()">
			<s:options-collection collection="${actionBean.localeList}" />
		</s:select>
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

<display:table id="brandIndex" name="actionBean.brandIndexList.results" requestURI="" defaultsort ="1">
	<display:column title="Index ID" property="id" sortable="true" />
	<display:column title="Index Character" property="index" sortable="true" />
	<display:column title="Index Locale" property="locale" sortable="true" />
	<display:column title="Action">
		<!--
		<s:link beanclass="com.cyberlink.cosmetic.action.backend.product.BrandIndexManageAction" event="updateBrandRequest">
			<s:param name="brandIndexId" value="${brandIndex.id}"/>
			Update
		</s:link>
		 | 
		<s:link beanclass="com.cyberlink.cosmetic.action.backend.product.BrandIndexManageAction" event="deleteBrand"
		onclick="return confirm('Delete ${brandIndex.index}?');">
			<s:param name="brandIndexId" value="${brandIndex.id}"/>
			Delete
		</s:link>
		-->
	</display:column>
</display:table>
</c:if>
<c:if test="${actionBean.currentUserAdmin == false and actionBean.accessControl.productManagerAccess == false}">
	Sorry, you have no authority to access this page
</c:if>
<script type="text/javascript">
	function changeLocale() {
		document.getElementById('offset').value = 0 ;
		document.getElementById('limit').value = 20 ;
        document.querySelectorAll("input[type=submit]")[0].click();
    }
</script>