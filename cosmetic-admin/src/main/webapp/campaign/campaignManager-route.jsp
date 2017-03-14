<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/jquery-ui.min.js" />"></script>
<script src="<c:url value="/common/lib/general.js" />"></script>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>
<h2 class=ico_mug>Campaign :: Campaign Management</h2>
<div>
	<h3>Locale : <c:out value="${actionBean.campaignGroup.locale}"/></h3><br />
	<h3>Group Name : <c:out value="${actionBean.campaignGroup.name}"/></h3><br />
	<h3>Period : <c:out value="${actionBean.campaignGroup.rotationPeriod}"/></h3><br />
	<input id="return" type="button" class="button" value="<<" onclick="location.href='campaignManager.action?routeGroup&campaignGroupName=${actionBean.campaignGroupName}'" />
	<input id="create" type="button" class="button" value="Create New Campaign" onclick="location.href='campaignManager.action?modify&isCreate=true&campaignGroupId=${actionBean.campaignGroupId}&campaignGroupName=${actionBean.campaignGroupName}'" />
	<display:table id="row" name="actionBean.campaignList" requestURI="campaignManager.action" pagesize="20"sort="page"  partialList="true" size="${fn:length(actionBean.campaignList)}" export="false" >
		<display:column title="Id" sortable="true" style="width:160px;">
			<c:out value="${row.id}" />
		</display:column>
		<display:column title="File Id 720" sortable="true" style="width:160px;">
			<c:out value="${row.file720Id}" />
		</display:column>
		<display:column title="File Id 1080" sortable="true" style="width:160px;">
		 	<c:out value="${row.file1080Id}" />
		</display:column>
		<display:column title="Link" sortable="true" >
		 	<c:out value="${row.link}" />
		</display:column>
		<display:column title="End Day" sortable="true" style="width:200px;">
		 	<fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${row.endDate}" />
		</display:column>
		<display:column title="Action" sortable="false" style="width:200px;">
			<input id="modify" type="button" class="button" value="Modify" onclick="location.href='campaignManager.action?modify&isUpdate=true&campaignId=${row.id}&campaignGroupId=${actionBean.campaignGroupId}&campaignGroupName=${actionBean.campaignGroupName}'" />
			&nbsp; | &nbsp;
			<input id="delete" type="button" class="button" value="Delete" onclick="if(confirm('Are you sure you want to delete?')){location.href='campaignManager.action?delete&campaignId=${row.id}&campaignGroupId=${actionBean.campaignGroupId}&campaignGroupName=${actionBean.campaignGroupName}'}" />
		</display:column>
	</display:table>
	<input type="hidden" name="campaignGroupId" value="${actionBean.campaignGroupId}"/>
	<input type="hidden" name="campaignGroupName" value="${actionBean.campaignGroupName}"/>
</div>