<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
$(document).ready(function(){
	$("input[name='invalidEventUserIds']").click(function(){ 
		var isRedrawVacancy = false;
		$("input[name='invalidEventUserIds']").each(function(){
			if($(this).prop("checked") == true)
				isRedrawVacancy = true;
		});
		if(isRedrawVacancy){
			$("input[name='redrawVacancy']").attr("style","display:blocked;");
		}else{
			$("input[name='redrawVacancy']").attr("style","display:none;");
		}
	});
	
	$("input[name='redrawVacancy']").click(function(){
		var invalidUserId = [];
		$("input[name='invalidEventUserIds']").each(function(){
			if($(this).prop("checked") == true){
				invalidUserId.push($(this).prop("value"));
			}
		});
		$("input[name='invalidEventUserIdList']").prop("value",invalidUserId.toString());
	});
});
</script>
<h2 class=ico_mug>Event :: Event Draw Management</h2>

<c:if test="${actionBean.isFinished eq true}">
	<div>Draw finished</div>
</c:if>
<div>
	<c:if test="${actionBean.isFinished eq false}">
		<display:table id="row" name="actionBean.selectedEventUsers.results" requestURI="EventDrawManager.action" pagesize="${actionBean.defaultPageSize}" sort="page" partialList="true" size="actionBean.selectedEventUsers.totalSize" export="false" >
			<display:column title="Id">
				${row.id}
			</display:column>
			<display:column title="Display Name">
				${row.displayName}
			</display:column>
			<display:column title="Real Name">
				${row.name}
			</display:column>
			<display:column title="Birthday">
				${row.birthDayString}
			</display:column>
			<c:if test="${actionBean.receiveType ne 'Coupon'}">
				<display:column title="Phone">
					${row.phone}
				</display:column>
			</c:if>
			<display:column title="Email">
				${row.mail}
			</display:column>
			<c:if test="${actionBean.receiveType eq 'Store'}">
				<display:column title="Store Location">
					${row.storeLocation}
				</display:column>
				<display:column title="Store Name">
					${row.storeName}
				</display:column>
				<display:column title="Store Address">
					${row.storeAddress}
				</display:column>
			</c:if>
			<c:if test="${actionBean.receiveType eq 'Home'}">
				<display:column title="User Address">
					${row.userAddress}
				</display:column>
			</c:if>
		</display:table>
	</c:if>
	<c:if test="${actionBean.isFinished eq true}">
		<display:table id="row" name="actionBean.selectedEventUsers.results" requestURI="EventDrawManager.action" pagesize="${actionBean.defaultPageSize}" sort="page" partialList="true" size="actionBean.selectedEventUsers.totalSize" export="true" >
			<display:setProperty name="export.types" value="excel" />
			<display:setProperty name="export.excel.include_header" value="true" />
			<display:setProperty name="export.excel.filename" value="EventWinnerUser.xls" />
			<display:setProperty name="export.csv" value="false" />
			<display:setProperty name="export.xml" value="false" />
			<display:column title="Id">
				${row.id}
			</display:column>
			<display:column title="Display Name">
				${row.displayName}
			</display:column>
			<display:column title="Real Name">
				${row.name}
			</display:column>
			<display:column title="Birthday">
				${row.birthDayString}
			</display:column>
			<c:if test="${actionBean.receiveType ne 'Coupon'}">
				<display:column title="Phone">
					${row.phone}
				</display:column>
			</c:if>
			<display:column title="Email">
				${row.mail}
			</display:column>
			<c:if test="${actionBean.receiveType eq 'Store'}">
				<display:column title="Store Location">
					${row.storeLocation}
				</display:column>
				<display:column title="Store Name">
					${row.storeName}
				</display:column>
				<display:column title="Store Address">
					${row.storeAddress}
				</display:column>
			</c:if>
			<c:if test="${actionBean.receiveType eq 'Home'}">
				<display:column title="User Address">
					${row.userAddress}
				</display:column>
			</c:if>
			<c:if test="${actionBean.receiveType eq 'Coupon'}">
				<display:column title="eCoupon">
					${row.code}
				</display:column>
			</c:if>
			<c:if test="${actionBean.isNotifyed eq false}">
				<display:column title="Set Invalid" media="html">
					<c:if test="${row.isInvalid eq false}">
						<input class="checkbox" style="width:20px;height:20px;cursor: pointer;" type="checkbox" name="invalidEventUserIds" value="${row.id}" />	
					</c:if>
				</display:column>
			</c:if>
		</display:table>
	</c:if>
</div>
<s:form name="listEventForm" id="listEventForm" beanclass="${actionBean.class}">
	<div>
		<c:if test="${actionBean.isFinished eq false}">
			<s:submit name="confirmRoute" value="Confirm" class="button"/>
			<s:submit name="reDraw" value="ReDraw" class="button"/>
		</c:if>
		<c:if test="${actionBean.isFinished eq true and actionBean.isSend eq true}">
			<s:submit name="sendNotify" value="SendNotify" class="button"/>
		</c:if>
		<c:if test="${actionBean.isFinished eq true and actionBean.isSend eq false}">
			<s:submit name="sendNotify" value="SendNotify" disabled="disabled" class="button"/>
		</c:if>
		<c:if test="${actionBean.isNotifyed eq false and actionBean.isFinished eq true}">
			<s:submit id="redrawVacancy" name="redrawVacancy" value="Set invalidate and redraw" style="display:none;" class="button"/>
		</c:if>
	</div>
	<s:hidden name="brandEventId" value="${actionBean.brandEventId}"/>
	<s:hidden name="receiveType" value="${actionBean.receiveType}"/>
	<s:hidden name="selectedEventUserIds" value="${actionBean.selectedEventUserIds}"/>
	<s:hidden name="invalidEventUserIdList" value=""/>
	<s:hidden name="isSend" value=""/>
</s:form>
