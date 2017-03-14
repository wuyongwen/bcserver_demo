<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<h2 class=ico_mug>User :: Delete User</h2>

<s:form beanclass="${actionBean.class}" method="post">
	<label>Please input User Id: </label>
	<s:text name="userId" id="userId" />
	&nbsp;<s:submit name="delete" value="delete"/>
</s:form>