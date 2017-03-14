<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>


<h2 class=ico_mug>Post :: External Post Rescue</h2>
<s:form beanclass="${actionBean.class}" method="post">
<div>
	<label>Import JSON File: </label>
	<s:file id="uploadFile" name="jsonFile" accept=".json, application/json"/>
	<s:submit name="post" id="post" value="Post"/>
</div>
</s:form>