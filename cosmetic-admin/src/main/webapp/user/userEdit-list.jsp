<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<script>
function search(btn)
{
	var apiUrl = "./userEdit.action?list";
	var searchId = "searchId=" + document.getElementById("searchById").value;
	var searchGender = "searchGender=" + document.getElementById("genderId").value;
	var searchUserType = "searchUserType=" + document.getElementById("searchUserTypeId").value;
	var searchUserAccess = "searchUserAccess=" + document.getElementById("searchUserAccessId").value;
	var searchLocale = "searchLocale=" + document.getElementById("locale").value;
	var searchEmail = "searchEmail=" + document.getElementById("searchByEmail").value;
	window.location.href = apiUrl + "&" + searchId + "&" + searchGender + "&" + searchUserType +"&" + searchUserAccess + "&" + searchLocale + "&" + searchEmail + "&isSearch=true";
}
</script>

<h2 class=ico_mug>User :: User Management</h2>
<div class=clearfix>

<s:form beanclass="${actionBean.class}" method="get">
	Region
	<s:select name="searchLocale" id="locale">
		<s:option value="">--ALL--</s:option>
		<s:options-collection collection="${actionBean.userLocaleList}" />
	</s:select>
	&nbsp;|&nbsp;Gender
	<s:select name="searchGender" id="genderId">
		<s:option value="">--ALL--</s:option>
		<s:option value="Male">Male</s:option>
		<s:option value="Female">Female</s:option>
		<s:option value="Unspecified">Unspecified</s:option>
	</s:select>
	&nbsp;|&nbsp;UserType
	<s:select name="searchUserType" id="searchUserTypeId" >
		<s:option value="">--ALL--</s:option>
		<s:option value="Normal">Normal</s:option>
		<s:option value="Expert">Expert</s:option>
		<s:option value="CL">CL</s:option>
		<s:option value="Blogger">Blogger</s:option>
		<s:option value="Master">Master</s:option>
		<s:option value="Brand">Brand</s:option>
		<s:option value="Publisher">Publisher</s:option>
		<s:option value="Celebrity">Celebrity</s:option>
		<s:option value="LiveBrand">LiveBrand</s:option>
		<s:option value="Anchor">Anchor</s:option>
	</s:select>
	&nbsp;|&nbsp;Access
	<s:select name="searchUserAccess" id="searchUserAccessId" >
		<s:option value="">--ALL--</s:option>
		<s:option value="0">Administrator</s:option>
		<s:option value="1">User manager</s:option>
		<s:option value="2">Post manager</s:option>
		<s:option value="4">Circle manager</s:option>
		<s:option value="8">Product manager</s:option>
		<s:option value="16">Report manager</s:option>
		<s:option value="32">Report auditor</s:option>
		<s:option value="64">Event manager</s:option>
	</s:select>
</s:form>
<br/>
	Just Search By Id
	<input name="searchId" type="text" id=searchById>
<br/>
	Just Search By Email
	<input name="searchEmail" type="text" id=searchByEmail size="50">
<br/>
	<input name="searchBtn" type="button" id="searchBtn" onclick="search(this);" value="Search">
<c:if test="${actionBean.isSearch eq true}">
	<display:table id="row" name="actionBean.pageResult.results" requestURI="userEdit.action" pagesize="20" sort="page" partialList="true" size="actionBean.pageResult.totalSize" export="false" >
	    <display:column title="userId" sortable="true" style="width:50px">
	        <c:out value="${row.id}"/>
	    </display:column>
	    <display:column title="avatar" sortable="false">
	        <img src="${row.avatarUrl}" height="50" width="50" />
	    </display:column>
	    <display:column title="displayName" sortable="true" sortName="displayName">
	        <c:out value="${row.displayName}"/>
	    </display:column>
	    <display:column title="email" sortable="false">
	        <c:out value="${row.allEmailAccountList[0].email}"/>
	    </display:column>
	    <display:column title="region" sortable="false">
	        <c:out value="${row.region}"/>
	    </display:column>
	    <display:column title="birthDay" sortable="false">
	        <c:out value="${row.birthDay}"/>
	    </display:column>
	    <display:column title="userType" sortable="false">
	        <c:out value="${row.userType}"/>
	    </display:column>
	    <display:column title="description" sortable="false" style="width:300px">
	        <c:out value="${row.description}"/>
	    </display:column>
	    <display:column title="status" sortable="false" style="width:100px;">
	        <c:out value="${row.userStatus}"/>
	    </display:column>
	    <display:column title="token" sortable="false">
	        <c:out value="${row.token}"/>
	    </display:column>
	    <display:column title="encryption" sortable="false">
	        <c:out value="${row.encryption}"/>
	    </display:column>
	    <display:column title="Actions">
	        <s:link href="userEdit.action" event="edit">
	            <s:param name="userId" value="${row.id}"/>
	            Edit User
	        </s:link>
			&nbsp;|&nbsp;
	        <s:link href="userEdit.action" event="access">
	            <s:param name="userId" value="${row.id}"/>
	            Access Control
	        </s:link>
			&nbsp;|&nbsp;
	        <s:link href="userEdit.action" event="sendNotify">
	            <s:param name="userId" value="${row.id}"/>
	            Send Notify
	        </s:link>

	    </display:column>
	
	</display:table>
</c:if>
</div>
