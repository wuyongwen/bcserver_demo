<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>

<h2 class=ico_mug>File :: File Management</h2>

<s:form beanclass="${actionBean.class}" method="get">
    FileId <s:text name="fileId" />
    <s:submit name="list" value="Go"/>
</s:form>

<display:table id="row" name="actionBean.pageResult.results" requestURI="file-manage.action" pagesize="20" sort="page" partialList="true" size="actionBean.pageResult.totalSize" export="false" >
    <display:column title="fileItemId">
        <c:out value="${row.id}"/>
    </display:column>
    <display:column title="file">
        <c:choose>
            <c:when test="${row.file.fileType.isImage}">
               <img src="${row.originalUrl}" height="50" width="50" />        
            </c:when>
            <c:otherwise>
               <a href="${row.originalUrl}" target="_blank">Download</a>
            </c:otherwise>    
        </c:choose>
    </display:column>
    <display:column title="fileId">
        <c:out value="${row.file.id}"/>
    </display:column>
    <display:column title="fileType">
        <c:out value="${row.file.fileType}"/>
    </display:column>
    <display:column title="isOriginal">
        <c:out value="${row.isOriginal}"/>
    </display:column>
    <display:column title="thumbnailType">
        <c:out value="${row.thumbnailType}"/>
    </display:column>
    <display:column title="filePath">
        <c:out value="${row.filePath}"/>
    </display:column>
    <display:column title="fileSize">
        <fmt:formatNumber value="${row.fileSize/1024.0}" pattern="#,##0" /> KB
    </display:column>
    <display:column title="width">
        <c:out value="${row.width}"/>
    </display:column>
    <display:column title="height">
        <c:out value="${row.height}"/>
    </display:column>
    <display:column title="createdTime">
        <fmt:formatDate value="${row.createdTime}" pattern="yyyy-MM-dd HH:mm:ss" />
    </display:column>      
    <display:column title="Action">
        <s:link beanclass="${actionBean.class}" event="query" target="_blank">
            <s:param name="fileId" value="${row.file.id}"/>
            See Detail
        </s:link>
    </display:column>
</display:table>