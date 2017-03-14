<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<script src="http://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.11.2.min.js"></script>
<div>Create New Brand</div>
<s:form id="table" beanclass="com.cyberlink.cosmetic.action.backend.product.BrandManageAction" onsubmit="return checkPriority()">
<s:errors/>
	<table>
		<tr>
			<td>Brand Name</td>
			<td><s:text name="brandName" /></td>
		</tr>
		<tr>
			<td>Locale</td>
			<td>
				<s:select name="locale" id="locale" onchange="updateSelectionBox( $(this).val() )">
					<s:options-collection collection="${actionBean.localeList}" />
				</s:select>
			</td>
		</tr>
		<tr>
			<td>Brand Index</td>
			<td>
				<s:select name="brandIndexId" id="brandIndexId">
					<s:options-collection collection="${actionBean.brandIndexList.results}" value="id" label="index"/>
				</s:select>
			</td>
		</tr>
		<tr>
			<td>Brand Priority</td>
			<td>
				<s:text name="priority" />
			</td>
		</tr>
	</table>
	<s:submit name="submitNewBrand" value="submit"/>
	<s:submit name="route" value="cancel"/>
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