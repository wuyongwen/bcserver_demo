<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.10.2.min.js" />"></script>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/jquery-ui.min.js" />"></script>
<link href="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/themes/hot-sneaks/jquery-ui.css" />" rel="stylesheet">
<script src="<c:url value="/common/lib/timepicker/jquery-ui-timepicker-addon.js" />"></script>
<script src="<c:url value="/common/lib/timepicker/jquery-ui-sliderAccess.js" />"></script>
<link href="<c:url value="/common/lib/timepicker/jquery-ui-timepicker-addon.css" />" rel="stylesheet">

<script type="text/javascript">

	$(function() {
		
		var zone = "+0800";
		
		Date.prototype.yyyyMMddHHmmss = function() {
			var yyyy = this.getFullYear().toString();
			var MM = (this.getMonth()+1).toString(); // getMonth() is zero-based
			var dd  = this.getDate().toString();
			var HH  = this.getHours().toString();
			var mm  = this.getMinutes().toString();
			var ss  = this.getSeconds().toString();
			return yyyy + "-" + (MM[1]?MM:"0"+MM[0]) + "-" + (dd[1]?dd:"0"+dd[0]) + " "  + (HH[1]?HH:"0"+HH[0]) +":" + (mm[1]?mm:"0"+mm[0]) + ":" + (ss[1]?ss:"0"+ss[0]) + " " + zone; // padding
		};
		var serverTime = document.getElementById('serverTime').value;
		var d = new Date(Math.floor(Number(serverTime)+300000));
		var opt={dateFormat: 'yy-mm-dd',
				showSecond: true,
				timeFormat: 'HH:mm:ss z',
	            timezone: zone,
	            showTimezone: false,
	            defaultValue: d.yyyyMMddHHmmss()
	           };
		$('#datetimepicker').datetimepicker(opt);
		$('#datetimepicker').val(d.yyyyMMddHHmmss());
		
	});

	$(document).ready(function(){
		$("#datetimepicker").change(function() {
			current = new Date();
			selected = new Date($(this).val());
			if (current > selected) {
				$(this).css("background-color","#FF0000");
				$('#hint').text("Incorrect time!");
				$('#hint').css("color", "#FF0000");
				$("#post").hide();
			} else {
				$(this).css("background-color","#FFFFFF");
				$('#hint').text("ok!");
				$('#hint').css("color", "#0000FF");
				$("#post").show();
			}
		});
		
		$("#post").click(function() {
			current = new Date();
			selected = new Date($("#datetimepicker").val());
			if (current > selected) {
				$("#datetimepicker").css("background-color","#FF0000");
				$('#hint').text("Incorrect time!");
				$('#hint').css("color", "#FF0000");
				$("#post").hide();
				return false;
			} else {
				$("#datetimepicker").css("background-color","#FFFFFF");
				$('#hint').text("ok!");
				$('#hint').css("color", "#0000FF");
				$("#post").hide();
				return true;
			}
		});
		
	});
</script>

<h2 class=ico_mug>Miscellaneous :: Server Restart Management</h2>
<s:form beanclass="${actionBean.class}" method="post">
	<div>
		<label>&nbsp;Server restart Time: </label>
		<input type="text" id="datetimepicker" name="datetimepicker" readonly='true'>
		<span id="hint"></span>
		<input type="Submit" id="post" name="post" value="Set">
	</div>
	<div>
		<label>&nbsp;Currently setting time: </label>
		<c:if test="${actionBean.restartServerTimeSetting != null}">
			<fmt:formatDate value="${actionBean.restartServerTimeSetting}"  type="both" pattern="yyyy/MM/dd HH:mm:ss Z"/>
			<input type="Submit" id="clear" name="clear" value="clear">
		</c:if>
		<c:if test="${actionBean.restartServerTimeSetting == null}">
		 There is no set time!
		</c:if>
	</div>
	<s:hidden name="serverTime" id="serverTime" value="${actionBean.serverTime}"></s:hidden>
</s:form>