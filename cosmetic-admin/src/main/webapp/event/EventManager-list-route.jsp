<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<h2 class=ico_mug>Event :: Event Management</h2>
<s:form name="listEventForm" id="listEventForm" beanclass="${actionBean.class}">
	Type : <s:select name="serviceType" id="serviceType">
		<s:option value="FREE_SAMPLE">Free Sample</s:option>
		<s:option value="CONSULTATION">Consultation</s:option>
    </s:select>
	Locale : <s:select name="locale" id="locale">
		<s:options-collection collection="${actionBean.eventLocales}" />
    </s:select>
    <s:submit name="listRoute">Change</s:submit>
</s:form>
<div>
	<display:table id="row" name="actionBean.pageResult.results" requestURI="EventManager.action" pagesize="20" sort="page" partialList="true" size="actionBean.pageResult.totalSize" export="false" >
		<display:column title="Id">
			<a href="./EventManager.action?detail&brandEventId=${row.event.id}" target="_blank">${row.event.id}</a>
		</display:column>
		<display:column title="Title">
			${row.event.title}
		</display:column>
		<display:column title="Priority">
			${row.event.priority}
		</display:column>
		<display:column title="Status">
			${row.status}
		</display:column>
		<display:column title="Notify Time">
			<c:if test="${row.hasNotifyTime eq true}">
				${row.notifyTime}
        	</c:if>
        	<c:if test="${row.hasNotifyTime eq false}">
				None
        	</c:if>
		</display:column>
		<display:column title="WinnerList">
			${row.hasWinnerList}
		</display:column>
		<display:column title="Action">
			<input name="deleteBtn" type="button" id="deleteBtn" onclick="deleteEvent(this);" value="Delete" eventId="${row.event.id}">
		</display:column>
	</display:table>
</div>
<div id="progressDialog" title="Processing">
</div>

<script>
	function deleteEvent(btn) {
		var apiUrl = "./EventManager.action?delete";
		var brandEventId = "brandEventId=" + btn.getAttribute("eventId");
		var locale = "locale=" + document.getElementById("locale").value;
		window.location.href = apiUrl + "&" + brandEventId + "&" + locale;
	}
</script>