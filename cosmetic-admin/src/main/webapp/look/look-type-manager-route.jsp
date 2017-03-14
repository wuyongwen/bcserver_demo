<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/jquery-ui.min.js" />"></script>
<script src="<c:url value="/look/look.js?v=${randVer}" />"></script>
<script src="<c:url value="/common/lib/general.js" />"></script>

<h2 class=ico_mug>Look :: Look Type Management</h2>
<div>
	<input id="newType" type="button" value="New Look Type" />
	<display:table id="row" name="actionBean.lookTypes" requestURI="look-type-manager.action" pagesize="20" sort="page" partialList="true" size="${fn:length(actionBean.lookTypes)}" export="false" >
		<display:column title="Id" sortable="true" style="width:10%;">
			<c:out value="${row.id}" />
		</display:column>
		<display:column title="Locale" sortable="true" style="width:10%;">
			<c:out value="${row.locale}" />
		</display:column>
		<display:column title="name" sortable="true" style="width:10%;">
			<c:out value="${row.name}" />
		</display:column>
		<display:column title="name" sortable="true" style="width:10%;">
			<c:out value="${row.codeName}" />
		</display:column>
		<display:column title="Background Image" sortable="true" sortProperty="bgImgId" style="width:40%;">
			<img src="${row.bgImgUrl}" height="50px" width="50px" /><br>
			<label>${row.bgImgId}</label><br>
			<label>${row.bgImgUrl}</label>
		</display:column>
		<display:column title="Action" sortable="false" style="width:40%;">
			<a href="look-type-manager.action?newType&lookTypeId=${row.id}">Modify</a>
			&nbsp; | &nbsp;
			<a href="look-type-manager.action?copy&lookTypeId=${row.id}">Copy</a>
			&nbsp; | &nbsp;
			<c:choose>
				<c:when test="${row.isDeleted}">
					<label ild="${row.id}">Deleted</label>
				</c:when>
				<c:when test="${row.isVisible}">
					<input type="button" class="hideTypeBtn" ild="${row.id}" value="Hide" />
				</c:when>
				<c:otherwise>
					<input type="button" class="showTypeBtn" ild="${row.id}" value="Show" />
				</c:otherwise>
			</c:choose>
		</display:column>
	</display:table>
</div>
<div id="progressDialog" title="Processing">
</div>