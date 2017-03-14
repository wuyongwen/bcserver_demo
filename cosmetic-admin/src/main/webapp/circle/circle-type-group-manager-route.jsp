<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/jquery-ui.min.js" />"></script>
<script src="<c:url value="/circle/circle.js?v=${randVer}" />"></script>
<script src="<c:url value="/common/lib/general.js" />"></script>

<h2 class=ico_mug>Circle :: Circle Management</h2>
<div>
	<input id="newGroup" type="button" value="New Group" />
	<display:table id="row" name="actionBean.pageResult.results" requestURI="circle-type-group-manager.action" pagesize="20" sort="page" partialList="true" size="actionBean.pageResult.totalSize" export="false" >
		<display:column title="Group Id" sortable="true" style="width:10%;">
			${row.id}
		</display:column>
		<display:column title="Group Name" sortable="true" style="width:10%;">
			${row.groupName}
		</display:column>
		<display:column title="Group Order" sortable="true" style="width:10%;">
			${row.sortOrder}
		</display:column>
		<display:column title="Image" sortable="false" style="width:10%;">
			<img src="${row.imgUrl}" style="max-width:180px;"/>
		</display:column>
		<display:column title="Action" sortable="true" style="width:40%;">
			<c:if test="${!row.isDeleted}">
	          	<input class="delete" type="button" value="Delete" id="${row.id}" />  
	          	<input class="add" type="button" value="Modify" id="${row.id}" />  
          	</c:if>  
		</display:column>
	</display:table>
</div>
<div id="progressDialog" title="Processing">
</div>