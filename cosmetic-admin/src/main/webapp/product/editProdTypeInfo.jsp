<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<div>Edit Product Type Info</div>
<s:form id="table" name="table" beanclass="com.cyberlink.cosmetic.action.backend.product.ProdTypeManageAction" onsubmit="return checkPriority()">
	<s:errors/>
	<s:hidden name="typeId" value="${actionBean.typeId}" />
	<table>
		<tr>
			<td>Type ID</td>
			<td>${actionBean.typeItem.id}</td>
		</tr>
		<tr>
			<td>Type Name</td>
			<td><s:text name="typeName" value="${actionBean.typeItem.typeName}"/></td>
		</tr>
		<tr>
			<td>Locale</td>
			<td>
				<s:select name="locale" id="locale" value="${actionBean.locale}">
					<s:options-collection collection="${actionBean.localeList}" />
				</s:select>
			</td>
		</tr>
		<tr>
			<td>Priority</td>
			<td>
				<s:text name="sortPriority" value="${actionBean.typeItem.sortPriority}"/>
			</td>
		</tr>
	</table>
	<s:submit name="submitProdTypeUpdates" value="submit" />
	<s:submit name="cancel" value="cancel"/>
</s:form>
<script>
function checkPriority(){
	var priority = document.forms["table"]["sortPriority"].value;
	if( isNaN(priority) ){
		alert("priority must be a number");
		return false;
	}
	else{
		return true;
	}
}
</script>