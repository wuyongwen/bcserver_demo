<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/jquery-ui.min.js" />"></script>
<script src="<c:url value="/common/lib/general.js" />"></script>

<h2 class=ico_mug>Campaign :: Campaign Group Event Management</h2>
<div>
	<input id="createGroup" type="button" class="button" value="Create New Campaign Group Event" onclick="location.href='campaignManager.action?modifyGroupEvent&isCreate=true'" />
	<display:table id="row" name="actionBean.campaignGroupList" requestURI="campaignManager.action" pagesize="20" sort="page" partialList="true" size="${fn:length(actionBean.campaignGroupList)}" export="false" >
		<display:column title="Group Name" sortable="true" >
			<c:out value="${row.name}" />
		</display:column>
		<display:column title="Period" sortable="true" >
			<c:out value="${row.rotationPeriod}" />
		</display:column>
		<display:column title="Action" sortable="false" style="width:600px;">
			<input id="EditCampaign" type="button" class="button" value="Edit Locale Campaign Groups" onclick="location.href='campaignManager.action?routeGroup&isUpdate=true&campaignGroupName=${row.name}'" />
			&nbsp; | &nbsp;
			<input id="Modify" type="button" class="button" value="Modify" onclick="location.href='campaignManager.action?modifyGroupEvent&isUpdate=true&campaignGroupName=${row.name}'" />
			&nbsp; | &nbsp;
			<input id="create" type="button" class="button" value="Delete" onclick="if(confirm('Are you sure you want to delete?')){location.href='campaignManager.action?deleteGroupEvent&campaignGroupName=${row.name}'}" />
		</display:column>
	</display:table>
</div>