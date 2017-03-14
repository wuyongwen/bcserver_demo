<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<div>Create New Product Type Info</div>
<s:form id="table" beanclass="com.cyberlink.cosmetic.action.backend.product.ProdTypeManageAction" onsubmit="return checkPriority()">
	<s:errors/>
	<table>
		<tr>
			<td>Product Type Name</td>
			<td><s:text name="typeName" /></td>
		</tr>
		<tr>
			<td>Locale</td>
			<td>
				<s:select name="locale" id="locale">
					<s:options-collection collection="${actionBean.localeList}" />
				</s:select>
			</td>
		</tr>
		<tr>
			<td>Priority</td>
			<td>
				<s:text name="sortPriority"/>
			</td>
		</tr>
	</table>
	<s:submit name="submitNewProdType" value="submit"/>
	<s:submit name="route" value="cancel"/>
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