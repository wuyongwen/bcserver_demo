<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<style type="text/css">
.deleteBtn {
  background: #ff7566;
  color: #fff;
  font-family: 'Helvetica', 'Arial', sans-serif;
  font-size: 2em;
  font-weight: bold;
  text-align: center;
  width: 40px;
  height: 40px;
  border-radius: 5px;
}
.deleteBtn:hover {
  background: #b21200;
  color: #fff;
  font-family: 'Helvetica', 'Arial', sans-serif;
  font-size: 2em;
  font-weight: bold;
  text-align: center;
  width: 40px;
  height: 40px;
  border-radius: 5px;
}

.deleteBtn_store {
  background: #ff7566;
  color: #fff;
  font-family: 'Helvetica', 'Arial', sans-serif;
  font-size: 1em;
  font-weight: bold;
  text-align: center;
  width: 30px;
  height: 30px;
  border-radius: 5px;
}
.deleteBtn_store:hover {
  background: #b21200;
  color: #fff;
  font-family: 'Helvetica', 'Arial', sans-serif;
  font-size: 1em;
  font-weight: bold;
  text-align: center;
  width: 30px;
  height: 30px;
  border-radius: 5px;
}
.city{
	background-color: #ECF5FF;
}
</style>
<h2 class=ico_mug>Event :: Event Management</h2>
<div id="test"/>
<s:form beanclass="${actionBean.class}">
<input type="hidden" name="brandEventId" value="${actionBean.brandEventDetail.event.id}">
<div align="center" style="with:100%;">
	<div style="max-width:720px; border-radius: 10px;background-color: #FFFFFF;box-shadow: 0px 0px 15px 1px; padding: 17px 35px 12px 35px;">
		</br>
		</br>
		<div>
			<img src="${actionBean.brandEventDetail.event.imageUrl}" align="center" style="min-width:29px; min-height:21px; max-width:720px; width:auto; height:auto;"/>
		</div>
		</br>
		<textarea name="title" rows="2" style="font-size:18px;line-height:20px;width:100%;">${actionBean.brandEventDetail.event.title}</textarea>
		</br>
		<textarea name="description" rows="7" style="font-size:18px;line-height:20px;width:100%;">${actionBean.brandEventDetail.event.description}</textarea>
		</br>
		<div style="font-size:22px; line-height:22px; border-color:#a969b6; border-style:solid; border-width:2px; " >
		<c:choose>
                <c:when test="${actionBean.brandEventDetail.status eq 'Upcoming'}">
                    <div id="count_down_pre">距離活動開始倒數<span style="color: #a969b6;">${actionBean.brandEventDetail.remainDays}</span>天</div>
                </c:when>
                <c:when test="${actionBean.brandEventDetail.status eq 'Ongoing'}">
                    <div id="count_down">只剩${actionBean.brandEventDetail.remainDays}天${actionBean.brandEventDetail.remainHours}小時${actionBean.brandEventDetail.remainMinutes}分鐘<br /><span class="join">已參與${actionBean.brandEventDetail.event.joinNum}人</span></div>
                </c:when>
                <c:when test="${actionBean.brandEventDetail.status eq 'Deleted' or actionBean.brandEventDetail.status eq 'Drawing' or actionBean.brandEventDetail.status eq 'Expired'}">
                    <div id="count_down_expired">活動已結束</div>
                </c:when>
        </c:choose>
        </div>
        <div align="left">
        	<h1>Id</h1>
        	<p style="font-size:18px; line-height:20px;">${actionBean.brandEventDetail.event.id}</p>
        	<h1>Brand UserId</h1>
        	<p style="font-size:18px; line-height:20px;">${actionBean.brandEventDetail.event.brandId}</p>
        	<h1>活動期間</h1>
			<input type="text" style="font-size:18px;line-height:20px;width:230px;" id="datetimepicker_startTime" name="startTime" act='datetimepicker' itemName="startTime" readonly='true' value="${actionBean.brandEventDetail.startTime}"/> ~ <input type="text" style="font-size:18px;line-height:20px;width:230px;" id="datetimepicker_endTime" name="endTime" act='datetimepicker' itemName="endTime" readonly='true' value="${actionBean.brandEventDetail.endTime}"/><br/>
			<span id="active_start_hint"></span><br/><span id="active_end_hint"></span>
            <h1>客戶寄信時間</h1>
            <input type="text" style="font-size:18px;line-height:20px;width:230px;" id="datetimepicker_companySendDate" name="companySendDate" act='datetimepicker' itemName="companySendDate" readonly='true' value="${actionBean.brandEventDetail.companySendDate}"/><span id="send_hint"></span>
            <h1>得獎名單公布時間</h1>
            <input type="text" style="font-size:18px;line-height:20px;width:230px;" id="datetimepicker_drawTime" name="drawTime" act='datetimepicker' itemName="drawTime" readonly='true' value="${actionBean.brandEventDetail.drawTime}"/><span id="draw_hint"></span>
            <h1>領獎期間</h1>
            <input type="text" style="font-size:18px;line-height:20px;width:230px;" id="datetimepicker_receiveBeginDate" name="receiveBeginDate" act='datetimepicker' itemName="receiveBeginDate" readonly='true' value="${actionBean.brandEventDetail.receiveBeginDate}"/> ~ <input type="text" style="font-size:18px;line-height:20px;width:230px;" id="datetimepicker_receiveEndDate" name="receiveEndDate" act='datetimepicker' itemName="receiveEndDate" readonly='true' value="${actionBean.brandEventDetail.receiveEndDate}"/>
            <input type="button" id="clearRewardTime" value="clear reward time"/><br/>
            <span id="receive_start_hint"></span><br/><span id="receive_end_hint"></span>
            <h1>好康贈品</h1>
            <textarea name="prodName" rows="2" style="font-size:18px;line-height:20px;width:100%;">${actionBean.brandEventDetail.event.prodName}</textarea>
            <h1>產品特色</h1>
            <textarea name="prodDescription" rows="5" id="productDescription" style="font-size:18px;line-height:20px;width:100%;">${actionBean.brandEventDetail.event.prodDescription}</textarea>
            <h1>好康內容</h1>
            <textarea name="prodDetail" rows="5" style="font-size:18px;line-height:20px;width:100%;">${actionBean.brandEventDetail.event.prodDetail}</textarea>
            <h1>贈品數量</h1>
            <p style="font-size:18px; line-height:20px;">${actionBean.brandEventDetail.event.quantity}</p>
            <h1>參與人數</h1>
            <p style="font-size:18px; line-height:20px;">${actionBean.brandEventDetail.event.joinNum}</p>
            <h1>申請資格&nbsp;(${actionBean.brandEventDetail.event.applyType})</h1>
            <textarea name="applyDesc" rows="5" style="font-size:18px;line-height:20px;width:100%;">${actionBean.brandEventDetail.event.eventAttrJNode.applyDesc}</textarea>
            <h1>活動方式&nbsp;(${actionBean.brandEventDetail.event.eventType})</h1>
            <textarea name="eventTypeDesc" rows="5" style="font-size:18px;line-height:20px;width:100%;">${actionBean.brandEventDetail.event.eventAttrJNode.eventTypeDesc}</textarea>
            <h1>領取方式&nbsp;(${actionBean.brandEventDetail.event.receiveType})</h1>
            <textarea name="receiveDesc" rows="5" style="font-size:18px;line-height:20px;width:100%;">${actionBean.brandEventDetail.event.eventAttrJNode.receiveDesc}</textarea>
            <c:if test="${actionBean.brandEventDetail.event.receiveType eq 'Store'}">
            	<h2>Store List:</h2>
            	<c:if test="${actionBean.brandEventDetail.hasWinnerList}">
		            <display:table id="allStores" name="actionBean.brandEventDetail.event.stores" requestURI="" defaultsort ="1">
						<display:column title="CITY" property="city" />
						<display:column title="STORES">
							<c:forEach items="${allStores.stores}" var="storeDetail">
								<c:out value="${storeDetail.name}"/>&nbsp;&nbsp;<c:out value="${storeDetail.address}"/><br/>
							</c:forEach>
						</display:column>
					</display:table>
				</c:if>
				<c:if test="${!actionBean.brandEventDetail.hasWinnerList}">
				<div id="allStore">
				<c:forEach var="allStores" items="${actionBean.brandEventDetail.event.stores}"  varStatus="loop">
					<div id="storeCity_${loop.index}" class="city">
						<div style="background-color:#BDE7FF">
							<input type="text" name="storeCity" cityIdx="${loop.index}" style="font-size:14px;line-height:20px;width:200px;" value="${allStores.city}"/>&nbsp;<input type="button" act="delStoreCity" cityIdx="${loop.index}" class="deleteBtn_store" value="&#10006;">			
						</div>
						<div id="storeDetail_${loop.index}">
							<c:forEach items="${allStores.stores}" var="storeDetail" varStatus="subLoop">
							<div id="store_${loop.index}_${subLoop.index}">
								<input type="text" name="storeName" cityIdx="${loop.index}" idx="${subLoop.index}" style="font-size:14px;line-height:20px;width:200px;" value="${storeDetail.name}"/>&nbsp;<input type="text" name="storeAdress" cityIdx="${loop.index}" idx="${subLoop.index}" style="font-size:14px;line-height:20px;width:430px;" value="${storeDetail.address}"/>&nbsp;<input type="button" act="delStore" cityIdx="${loop.index}" idx="${subLoop.index}" class="deleteBtn_store" value="&#10006;">
							</div>
							</c:forEach>
						</div>
						<input type="button" cityIdx="${loop.index}" value="Add More Store Field" act="addStoreField"/>
					</div>
				</c:forEach>
				</div>
				<input type="button" cityIdx="${loop.index}" value="Add More City Field" act="addCityField" />
				</c:if>
			</c:if>
            <h1>附註</h1>
            <textarea name="comment" rows="5" style="font-size:18px;line-height:20px;width:100%;">${actionBean.brandEventDetail.event.comment}</textarea>
            <h1>客戶聯繫</h1>
            <div id="companyEmailGroup">
                <c:if test="${fn:length(actionBean.brandEventDetail.companyEmail) gt 0}">
		            <c:forEach items="${actionBean.brandEventDetail.companyEmail}" var="companyEmail" varStatus="loop">
		            	<p name="companyEmail" idx="${loop.index}"><input type="text" format="email" name="companyEmailList" placeholder="please enter email(e.g. xxxx@gmail.com)" style="font-size:16px;line-height:30px;width:400px;" value="${companyEmail}"/>&nbsp;<input type="button" act="delCompanyEmail" idx="${loop.index}" class="deleteBtn" value="&#10006;"></p>
		            </c:forEach>
	            </c:if>
	            <c:if test="${fn:length(actionBean.brandEventDetail.companyEmail) eq 0}">
	            	<p name="companyEmail" idx="0"><input type="text" format="email" name="companyEmailList" placeholder="please enter email(e.g. xxxx@gmail.com)" style="font-size:16px;line-height:30px;width:400px;" value="${companyEmail}"/>&nbsp;<input type="button" act="delCompanyEmail" idx="0" class="deleteBtn" value="&#10006;"></p>
            	</c:if>
            </div>
            <p><input type="button" value="Add More Email Field" act="addCompanyEmailField" /></p>
            <h1>PF mail</h1>
            <div id="pfEmailGroup">
	            <c:if test="${fn:length(actionBean.brandEventDetail.pfEmail) gt 0}">
		            <c:forEach items="${actionBean.brandEventDetail.pfEmail}" var="pfEmail" varStatus="loop">
		            	<p name="pfEmail" idx="${loop.index}"><input type="text" format="email" name="pfEmailList" placeholder="please enter email(e.g. xxxx@gmail.com)" style="font-size:16px;line-height:30px;width:400px;" value="${pfEmail}"/>&nbsp;<input type="button" class="deleteBtn" act="delPfEmail" idx="${loop.index}" value="&#10006;"></p>
		            </c:forEach>
	            </c:if>
	            <c:if test="${fn:length(actionBean.brandEventDetail.pfEmail) eq 0}">
		            <p name="pfEmail" idx="0"><input type="text" format="email" name="pfEmailList" placeholder="please enter email(e.g. xxxx@gmail.com)" style="font-size:16px;line-height:30px;width:400px;" value="${pfEmail}"/>&nbsp;<input type="button" class="deleteBtn" act="delPfEmail" idx="0" value="&#10006;"></p>
	            </c:if>
            </div>
            <p><input type="button" value="Add More Email Field" act="addPfEmailField" /></p>
            <h1>客戶是否要求密件副本</h1>
            <c:if test="${actionBean.brandEventDetail.isBcc eq true}">
            	<p style="font-size:18px; line-height:20px;">是</p>
            </c:if>
            <c:if test="${actionBean.brandEventDetail.isBcc eq false}">
            	<p style="font-size:18px; line-height:20px;">否</p>
            </c:if>
            <h1>客戶信件已寄送</h1>
            <c:if test="${actionBean.brandEventDetail.isSent eq true}">
            	<p style="font-size:18px; line-height:20px;">是</p>
            </c:if>
            <c:if test="${actionBean.brandEventDetail.isSent eq false}">
            	<p style="font-size:18px; line-height:20px;">否</p>
            </c:if>
            <h1>Priority</h1>
            <p><input type="text" name="priority" style="font-size:18px;line-height:30px;width:300px;" value="${actionBean.brandEventDetail.event.priority}"/></p>
			<h1>Post Id</h1>
			<p><input type="number" name="postId" style="font-size:18px;line-height:30px;width:300px;" value="${actionBean.postId}"/></p>
		</div>
		<s:hidden id="storesInfo" name="storesInfo" />
		<s:submit id="saveFileEdit" name="detailUpdate" value="Update"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input id="return" type="button" value="Cancel" onclick="location.href='EventManager.action?detail&brandEventId=${actionBean.brandEventDetail.event.id}'" />
	</div>
</div>
</s:form>
<script src="<c:url value="/common/lib/timepicker/jquery-ui-timepicker-addon.js" />"></script>
<script src="<c:url value="/common/lib/timepicker/jquery-ui-sliderAccess.js" />"></script>
<link href="<c:url value="/common/lib/timepicker/jquery-ui-timepicker-addon.css" />" rel="stylesheet">
<script type="text/javascript">
$(document).ready(function(){
	// Setting time related region -- start --
	var startTime = $("#datetimepicker_startTime").val();
	var endTime = $("#datetimepicker_endTime").val();
	var companySendTime = $("#datetimepicker_companySendDate").val();
	var drawTime = $("#datetimepicker_drawTime").val();
	var receiveBeginTime= $("#datetimepicker_receiveBeginDate").val();
	var receiveEndTime = $("#datetimepicker_receiveEndDate").val();
	
	var dStartTime = null;
	var dEndTime = null;
	var dCompanySendTime = null;
	var dDrawTime = null;
	var dReceiveBeginTime= null;
	var dReceiveEndTime = null;
	if(startTime != ""){
		dStartTime = new Date(Date.parse(startTime.substring(0,startTime.indexOf("CST"))));
	}
	if(endTime != ""){
		dEndTime = new Date(Date.parse(endTime.substring(0,endTime.indexOf("CST"))));
	} 
	if(companySendTime != ""){
		dCompanySendTime = new Date(Date.parse(companySendTime.substring(0,companySendTime.indexOf("CST"))));
	} 
	if(drawTime != ""){
		dDrawTime = new Date(Date.parse(drawTime.substring(0,drawTime.indexOf("CST"))));
	} 
	if(receiveBeginTime != ""){
		dReceiveBeginTime = new Date(Date.parse(receiveBeginTime.substring(0,receiveBeginTime.indexOf("CST"))));
	}
	if(receiveEndTime != ""){
		dReceiveEndTime = new Date(Date.parse(receiveEndTime.substring(0,receiveEndTime.indexOf("CST"))));
	} 
	
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
	function getOpt(d){
		var opt = {dateFormat: 'yy-mm-dd',
					showSecond: true,
					timeFormat: 'HH:mm:ss z',
		            timezone: zone,
		            showTimezone: false,
		            defaultValue: d.yyyyMMddHHmmss()};
		return opt;
	} 
	
	$("input[act='datetimepicker']").each(function(){
		var itemName = $(this).attr("itemName");
		var tempTime;
		var nowTime = new Date();
		tempTime = getInputTime(itemName, true);
		if(tempTime != null){
			$(this).val(tempTime.yyyyMMddHHmmss());
			if(tempTime > nowTime){
				$(this).datetimepicker(getOpt(tempTime));
			} else {
				$(this).css("border","0px");
			}
		}else if(tempTime == null){
			tempTime = new Date();
			$(this).datetimepicker(getOpt(tempTime));
		}
	}); 
	
	$("input[act='datetimepicker']").change(function() {
		var itemName = $(this).attr("itemName");
		current = new Date();
		selected = new Date($(this).val());
		if (current > selected) {
			$(this).css("background-color","#ffd1cc");
			switch(itemName){
				case "startTime":
					$('#active_start_hint').text("Event start time has to be greater than the time now! ");
					$('#active_start_hint').css("color", "#FF0000");
					break;
				case "endTime":
					$('#active_end_hint').text("Event end time has to be greater than the time now! ");
					$('#active_end_hint').css("color", "#FF0000");
					break;
				case "companySendDate":
					$('#send_hint').text("Send letter time has to be greater than the time now! ");
					$('#send_hint').css("color", "#FF0000");
					break;
				case "drawTime":
					$('#draw_hint').text("Draw time has to be greater than the time now! ");
					$('#draw_hint').css("color", "#FF0000");
					break;
				case "receiveBeginDate":
					$('#receive_start_hint').text("Receive reward start time has to be greater than the time now! ");
					$('#receive_start_hint').css("color", "#FF0000");
					break;
				case "receiveEndDate":
					$('#receive_end_hint').text("Receive reward end time has to be greater than the time now! ");
					$('#receive_end_hint').css("color", "#FF0000");
					break;
			}
		} else {
			switch(itemName){
				case "startTime":
					var tempStartTime = getInputTime("startTime",false);
					var tempEndTime = getInputTime("endTime",false);
					if((tempStartTime != null && tempEndTime != null) && (tempStartTime >= tempEndTime)){
						$('#active_start_hint').text("Event start time has to be less than the event end time! ");
						$('#active_start_hint').css("color", "#FF0000");
						$(this).css("background-color","#ffd1cc");
						return;
					}
					break;
				case "endTime":
					var tempStartTime = getInputTime("startTime",false);
					var tempEndTime = getInputTime("endTime",false);
					var tempCompanySendDate = getInputTime("companySendDate",false);
					if((tempStartTime != null && tempEndTime != null) && (tempStartTime >= tempEndTime)){
						$('#active_end_hint').text("Event end time has to be greater than the event start time! ");
						$('#active_end_hint').css("color", "#FF0000");
						$(this).css("background-color","#ffd1cc");
						return;
					} else if((tempEndTime != null && tempCompanySendDate != null) && (tempEndTime >= tempCompanySendDate)){
						$('#active_end_hint').text("Event end time has to be less than the send letter time! ");
						$('#active_end_hint').css("color", "#FF0000");
						$(this).css("background-color","#ffd1cc");
						return;
					}
					break;
				case "companySendDate":
					var tempEndTime = getInputTime("endTime",false);
					var tempCompanySendDate = getInputTime("companySendDate",false);
					var tempDrawTime = getInputTime("drawTime",false);
					if(((tempEndTime != null && tempCompanySendDate != null) && (tempEndTime >= tempCompanySendDate))){
						$('#send_hint').text("Send letter time has to be less than the event end time! ");
						$('#send_hint').css("color", "#FF0000");
						$(this).css("background-color","#ffd1cc");
						return;
					}else if((tempCompanySendDate != null && tempDrawTime != null) && (tempCompanySendDate >= tempDrawTime)){
						$('#send_hint').text("Send letter time has to be less than the draw time! ");
						$('#send_hint').css("color", "#FF0000");
						$(this).css("background-color","#ffd1cc");
						return;
					}
					break;
				case "drawTime":
					var tempCompanySendDate = getInputTime("companySendDate",false);
					var tempDrawTime = getInputTime("drawTime",false);
					var tempReceiveBeginDate = getInputTime("receiveBeginDate",false);
					if(((tempCompanySendDate != null && tempDrawTime != null) && (tempCompanySendDate >= tempDrawTime))){
						$('#draw_hint').text("Draw time has to be greater than the send letter time! ");
						$('#draw_hint').css("color", "#FF0000");
						$(this).css("background-color","#ffd1cc");
						return;
					}else if((tempDrawTime != null && tempReceiveBeginDate != null) && (tempDrawTime >= tempReceiveBeginDate)){
						$('#draw_hint').text("Draw time has to be less than the receive reward start time! ");
						$('#draw_hint').css("color", "#FF0000");
						$(this).css("background-color","#ffd1cc");
						return;
					}
					break;
				case "receiveBeginDate":
					var tempDrawTime = getInputTime("drawTime",false);
					var tempReceiveBeginDate = getInputTime("receiveBeginDate",false);
					var tempReceiveEndDate = getInputTime("receiveEndDate",false);
					if(((tempDrawTime != null && tempReceiveBeginDate != null) && (tempDrawTime >= tempReceiveBeginDate))){
						$('#receive_start_hint').text("Receive reward start time has to be greater than the draw time! ");
						$('#receive_start_hint').css("color", "#FF0000");
						$(this).css("background-color","#ffd1cc");
						return;
					}else if ((tempReceiveBeginDate != null && tempReceiveEndDate != null) && (tempReceiveBeginDate >= tempReceiveEndDate)){
						$('#receive_start_hint').text("Receive reward start time has to be less than the receive reward end time! ");
						$('#receive_start_hint').css("color", "#FF0000");
						$(this).css("background-color","#ffd1cc");
						return;
					}
					break;
				case "receiveEndDate":
					var tempReceiveBeginDate = getInputTime("receiveBeginDate",false);
					var tempReceiveEndDate = getInputTime("receiveEndDate",false);
					if(((tempReceiveBeginDate != null && tempReceiveEndDate != null) && (tempReceiveBeginDate >= tempReceiveEndDate))){
						$('#receive_end_hint').text("Receive reward end time has to be greater than the receive reward start time! ");
						$('#receive_end_hint').css("color", "#FF0000");
						$(this).css("background-color","#ffd1cc");
						return;
					}
					break;
			}
			initHints(itemName);
			$(this).css("background-color","#FFFFFF");
		}
	});
	
	function initHints(itemName){
		switch(itemName){
		case "startTime":
			$('#active_start_hint').text("");
			break;
		case "endTime":
			$('#active_end_hint').text("");
			break;
		case "companySendDate":
			$('#send_hint').text("");
			break;
		case "drawTime":
			$('#draw_hint').text("");
			break;
		case "receiveBeginDate":
			$('#receive_start_hint').text("");
			break;
		case "receiveEndDate":
			$('#receive_end_hint').text("");
			break;
		}
	} 
	
	function getInputTime(itemName, isOrigin){
		var tempTime;
		var originalTime;
		switch(itemName){
			case "startTime":
				originalTime = dStartTime;
				tempTime = $("[itemName='startTime']").val();
				break;
			case "endTime":
				originalTime = dEndTime;
				tempTime = $("[itemName='endTime']").val();
				break;
			case "companySendDate":
				originalTime = dCompanySendTime;
				tempTime = $("[itemName='companySendDate']").val();
				break;
			case "drawTime":
				originalTime = dDrawTime;
				tempTime = $("[itemName='drawTime']").val();
				break;
			case "receiveBeginDate":
				originalTime = dReceiveBeginTime;
				tempTime = $("[itemName='receiveBeginDate']").val();
				break;
			case "receiveEndDate":
				originalTime = dReceiveEndTime;
				tempTime = $("[itemName='receiveEndDate']").val();
				break;
		}
		if(isOrigin){
			return originalTime;
		}else{
			if(tempTime != ""){
				var date = new Date(Date.parse(tempTime));
				return date;
			}else{
				return null;
			}
		}
	}
	
	function checkIsTimeFielsCorrect(){
		var isValid = true;
		var pStartTime = $("#datetimepicker_startTime");
		var pEndTime = $("#datetimepicker_endTime");
		var pCompanySendDate = $("#datetimepicker_companySendDate");
		var pDrawTime = $("#datetimepicker_drawTime");
		var pReceiveBeginDate = $("#datetimepicker_receiveBeginDate");
		var pReceiveEndDate = $("#datetimepicker_receiveEndDate");
		if(pStartTime.val() == ""){
			$('#active_start_hint').text("Event start time field is required! ");
			$('#active_start_hint').css("color", "#FF0000");
			pStartTime.css("background-color","#ffd1cc");
			isValid = false;
		}
		if(pEndTime.val() == ""){
			$('#active_end_hint').text("Event end time field is required! ");
			$('#active_end_hint').css("color", "#FF0000");
			pEndTime.css("background-color","#ffd1cc");
			isValid = false;
		}
		if(pCompanySendDate.val() == ""){
			$('#send_hint').text("Send letter time field is required! ");
			$('#send_hint').css("color", "#FF0000");
			pCompanySendDate.css("background-color","#ffd1cc");
			isValid = false;
		}
		if(pDrawTime.val() == ""){
			$('#draw_hint').text("Draw time has to be greater than the event start time! ");
			$('#draw_hint').css("color", "#FF0000");
			pDrawTime.css("background-color","#ffd1cc");
			isValid = false;
		}
		if(!isValid){
			alert("Please fill in the required time fields! ");
			return isValid;
		}
		
		if(($("#datetimepicker_startTime").css('background-color') != 'rgb(255, 255, 255)')
			|| ($("#datetimepicker_endTime").css('background-color') != 'rgb(255, 255, 255)')
			|| ($("#datetimepicker_companySendDate").css('background-color') != 'rgb(255, 255, 255)')
			|| ($("#datetimepicker_drawTime").css('background-color') != 'rgb(255, 255, 255)')
			|| ($("#datetimepicker_receiveBeginDate").css('background-color') != 'rgb(255, 255, 255)')
			|| ($("#datetimepicker_receiveEndDate").css('background-color') != 'rgb(255, 255, 255)')){
			isValid = false;
			alert("Exist invalid time! ");
			return isValid;
		}
		var nowTime = new Date();
		if(getInputTime("startTime",true) != "" && ggetInputTime("startTime",true) < nowTime){
			pStartTime.val(startTime);
		}
		if(getInputTime("endTime",true) != "" && ggetInputTime("endTime",true) < nowTime){
			pEndTime.val(endTime);
		}
		if(getInputTime("companySendDate",true) != "" && ggetInputTime("companySendDate",true) < nowTime){
			pCompanySendDate.val(companySendTime);
		}
		if(getInputTime("drawTime",true) != "" && ggetInputTime("drawTime",true) < nowTime){
			pDrawTime.val(drawTime);
		}
		if(getInputTime("receiveBeginDate",true) != "" && getInputTime("receiveBeginDate",true) < nowTime){
			pReceiveBeginDate.val(receiveBeginTime);
		}
		if(getInputTime("receiveEndDate",true) != "" && getInputTime("receiveEndDate",true) < nowTime){
			pReceiveEndDate.val(receiveEndTime);
		}
		return isValid;
	}
	
	$("#clearRewardTime").click(function(){
		$("#datetimepicker_receiveBeginDate").val("");
		$("#datetimepicker_receiveBeginDate").css("background-color","");
		$('#receive_start_hint').text("");
		$('#receive_start_hint').css("color", "");
		$("#datetimepicker_receiveEndDate").val("");
		$("#datetimepicker_receiveEndDate").css("background-color","");
		$('#receive_end_hint').text("");
		$('#receive_end_hint').css("color", "");
	});
	
	// Setting time related region -- end --
	
	
	var companyEmailMaxIdx = ${fn:length(actionBean.brandEventDetail.companyEmail)};
	var pfEmailMaxIdx = ${fn:length(actionBean.brandEventDetail.pfEmail)};
	var storeCityMaxIdx = ${fn:length(actionBean.brandEventDetail.event.stores)};
	var storeDetailMaxIdx = [];
	
	if(companyEmailMaxIdx == 0){
		companyEmailMaxIdx++;
	}
	if(pfEmailMaxIdx == 0){
		pfEmailMaxIdx++;
	}
	if(storeCityMaxIdx == 0){
		storeCityMaxIdx++;
	}
	<c:forEach var="allStores" items="${actionBean.brandEventDetail.event.stores}"  varStatus="loop">
			storeDetailMaxIdx[${loop.index}] = ${fn:length(allStores.stores)} + 1;
	</c:forEach>
	
	function handleBtnEvent(obj){
		var $this = obj;
		var delType = $this.attr("act");
		var idx = $this.attr("idx");
		var cityIdx = $this.attr("cityIdx");
		switch(delType){
			case 'delCompanyEmail':
				removeEmailField('companyEmail', idx);
				break;
			case 'delPfEmail':
				removeEmailField('pfEmail', idx);
				break;
			case 'addCompanyEmailField':
				addEmailField('companyEmail');
				break;
			case 'addPfEmailField':
				addEmailField('pfEmail');
				break;
			case 'delStore':
				removeStoreField(cityIdx,idx);
				break;
			case 'delStoreCity':
				removeStoreCityField(cityIdx);
				break;
			case 'addStoreField':
				addStoreDetailField(cityIdx);
				break;
			case 'addCityField':
				addCityField();
				break;
		}
	}
	
	function addCityField(){
		var cityIdx = storeCityMaxIdx;
		var newDiv = $(document.createElement('div')).attr("id", "storeCity_" + cityIdx ).attr("class","city");
		
		var newCityDiv = $(document.createElement('div')).attr("style","background-color:#BDE7FF");
		newCityDiv.after().html("<input type='text' name='storeCity'  cityIdx='" + cityIdx + "' style='font-size:14px;line-height:20px;width:200px;' value=''/>&nbsp;<input type='button' act='delStoreCity' cityIdx='" + cityIdx + "' class='deleteBtn_store' value='&#10006;'/>");
		newCityDiv.appendTo(newDiv);
		var newStoreDiv = $(document.createElement('div')).attr("id", "storeDetail_" + cityIdx );
		newStoreDiv.appendTo(newDiv);
		var newButton = $(document.createElement('input')).attr("type", "button").attr("cityIdx",cityIdx).attr("act","addStoreField").attr("value","Add More Store Field");
		newButton.appendTo(newDiv);
		storeDetailMaxIdx[cityIdx] = 1;
		newDiv.appendTo("#allStore");
		$("input[type='button'][act='addStoreField'][cityIdx='" + cityIdx + "']").on("click", '', function () {
			handleBtnEvent($(this));
		});
		$("input[type='button'][act='delStoreCity'][cityIdx='" + cityIdx + "']").on("click", '', function () {
			handleBtnEvent($(this));
		});
		addStoreDetailField(cityIdx);
		storeCityMaxIdx++;
	}
	
	function addStoreDetailField(cityIdx){
		var idx = storeDetailMaxIdx[cityIdx];
		var newDiv = $(document.createElement('div')).attr("id", "store_" + cityIdx + "_" + idx);
		newDiv.after().html("<input type='text' name='storeName' cityIdx='" + cityIdx + "' idx='" + idx + "' style='font-size:14px;line-height:20px;width:200px;' value=''/>&nbsp;<input type='text' name='storeAdress' cityIdx='" + cityIdx + "' idx='" + idx + "' style='font-size:14px;line-height:20px;width:430px;' value=''/>&nbsp;<input type='button' act='delStore' cityIdx='" + cityIdx + "' idx='" + idx + "' class='deleteBtn_store' value='&#10006;'>");
		newDiv.appendTo("#storeDetail_" + cityIdx);
		$("input[type='button'][act='delStore'][cityIdx='" + cityIdx + "'][idx='" + idx + "']").on("click", '', function () {
			handleBtnEvent($(this));
		});
		storeDetailMaxIdx[cityIdx] = ++idx;
	}

	function removeStoreCityField(cityIdx){
		var removeDivId = "#storeCity_" + cityIdx;
		$(removeDivId).remove();
	}
	
	function removeStoreField(cityIdx,idx){
		var removeDivId = "#store_" + cityIdx + "_" + idx;
		$(removeDivId).remove();
	};
	
	function addEmailField(typeName){
		var idx = 0;
		switch(typeName){
			case 'companyEmail':
				idx = companyEmailMaxIdx;
				var newTextBoxP = $(document.createElement('p')).attr("name", typeName).attr("idx", idx);
				newTextBoxP.after().html("<input type='text' format='email' name='companyEmailList' placeholder='please enter email(e.g. xxxx@gmail.com)' style='font-size:16px;line-height:30px;width:400px;' value=''/>&nbsp;<input type='button' class='deleteBtn' act='delCompanyEmail' idx='" + idx + "' value='&#10006;'>");
				newTextBoxP.appendTo("#companyEmailGroup");
				$("input[type='button'][act='delCompanyEmail'][idx='"+idx+"']").on("click", '', function () {
					handleBtnEvent($(this));
				});
				companyEmailMaxIdx++;
				break;
			case 'pfEmail':
				idx = pfEmailMaxIdx;
				var newTextBoxP = $(document.createElement('p')).attr("name", typeName).attr("idx", idx);
				newTextBoxP.after().html("<input type='text' format='email' name='pfEmailList' placeholder='please enter email(e.g. xxxx@gmail.com)' style='font-size:16px;line-height:30px;width:400px;' value=''/>&nbsp;<input type='button' class='deleteBtn' act='delPfEmail' idx='" + idx + "' value='&#10006;'>");
				newTextBoxP.appendTo("#pfEmailGroup");
				$("input[type='button'][act='delPfEmail'][idx='"+idx+"']").on("click", '', function () {
					handleBtnEvent($(this));
				});
				pfEmailMaxIdx++;
				break;
			default:
				break;
		}
		$("input[format='email']").on("focusout", '', function () {
			if(!checkEmailFormat($(this))){
				$(this).css("background-color", "#ffd1cc");
			}else{
				$(this).css("background-color", "");
			}
		});
	}
	
	function removeEmailField(typeName, idx){
		$("p[name='"+typeName+"'][idx='" + idx + "']").remove();
	}
	
	function checkEmailFormat(obj){
		var $this = obj;
		var email = $this.val();
		var regu =  "^(([0-9a-zA-Z]+)|([0-9a-zA-Z]+[_.0-9a-zA-Z-]*[0-9a-zA-Z]+))@([a-zA-Z0-9-]+[.])+([a-zA-Z]{2}|net|NET|com|COM|gov|GOV|mil|MIL|org|ORG|edu|EDU|int|INT)$";
		var re = new RegExp(regu);
	　　 if (email == '' || email.search(re) != -1) {
	　　 	return true;
	　　 } else {
	　　	 	return false;
	　　 }
	}
	
	
	$("input[type='button']").click(function(){ handleBtnEvent($(this));});
	
	
	$("input[format='email']").focusout(function(){
		if(!checkEmailFormat($(this))){
			$(this).css("background-color", "#ffd1cc");
		}else{
			$(this).css("background-color", "");
		}
	});
	
	$("#saveFileEdit").click(function(){
		
		var isTimeCorrect = checkIsTimeFielsCorrect();
		
		var formatCorrect = true;
		$("input[format='email']").each(function(){
			if(!checkEmailFormat($(this))){
				formatCorrect = false;
			}
		});
		if(!formatCorrect){
			alert("Please enter valid Emails address!");
		}
		if(formatCorrect == true){
			var cityList = [];
			for(cityIdx = 0 ; cityIdx < storeCityMaxIdx ; cityIdx++){
				var city = $("input[type='text'][name='storeCity'][cityIdx='" + cityIdx + "']").val();
				if(city != null && city != "undefined" && city != ""){
					var storeList = [];
					var storeDetailMaxNum = storeDetailMaxIdx[cityIdx];
					for(idx = 0 ; idx < storeDetailMaxNum ; idx++){
						var storeName = $("input[type='text'][name='storeName'][cityIdx='" + cityIdx + "'][idx='" + idx +"']").val();
						var storeAdress = $("input[type='text'][name='storeAdress'][cityIdx='" + cityIdx + "'][idx='" + idx +"']").val();
						if(storeName != null && storeName != "undefined" && storeName != ""){
							storeList.push(new Store(storeName, storeAdress));
						}
					}
					cityList.push(new CityStore(city,storeList));
				}
			}
			if(cityList.length > 0){
				$("#storesInfo").val(JSON.stringify(cityList));	
			}
		}
		if(formatCorrect && isTimeCorrect)
			return true;
		else 
			return false;
	});
	
    /**
     * Store Object
     */
    function Store(name, address){
    	this.name = name;
    	this.address = address;
    }
    
    /**
     * CityStore Object
     */
    function CityStore(city, storeList){
    	this.city = city;
    	this.stores = storeList;
    }
});
</script>