<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/jquery-ui.min.js" />"></script>
<script src="<c:url value="/common/lib/general.js" />"></script>

<h2 class=ico_mug>Campaign :: Campaign Group Management</h2>
<div>
	<h3>Group Name : <c:out value="${actionBean.campaignGroup.name}"/></h3><br />
	<h3>Period : <c:out value="${actionBean.campaignGroup.rotationPeriod}"/></h3><br />
	<input id="return" type="button" class="button" value="<<" onclick="location.href='campaignManager.action?routeGroupEvent'" />
	<input id="createGroup" type="button" class="button" value="Create A New Campaign For All Campaign Groups" onclick="location.href='campaignManager.action?modifyCreateAll&isCreate=true&campaignGroupName=${actionBean.campaignGroupName}'" />
	<display:table id="row" name="actionBean.campaignGroupList" requestURI="campaignManager.action" pagesize="20" sort="page" partialList="true" size="${fn:length(actionBean.campaignGroupList)}" export="false" >
		<display:column title="Campaign Group Id" sortable="true">
			<c:out value="${row.id}" />
		</display:column>
		<display:column title="Locale" sortable="true" >
		 	<c:out value="${row.locale}" />
		</display:column>
		<display:column title="Action" sortable="false" style="width:300px;">
			<input id="EditCampaign" type="button" class="button" value="Edit Campaigns" onclick="location.href='campaignManager.action?route&isUpdate=true&campaignGroupId=${row.id}&campaignGroupName=${actionBean.campaignGroupName}'" />
			&nbsp; | &nbsp;
			<input id="delete" type="button" class="button" value="Delete" onclick="if(confirm('Are you sure you want to delete?')){location.href='campaignManager.action?deleteGroup&campaignGroupId=${row.id}'}" />
		</display:column>
	</display:table>
	<input type="hidden" name="campaignGroupName" value="${actionBean.campaignGroupName}"/>
</div>