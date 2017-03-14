<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<h2 class=ico_mug>User :: User Management</h2>
<div class=clearfix>

<s:form beanclass="${actionBean.class}" method="get">
<input name="userId" type="text" value="${actionBean.userId}" />
<input name="curUserId" type="text" value="${actionBean.curUserId}" />
<s:submit name="route" value="Search" />
<display:table id="row" name="actionBean.circles.results" requestURI="./list-circle-by-user.action" pagesize="20" sort="page" partialList="true" size="actionBean.circles.totalSize" export="false" >
    <display:column title="Circle Id" sortable="true">
        <c:out value="${row.id}"/>
    </display:column>
    <display:column title="circle Name" sortable="true">
        <c:out value="${row.circleName}"/>
    </display:column>
    <display:column title="Icon Url" sortable="false">
        <img src="${row.iconUrl}" height="50" width="50" />
    </display:column>
    <display:column title="Post Thumbnails" sortable="true" sortName="displayName">
    	<c:forEach items="${row.postThumbnails}" var="circle" varStatus="loop">
        	<img src="${circle}" height="50" width="50" />
        </c:forEach>
    </display:column>
    <display:column title="Post Count" sortable="true" sortName="displayName">
    	<c:out value="${row.postCount}"/>
    </display:column>
</display:table>
</s:form>