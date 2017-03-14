<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/decorators/backend/styles.jsp"%>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000)%></c:set>
<script src="<c:url value="/user/badge.js?v=${randVer}" />"></script>
<script src="<c:url value="/post/spinner/spin.js" />"></script>

<div id="displayDiv" style="width:100%;">
<label>Locale :  </label>
<select id="selLocale">
<c:forEach items="${actionBean.availableRegion}" var="locale" varStatus="loop">
<c:choose>
	<c:when test="${locale eq actionBean.selLocale}">
		<option value="${locale}" selected>${locale}</option>
	</c:when>
	<c:otherwise>
		<option value="${locale}">${locale}</option>
	</c:otherwise>
</c:choose>
</c:forEach>
</select>

<label>Action :  </label>
<select id="pageAction">
<option value="select" ${actionBean.pageAction == 'select' ? 'selected' : ''}>Candidate</option>
<option value="view" ${actionBean.pageAction == 'view' ? 'selected' : ''}>Weekly Star</option>
</select>
</div>

<c:choose>
	<c:when  test="${not empty actionBean.error}">
		<p style="font-weight:bold;color:#FC032C;font-size:15px;text-align:center;">
			${actionBean.error}
		</p>
	</c:when>
	<c:otherwise>
		<display:table id="user" name="actionBean.usersList" requestURI="badge-program.action" pagesize="50" size="50" export="false" >
			<display:column title="Avatar">
				<img src="${user['avatar']}" style="max-height: 50px; max-width: 50px;" />
			</display:column>
		   	<display:column title="Id" sortable="true" style="font-size: 20px;">
		       <c:out value="${user['id']}"/>
		   	</display:column>
		   	<display:column title="Name" sortable="true" style="font-size: 20px;">
		   		<a href="${user['profile']}" target="_blank">${user['name']}</a>
		   	</display:column>
		   	<display:column title="Post Count" sortable="true" style="font-size: 20px;">
		       <c:out value="${user['postCount']}" />
		   	</display:column>
		   	<display:column title="Score" sortable="true" style="font-size: 20px;">
		   		<label class="score">${user['score']}</label>
		   	</display:column>
		   	<c:choose>
			   	<c:when test="${actionBean.pageAction eq 'view'}">
			   		<display:column title="Create Time" sortable="true" style="font-size: 20px;">
			   		<c:out value="${user['createTime']}"/>
			   		</display:column>
			   	</c:when>
			   	<c:otherwise>
			   		<display:column title="Select" class="nonPropagate clickableTd">
			   		<c:choose>
			   			<c:when test="${user['selected']}">
			   				<input class="nonPropagate clickTarget checkUser" type="checkbox" uid="${user['id']}" style="transform: scale(3.5); margin:20px;" checked />
			   			</c:when>
			   			<c:otherwise>
			   				<input class="nonPropagate clickTarget checkUser" type="checkbox" uid="${user['id']}" style="transform: scale(3.5); margin:20px;"/>
			   			</c:otherwise>
		   			</c:choose>
			   		</display:column>
			   	</c:otherwise>
		   	</c:choose>
		</display:table>
		<c:if test="${actionBean.pageAction eq 'select'}">
			<div align="right" style="with:80%; margin:0 12% 0 10%;" id="saveButtonDiv">
				<button style="font-size:14px;" class="button" id="addCheckedUser">Submit</button>
			</div>
		</c:if>
	</c:otherwise>
</c:choose>