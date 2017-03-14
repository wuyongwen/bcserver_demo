<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/jquery-ui.min.js" />"></script>
<script src="<c:url value="/common/lib/general.js" />"></script>

<h2 class=ico_mug>Even :: Beauty Buzz Management</h2>
<div>
	Search By Locale : 
    <select name="actionBean.locale" id="locale" onchange="location.href=location.pathname+'?locale='+this.value;" >
	    <c:forEach items="${actionBean.availableLocale}" var="loc">
	    	<option value="${loc}" ${loc == actionBean.locale ? 'selected' : ''}>${loc}</option>
	    </c:forEach>
    </select>
    &nbsp;&nbsp;&nbsp;&nbsp;
	<input id="create" type="button" class="button" value="Create New Beauty Insight" onclick="location.href='EventBeautyInsightManager.action?modify&isCreate=true'" />
	<display:table id="row" name="actionBean.beautyInsightList" requestURI="EventBeautyInsightManager.action" pagesize="20" sort="page" partialList="true" size="${fn:length(actionBean.beautyInsightList)}" export="false" >
		<display:column title="Id" sortable="true" style="width:15%;">
			<c:out value="${row.id}" />
		</display:column>
		<display:column title="Locale" sortable="true" style="width:5%;">
			<c:out value="${row.locale}" />
		</display:column>
		<display:column title="ImgUrl" sortable="true" style="width:20%;">
			<a href="javascript:window.open('${row.imgUrl}');"><img src="${row.imgUrl}" alt="${row.imgUrl}" height="300"></a>
		</display:column>
		<display:column title="RedirectUrl" sortable="true" style="width:15%;">
		 	<a href="javascript:window.open('${row.redirectUrl}');">${row.redirectUrl}</a>
		</display:column>
		<display:column title="Description" sortable="true" style="width:15%;">
			<c:out value="${row.description}" />
		</display:column>
		<display:column title="Meta Data" sortable="true" style="width:10%;">
			<c:out value="${row.metadata}" />
		</display:column>
		<display:column title="Action" sortable="false" style="width:15%;">
			<input id="create" type="button" class="button" value="Modify" onclick="location.href='EventBeautyInsightManager.action?modify&isUpdate=true&beautyInsightId=${row.id}'" />
			&nbsp; | &nbsp;
			<input id="create" type="button" class="button" value="Delete" onclick="if(confirm('Are you sure you want to delete?')){location.href='EventBeautyInsightManager.action?delete&beautyInsightId=${row.id}&locale=${row.locale}'}" />
		</display:column>
	</display:table>
</div>