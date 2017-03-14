<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>

<h2 class=ico_mug>File :: Copy Files from production bucket to test bucket</h2>

<div>
    <s:form beanclass="${actionBean.class}" method="post">
        File Item Created Time: <s:text name="startTime" formatPattern="yyyy/MM/dd HH:mm:ss" />&nbsp;~&nbsp;<s:text name="endTime" formatPattern="yyyy/MM/dd HH:mm:ss" />
        &nbsp;<s:submit name="copyfiles" value="Submit"/>
    </s:form>  
    <c:if test="${actionBean.result != null}">
    <hr style="width:100%" />
	    <c:forEach var="detail" items="${actionBean.result}">
	        <div>${detail}</div>    
	    </c:forEach>
    </c:if>
</div>