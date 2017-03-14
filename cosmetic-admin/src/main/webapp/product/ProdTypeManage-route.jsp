<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<h2 class=ico_mug>Product :: Product Category Management</h2>
<c:if test="${actionBean.currentUserAdmin == true or actionBean.accessControl.productManagerAccess == true}">
<div>
	<s:link beanclass="com.cyberlink.cosmetic.action.backend.product.ProdTypeManageAction" event="createNewProdTypeRequest">
		<s:param name="locale" value="${actionBean.locale}"/>
		*Create New Product Category
	</s:link>
</div>
<div><b>Current app rule: category with bigger priority will be displayed first</b></div>
<div>
	<s:form id="offsetLimit" beanclass="com.cyberlink.cosmetic.action.backend.product.ProdTypeManageAction">
		Total <b>${actionBean.typeList.totalSize}</b> items in result.
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
<display:table id="type" name="actionBean.typeList.results" requestURI="">
	<display:column title="Category ID" property="id" sortable="true" />
	<display:column title="Category Name" property="typeName" sortable="true" />
	<display:column title="Locale" property="locale" sortable="true" />
	<display:column title="Priority" property="sortPriority" sortable="true" />
	<display:column title="Action">
		<s:link beanclass="com.cyberlink.cosmetic.action.backend.product.ProdTypeManageAction" event="updateProdTypeRequest">
			<s:param name="typeId" value="${type.id}"/>
			Update
		</s:link>
		 | 
		<s:link beanclass="com.cyberlink.cosmetic.action.backend.product.ProdTypeManageAction" event="deleteProdType"
		onclick="return confirm('Delete ${type.typeName}? this type will be clean from all products');">
			<s:param name="typeId" value="${type.id}"/>
			Delete
		</s:link>
	</display:column>
</display:table>
</c:if>
<c:if test="${actionBean.accessControl.productManagerAccess == false and actionBean.currentUserAdmin == false }">
Sorry, you don't have authority to access this page. 
</c:if>
<script type="text/javascript">
	function changeLocale() {
		document.getElementById('offset').value = 0 ;
		document.getElementById('limit').value = 20 ;
        document.querySelectorAll("input[type=submit]")[0].click();
    }
</script>