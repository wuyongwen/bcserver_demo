<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/jquery-ui.min.js" />"></script>
<script src="<c:url value="/post/defaultTag.js?v=${randVer}" />"></script>

<h2 class=ico_mug>Post :: Post Default Tag Management</h2>
<div>
	<input id="newTag" type="button" value="New Default Post Tag" />
	<display:table id="row" name="actionBean.defaultTags" requestURI="post-default-tag-manager.action" pagesize="20" sort="page" partialList="true" size="${fn:length(actionBean.defaultTags)}" export="false" >
		<display:column title="Id" sortable="true" style="width:10%;">
			<c:out value="${row.id}" />
		</display:column>
		<display:column title="Locale" sortable="true" style="width:10%;">
			<c:out value="${row.locale}" />
		</display:column>
		<display:column title="name" sortable="false" style="width:10%;">
			<c:out value="${row.tagName}" />
		</display:column>
		<display:column title="Action" sortable="false" style="width:40%;">
			<a href="post-default-tag-manager.action?newTag&defaultTagId=${row.id}">Modify</a>
			&nbsp; | &nbsp;
			<a href="post-default-tag-manager.action?copy&defaultTagId=${row.id}">Copy</a>
			&nbsp; | &nbsp;
			<c:choose>
				<c:when test="${row.isDeleted}">
					<input type="button" class="showTagBtn" ild="${row.id}" value="Show" />
				</c:when>
				<c:otherwise>
					<input type="button" class="hideTagBtn" ild="${row.id}" value="Hide" />
				</c:otherwise>
			</c:choose>
		</display:column>
	</display:table>
</div>
<div id="progressDialog" title="Processing">
</div>