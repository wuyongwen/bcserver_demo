<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<link rel="stylesheet" href="//code.jquery.com/ui/1.11.2/themes/smoothness/jquery-ui.css">
<script src="//code.jquery.com/jquery-1.10.2.js"></script>
<script src="//code.jquery.com/ui/1.11.2/jquery-ui.js"></script>

<h2 class=ico_mug>Product :: Product Management</h2>
<c:if test="${actionBean.currentUserAdmin == true }">
<s:form name="searchParameter" id="searchParameter" beanclass="com.cyberlink.cosmetic.action.backend.product.ProdChangeLogManageAction">
<table>
<tr>
<td>
<div>
	User:&nbsp;
	<s:select name="userId" value="userId">
		<s:option value="" label="--ALL--"/>
		<s:options-collection collection="${actionBean.backendUserList}" value="id" label="displayName" />
	</s:select>
</div>
<div>
	Item:&nbsp;
	<s:select name="itemType" value="itemType">
		<s:option value="">--ALL--</s:option>
		<s:options-enumeration enum="com.cyberlink.cosmetic.modules.product.model.ProductChangeLogType" />
	</s:select>
</div>
<!--
<div>
	Date:
	<s:text name="date" id="datepicker" />
</div>
 -->
<div>
	Total <b>${actionBean.productChangeLogList.totalSize}</b> items in result.&nbsp;|&nbsp;
		Go to page No.
		<s:select name="offset" id="offset">
			<c:if test="${actionBean.pages == 0}">
				<s:option value="0">0</s:option>
			</c:if>
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
		<s:submit name="route" value="Go" id="changePageOffset"/>
</div>
</td>
</tr>
</table>

<display:table id="product" name="actionBean.wrappedChangeLogList.results" requestURI="" >
	<display:column title="Log ID" property="id" sortable="true" />
	<display:column title="Time" property="time" sortable="true" />
	<display:column title="User Id" property="userId" sortable="true" />
	<display:column title="User Name" property="userName" sortable="true" />
	<display:column title="Changed item" property="changedItemName" sortable="true" />
	<display:column title="Changed item ID" property="changeItemId" sortable="true" />
	<display:column title="Original Values">
		<c:forEach var="titleValue" items="${product.beforeValueList}">
			<div>${titleValue}</div>
		</c:forEach>
	</display:column>
	<display:column title="Changed Values">
		<c:forEach var="titleValue" items="${product.afterValueList}">
			<div>${titleValue}</div>
		</c:forEach>
	</display:column>
</display:table>
</s:form>
</c:if>
<c:if test="${actionBean.currentUserAdmin == false}">
Need to login
</c:if>
<script type="text/javascript">
	function cleanKeyword(){
		document.getElementById('searchKeyword').value = null ;
	}
</script>

<script type="text/javascript">
	function changeLocale() {
		document.getElementById('brandId').value = '' ;
		document.getElementById('typeId').value = '' ;
		document.getElementById('offset').value = 0 ;
		document.getElementById('limit').value = 20 ;
		document.getElementById('priceRangeId').value = '' ;
		document.getElementById('searchKeyword').value = '' ;
        document.querySelectorAll("input[id=changePageOffset]")[0].click();
    }
</script>
 <script>
$(function() {
	$( "#datepicker" ).datepicker({
		dateFormat: "yy/mm/dd"
	});
});
</script>