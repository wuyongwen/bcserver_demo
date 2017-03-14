<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.10.2.min.js" />"></script>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/jquery-ui.min.js" />"></script>
<link href="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/themes/hot-sneaks/jquery-ui.css" />" rel="stylesheet">
<script src="<c:url value="/common/lib/timepicker/jquery-ui-timepicker-addon.js" />"></script>
<script src="<c:url value="/common/lib/timepicker/jquery-ui-sliderAccess.js" />"></script>
<link href="<c:url value="/common/lib/timepicker/jquery-ui-timepicker-addon.css" />" rel="stylesheet">

<script>

	$(function() {
		
		function calcTimeString(offset) {
			// create Date object for current location
			d = new Date();
			    
			// convert to msec
			// add local time zone offset 
			// get UTC time in msec
			utc = d.getTime() + (d.getTimezoneOffset() * 60000);
			    
			// create new Date object for different city
			// using supplied offset
			nd = new Date(utc + (3600000*offset));
			    
			// return time as a string
			return nd;
		}

		var myVar=setInterval(function () {myTimer()}, 1000);
		var counter = 0;
		function myTimer() {
			var date = new Date();
			document.getElementById("LosAngelesTime").innerHTML = "Los Angeles: " + calcTimeString("-7").toLocaleString();
			document.getElementById("BerlinTime").innerHTML = "Berlin: " + calcTimeString("+2").toLocaleString();
			document.getElementById("ParisTime").innerHTML = "Paris: " + calcTimeString("+2").toLocaleString();
			document.getElementById("BeijingTime").innerHTML = "Beijing: " + calcTimeString("+8").toLocaleString();
			document.getElementById("TapeiTime").innerHTML = "Tapei: " + calcTimeString("+8").toLocaleString();
			document.getElementById("TokyoTime").innerHTML = "Tokyo: " + calcTimeString("+9").toLocaleString();
			document.getElementById("SeoulTime").innerHTML = "Seoul: " + calcTimeString("+9").toLocaleString();
			document.getElementById("BrasiliaTime").innerHTML = "Brasilia: " + calcTimeString("-3").toLocaleString();
		}
		  
		var region = document.getElementById("regionSel");
		var zone = "+0000";
		if (region.innerHTML.toLowerCase() == "en_US".toLowerCase()) 
			zone = "-0800";
		else if (region.innerHTML.toLowerCase() == "de_DE".toLowerCase())
			zone = "+0200";
		else if (region.innerHTML.toLowerCase() == "fr_FR".toLowerCase())
			zone = "+0200";
		else if (region.innerHTML.toLowerCase() == "zh_TW".toLowerCase())
			zone = "+0800";
		else if (region.innerHTML.toLowerCase() == "zh_CN".toLowerCase())
			zone = "+0800";
		else if (region.innerHTML.toLowerCase() == "ja_JP".toLowerCase())
			zone = "+0900";
		else if (region.innerHTML.toLowerCase() == "ko_KR".toLowerCase())
			zone = "+0900";
		else if (region.innerHTML.toLowerCase() == "pt_BR".toLowerCase())
			zone = "-0300";
		
		Date.prototype.yyyymmdd = function() {
			var yyyy = this.getFullYear().toString();
			var mm = (this.getMonth()+1).toString(); // getMonth() is zero-based
			var dd  = this.getDate().toString();
			return yyyy + "-" + (mm[1]?mm:"0"+mm[0]) + "-" + (dd[1]?dd:"0"+dd[0]) + " "  + "00:00:00" + " " + zone; // padding
		};
		
		var d = new Date();
	  
		var opt={dateFormat: 'yy-mm-dd',
				showSecond: true,
				timeFormat: 'HH:mm:ss z',
	            timezone: zone,
	            showTimezone: false,
	            defaultValue: d.yyyymmdd()
	           };
		$('#datetimepicker').datetimepicker(opt);
		$('#datetimepicker').val(d.yyyymmdd());
		
		if ($('#accountNumber').text() ==  "0"){
			$("#post").hide();
			$('#accountNumber').css("color", "#FF0000");
		}
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
		
		$("form").submit(function() {
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
				$("#postingtext").css("color", "#0000FF");
				$("#postingtext").show();
				return true;
			}
		});
		
		$("#postingtext").hide();
	});
	
</script>
  
<h2 class=ico_mug>Post :: External Post</h2>
<s:form beanclass="${actionBean.class}" method="post">
<table>
	<tr>
		<td>
			<label>Locale : </label>
			<label id="regionSel" style="font-size:14px; border-style: solid; border-width: 1px; border-color: red;">${actionBean.locale}</label>
			<label>&nbsp;Circle : </label>
			<select id="circleSel" name="circleSel">
				<c:forEach items="${actionBean.circles}" var="circle" varStatus="loop">
					<option value="${circle.id}">${circle.circleName}</option>
				</c:forEach>
			</select>
			<label>&nbsp;Start Time: </label>
			<input type="text" id="datetimepicker" name="datetimepicker" readonly='true'>
			<label>&nbsp;</label>
			<span id="hint"></span>
		</td>
	</tr>
	<tr>
		<td>
			<label>Per user create ${actionBean.postNumberSel} post in Duration : </label>
			<select id="durationSel" name="durationSel">
				<option value="10" >10 min</option>
				<option value="30" >30 min</option>
				<option value="60" selected>1 hr</option>
				<option value="90" >1.5 hr</option>
				<option value="120" >2 hr</option>
				<option value="180" >3 hr</option>
				<option value="360" >6 hr</option>
				<option value="720" >12 hr</option>
				<option value="1440" >24 hr</option>
			</select>
		</td>
	</tr>
	<tr>
		<td>
			<label>Article Number : ${actionBean.selectSize}</label>
			<label>&nbsp;&nbsp;Account Number : </label>
			<label id="accountNumber">${actionBean.userNumber}</label>
			<label>&nbsp;&nbsp;</label>
			<input type="Submit" id="post" name="post" value="Post">
			<label id='postingtext'>Auto Posting...</label>
		</td>
	</tr>
	<tr>
		<td>
			<label>&nbsp;&nbsp;</label>
		</td>
	</tr>
	<tr>
		<table>
			<tr>
				<td>
					<span style="font-size:14px;">The local time in city: </span>
				</td>
			</tr>
			<tr>
				<td>
					<span id="BerlinTime"></span>
					<label>&nbsp;</label>
				</td>
				<td>
					<span id="LosAngelesTime"></span>
				</td>
			<tr>
			</tr>
			<tr>
				<td>
					<span id="ParisTime"></span>
					<label>&nbsp;</label>
				</td>
				<td>
					<span id="BeijingTime"></span>
				</td>
			</tr>
			<tr>
				<td>
					<span id="TapeiTime"></span>
					<label>&nbsp;</label>
				</td>
				<td>
					<span id="TokyoTime"></span>
				</td>
			</tr>
			<tr>
				<td>
					<span id="SeoulTime"></span>
					<label>&nbsp;</label>
				</td>
				<td>
					<span id="BrasiliaTime"></span>
				</td>
			</tr>
		</table>
	</tr>
</table>
</s:form>