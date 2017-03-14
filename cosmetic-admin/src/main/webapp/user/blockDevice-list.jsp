<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<h2 class=ico_mug>User :: User Management</h2>
<div class=clearfix>

<s:form beanclass="${actionBean.class}" method="get">
<td>
	Search Device By Id
	<s:text name="searchId" id="searchById" />
	&nbsp;<s:submit name="list" value="Go"/>
</td>
</s:form>

<display:table id="row" name="actionBean.pageResult.results" requestURI="blockDevice.action" pagesize="20" sort="page" partialList="true" size="actionBean.pageResult.totalSize" export="false" >
    <display:column title="userId" sortable="false">
        <c:out value="${row.userId}"/>
    </display:column>
    <display:column title="uuid" sortable="false">
        <c:out value="${row.uuid}"/>
    </display:column>
    <display:column title="block" sortable="true">
        <c:out value="${row.isBlocked}"/>
    </display:column>
    <display:column title="Actions">
        <s:link href="blockDevice.action" event="block">
            <s:param name="uuid" value="${row.uuid}"/>
            <s:param name="searchId" value="${row.userId}"/>   
            Block Device        
        </s:link>
		&nbsp;|&nbsp;
        <s:link href="blockDevice.action" event="unblock">
            <s:param name="uuid" value="${row.uuid}"/>
            <s:param name="searchId" value="${row.userId}"/>   
            UnBlock Device        
        </s:link>        
    </display:column>

</display:table>
</div>
