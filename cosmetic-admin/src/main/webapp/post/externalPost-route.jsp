<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
 <script type="text/javascript">
$(document).ready(function(){
	$("#regionSel").change(function() {
		var newUrl = "./externalPost.action?route&locale=" + $("#regionSel option:selected").val();
		window.location.href = newUrl
	});
	$("#uploadFile").change(function(e){
		var files = $(this).prop("files");
		if (files.length <= 0) {
			$('#hint').text("");
			$("#list").prop( "disabled", true);
		}
		else {
			var filename = files[0]['name'];
			$.get("./externalPost.action?checkFile&refInfo=" + encodeURIComponent(filename +"_"+ $("#regionSel").val()), function(result){
				if (result == "File name exist"){
					$('#hint').text(result);
					$('#hint').css("color", "#FF0000");
					$("#list").prop( "disabled", true);
				} else {
					$('#hint').text("");
					$("#list").prop( "disabled", false);
				}
				
			});
		}
		
	});
	
});
 </script>

<h2 class=ico_mug>Post :: External Post</h2>
<s:form beanclass="${actionBean.class}" method="post">
<div>
	<label>Locale : </label>
	<select id="regionSel" name="regionSel">
		<c:forEach items="${actionBean.availableRegion}" var="locale" varStatus="loop">
			<c:choose>
				<c:when test="${locale eq actionBean.locale}">
					<option value="${locale}" selected>${locale}</option>
				</c:when>
				<c:otherwise>
					<option value="${locale}">${locale}</option>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</select>
	<label>&nbsp;&nbsp;Account Number : </label>
	<label id="accountNumber">${actionBean.userNumber}</label>
</div>
<div>
	<label>Import JSON File: </label>
	<s:file id="uploadFile" name="jsonFile" accept=".json, application/json"/>
	<s:submit name="list" id="list" value="Upload and List Articles" disabled='disabled'/>
	<span id="hint"></span>
</div>
</s:form>