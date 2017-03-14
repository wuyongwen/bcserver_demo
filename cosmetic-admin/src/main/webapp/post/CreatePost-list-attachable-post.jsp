<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/decorators/backend/styles.jsp"%>

<display:table id="row" name="actionBean.pageResult.results" requestURI="CreatePost.action" pagesize="20" sort="external" partialList="true" size="actionBean.pageResult.totalSize" export="false" >
    <display:column title="Post Title">
    	 <input type="checkbox" name="checkboxChoices" value="${row.postId}" text="${row.title}"><c:out value="${row.title}"/></input>
    </display:column>
</display:table>
