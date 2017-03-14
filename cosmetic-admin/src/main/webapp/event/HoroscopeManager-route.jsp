<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/jquery-ui.min.js" />"></script>
<script src="<c:url value="/common/lib/general.js" />"></script>

<h2 class=ico_mug>Even :: Horoscope Management</h2>
<div>
	Search By Locale : 
    <select name="actionBean.locale" id="locale" onchange="location.href=location.pathname+'?locale='+this.value;" >
	    <c:forEach items="${actionBean.availableLocale}" var="loc">
	    	<option value="${loc}" ${loc == actionBean.locale ? 'selected' : ''}>${loc}</option>
	    </c:forEach>
    </select>
    &nbsp;&nbsp;&nbsp;&nbsp;
	<input id="create" type="button" class="button" value="Create New Horoscope" onclick="location.href='HoroscopeManager.action?modify'" />
	<display:table id="row" name="actionBean.horoscopeList" requestURI="HoroscopeManager.action" pagesize="20" sort="page" partialList="true" size="${fn:length(actionBean.horoscopeList)}" export="false" >
		<display:column title="Id" sortable="true" style="width:10%;">
			<c:out value="${row.id}" />
		</display:column>
		<display:column title="Locale" sortable="true" style="width:5%;">
			<c:out value="${row.locale}" />
		</display:column>
		<display:column title="Post Id" sortable="true" style="width:10%;">
			<a href="javascript:window.open('../post/queryPost.action?postId=${row.postId}')">${row.postId}</a>
		</display:column>
		<display:column title="Title" sortable="true" style="width:20%;">
			<c:out value="${row.title}" />
		</display:column>
		<display:column title="Image Url" sortable="true" style="width:30%;">
			<a href="javascript:window.open('${row.imageUrl}');"><img src="${row.imageUrl}" alt="${row.imageUrl}" height="300"></a>
		</display:column>
		<display:column title="Action" sortable="false" style="width:15%;">
			<input id="modify" type="button" class="button" value="Modify" onclick="location.href='HoroscopeManager.action?modify&horoscopeId=${row.id}'" />
			&nbsp; | &nbsp;
			<input id="delete" type="button" class="button" value="Delete" onclick="if(confirm('Are you sure you want to delete ${row.id}?')){location.href='HoroscopeManager.action?delete&horoscopeId=${row.id}&locale=${row.locale}'}" />
		</display:column>
	</display:table>
</div>