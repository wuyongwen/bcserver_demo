<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<style type="text/css">
/* Border styles */
.button{
	height:30px;
	font-size:12px;
}
#table thead, #table tr {
border-top-width: 1px;
border-top-style: solid;
border-top-color: rgb(230, 189, 189);
}
#table {
border-bottom-width: 1px;
border-bottom-style: solid;
border-bottom-color: rgb(230, 189, 189);
}

/* Padding and font style */
#table td, #table th {
padding: 5px 10px;
font-size: 16px;
font-family: Verdana;
color: #000;
}

/* Alternating background colors */
#table tr:nth-child(even) {
background: #ECF5FF;
line-height:25px;
}
#table tr:nth-child(odd) {
background: #C4E1FF;
line-height:25px;
}
.circle-correct{
	width:25px;height:25px; 
	border-radius:99em;
	background-color:green;
	font-size:20px;
	text-align:center;
	line-height:25px;
}
.circle-worng{
	width:25px;height:25px; 
	border-radius:99em;
	background-color:red;
	text-align:center;
	line-height:25px;
}
</style>
<script type="text/javascript">
$(document).ready(function(){
	
	var completeNum = 0;
	var verifyMxUrlListResult = "";
	var mxList;
	var email;
	$("#btnRoute").click(function() {
		completeNum = 0;
		verifyMxUrlListResult = "";
		mxList = null;
		email = "";
		$("#verifyMxUrlList").html("");
		$('#btnRoute').hide();
		$("#tdEmailFormat").text("Validating, please wait!");
		$("#tdMXList").text("Validating, please wait!");
		$("#verifyMxUrlDiv").hide();
		email = $("#email").val();
		var idx = email.indexOf("@");
		if(idx > 0){
			$("#tdEmailFormat").html("<b style='color:green'>OK!</b>");
			var domainUrl = email.substring(++idx,email.length);
			var requestUrl = "./verifyEmail.action?getMxList";
			var data = "domainUrl=" + domainUrl;
			jQuery.ajax({
				url : requestUrl,
				data : data,
	            dataType: "json",
				success : function(result) {
					if(result.mxList != null && result.mxList != ""){
						$("#tdMXList").html("<b style='color:green'>OK!</b>");
						mxList = result.mxList;
						$("#verifyMxUrlDiv").show();
						$('#btnRoute').show();
					}
					if(result.errorMessage != null){
						$("#tdMXList").html("<b style='color:red'>" + result.errorMessage + "</b>");
						$("#verifyMxUrlList").html("<b style='color:red'>Fail!</b>");
						$('#btnRoute').show();
					}
				},
				error : function (jqXHR, textStatus, errorThrown) {
					$("#tdMXList").html("<b style='color:red'>[errer] " +textStatus+ " (" + errorThrown + ")</b>");
					$('#btnRoute').show();
				}
			});
		}else{
			$("#tdEmailFormat").html("<b style='color:red'>Fail!</b>");
			$("#tdMXList").html("<b style='color:red'>Fail!</b>");
			$("#verifyMxUrlList").html("<b style='color:red'>Fail!</b>");
			$('#btnRoute').show();
		}
		return false;
	});
	
	function verifyMx(stepNum){
		completeNum--;
		var mxURL = mxList[completeNum];
		var requestUrlMX = "./verifyEmail.action?verifyMxUrl";
		var dataMX = "mxUrl=" + mxURL + "&email=" + email + "&stepNum=" + stepNum;
		jQuery.ajax({
			url : requestUrlMX,
			data : dataMX,
            dataType: "json",
			success : function(subResult) {
				if(subResult.errorMessage != null){
					verifyMxUrlListResult += ("<b style='color:red'>" + subResult.errorMessage + "</b></br>");
					if(completeNum == 0){
						$("#verifyMxUrlList").html(verifyMxUrlListResult);
						$('#btnRoute').show();
						$('#verifyMxUrlDiv').show();
						return;
					}
					$("#verifyMxUrlList").html(verifyMxUrlListResult + "Validating, please wait!");
					verifyMx(stepNum);
				}
				if(subResult.message != null){
					verifyMxUrlListResult += ("<b style='color:green'>" + subResult.message + "</b>");
					$("#verifyMxUrlList").html(verifyMxUrlListResult);
					$('#btnRoute').show();
					$('#verifyMxUrlDiv').show();
					return;
				}
			},
			error : function (jqXHR, textStatus, errorThrown) {
				verifyMxUrlListResult += ("<b style='color:red'>[" + mxURL + " errer] " + textStatus + " (" + errorThrown + ")</b></br>");
				if(completeNum == 0){
					$("#verifyMxUrlList").html(verifyMxUrlListResult);
					$('#btnRoute').show();
					$('#verifyMxUrlDiv').show();
					return;
				}
				$("#verifyMxUrlList").html(verifyMxUrlListResult + "Validating, please wait!");
				verifyMx(stepNum);
			}
		});
	}
	$("input[type='button'][act='verifyMx']").click(function(){ 
		verifyMxUrlListResult = "";
		completeNum = mxList.length;
		$('#btnRoute').hide();
		$('#verifyMxUrlDiv').hide();
		$("#verifyMxUrlList").html("Validating, please wait!");
		verifyMx($(this).attr("stepNum"));
	});
});
</script>
<h2 class=ico_mug>Miscellaneous :: Verify Email</h2>

<s:form beanclass="${actionBean.class}" method="get">
<div style="display:inline;"><s:text name="email" id="email" style="width:400px;" />&nbsp;<s:submit name="route" id="btnRoute" class="button" value="Verify Email"/></div>&nbsp;&nbsp;&nbsp;&nbsp;<div id="hint" style="display:inline;"></div>
</br>
<div>
<h2>Verify Items:</h2>
<table id="table">
<tr>
	<td style="width:200px;">Verify email format</td>
	<td id="tdEmailFormat">
	</td>
</tr>
<tr>
	<td>
		Get mx url list 
	</td>
	<td id="tdMXList">
	</td>
</tr>
<tr>
	<td>Verify mx url list</td>
	<td>
		<div id="verifyMxUrlDiv" style="display:none;">
			<input type="button" class="button" act="verifyMx" stepNum="1" value="Validate header"/><input type="button" class="button" act="verifyMx" stepNum="2" value="Validate ESMTP"/><input type="button" class="button" act="verifyMx" stepNum="3" value="Validate the sender address"/><input type="button" class="button" act="verifyMx" stepNum="4" value="Test RCPT"/>
		</div>
		<div id="verifyMxUrlList"></div>
	</td>
</tr>
</table>
</div>
</s:form>
