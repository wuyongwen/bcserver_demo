<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>

<h2 class=ico_mug>Miscellaneous :: Facebook Ad. Management</h2>
<div>
<display:table id="row" name="actionBean.fbAdList" requestURI="FacebookAdManage.action" pagesize="20" sort="page" partialList="true" size="${fn:length(actionBean.fbAdList)}" export="false" style="width:520px">
	<display:column title="Name" sortable="true" style="width:25%;">
		<c:out value="${row.attrName}" />
	</display:column>
	<display:column title="Value" sortable="true" style="width:50%;">
		<c:out value="${row.attrValue}" />
	</display:column>
	<display:column title="Action" sortable="false" style="width:25%;">
		<input class="modify" id="${row.id}" type="button" class="button" value="Modify"  />
	</display:column>
</display:table>
</div>

<table id="editTable" style="display:none">
<tr><th><label id="attrName"></label></th></tr>
<tr><td>adOffset:<input type="number" id="adOffset"/></td></tr>
<tr><td>adLimit:<input type="number" id="adLimit"/></td></tr>
<tr><td align="center"><input class="update" type="button" class="button" value="Update"  /></td></tr>
</table>

<script src="<c:url value="http://code.jquery.com/jquery-latest.min.js" />"></script>
<script>
$(".modify").on("click", function(){
	var data = "id=" + this.id;
	$.post("FacebookAdManage.action?modify", data, function(responseJson) {
		$("#attrName").text(responseJson.fbAd);
		$("#adOffset").val(responseJson.adOffset);
		$("#adLimit").val(responseJson.adLimit);
		$(".update").attr("id", responseJson.id);
		$("#editTable").show();
	}).fail(function(e) {
		alert("Failed to modify");
	});
});

$(".update").on("click", function(){
	var data = "id=" + this.id + "&offset=" + $("#adOffset").val() + "&limit=" + $("#adLimit").val();
	$.post("FacebookAdManage.action?update", data, function(responseJson) {
		window.location.href = "./FacebookAdManage.action";
	}).fail(function(e) {
		alert("Failed to update");
	});
});
</script>