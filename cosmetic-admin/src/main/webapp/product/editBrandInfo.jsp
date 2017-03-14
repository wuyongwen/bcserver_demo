<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script src="http://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.11.2.min.js"></script>
<div><b>Edit Brand Info</b></div>
<s:form id="table" name="table" beanclass="com.cyberlink.cosmetic.action.backend.product.BrandManageAction" onsubmit="return checkPriority()">
	<s:errors/>
	<s:hidden name="brandId" value="${actionBean.brandId}" />
	<table>
		<tr>
			<td>Brand ID</td>
			<td>${actionBean.brandItem.id}</td>
		</tr>
		<tr>
			<td>Brand Name</td>
			<td><s:text name="brandName" value="${actionBean.brandItem.brandName}"/></td>
		</tr>
		<tr>
			<td>Locale</td>
			<td>
				<s:select name="locale" id="locale" value="${actionBean.brandItem.locale}" onchange="updateSelectionBox( $(this).val() )">
					<s:options-collection collection="${actionBean.localeList}" />
				</s:select>				
			</td>
		</tr>	
		<tr>
			<td>Brand Index</td>
			<td>
				<s:select name="brandIndexId" id="brandIndexId" value="${actionBean.brandItem.brandIndex.id}">
					<s:options-collection collection="${actionBean.brandIndexList.results}" value="id" label="index"/>
				</s:select>
			</td>
		</tr>
		<tr>
			<td>Brand Priority</td>
			<td>
				<s:text name="priority" value="${actionBean.brandItem.priority}"/>
			</td>
		</tr>
	</table>
	<s:submit name="submitBrandUpdates" value="submit"/>
	<s:submit name="cancel" value="cancel"/>
</s:form>
<script>
function updateSelectionBox( locale ){
	var target = '#brandIndexId' ;
	$.getJSON( '/backend/product/BrandManage.action?updateSelectionBox',
	{"locale":locale,},
	function (data) {
		if (data) {
			var selectionBox = '<select name="brandIndexId" id="brandIndexId">' ;
			for(var i = 0; i<data.length; i++){
				selectionBox += '<option value="' + data[i].id + '">' + data[i].index + '</option>' ;
			}
			selectionBox += '</select>' ;
			$(target).replaceWith(selectionBox);
		}
	});
	
}
</script>

<script>
function checkPriority(){
	var priority = document.forms["table"]["priority"].value;
	if( isNaN(priority) ){
		alert("priority must be a number");
		return false;
	}
	else{
		return true;
	}
}
</script>