<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Create Circle</title>
</head>
<body>
<h3>Create Form</h3>
<hr/>
<fieldset>
<legend>Init</legend>
<s:form beanclass="${actionBean.class}">
	<label>Table Name:</label>
    <select name="tableName">
	    <c:forEach var='tableName' items='${actionBean.listStringTable}'>
	    	<option>${tableName}</option>    	
 		</c:forEach>
	</select><br/><br/> 
	<label>New Circle Type Name: <s:text name="circleTypeName" value="" size="20"/></label><br/>
	<label>Region: <s:text name="locale" value="" size="20"/></label><br/><br/>
	<label>Circle Type:</label>
    <select>
	    <c:forEach var='tableName' items='${actionBean.listStringCircleType}'>
	    	<option>${tableName}</option>    	
 		</c:forEach>
	</select><br/><br/>
		
	<label>Circle Name: <s:text name="circleName" value="" size="20"/></label><br/>
	<label>Circle Type ID: <s:text name="circleTypeId" size="20" value=""/></label><br/><br/>	
	<label>Circle Tag Group Name: <s:text name="circleTagGroupName" size="20" value=""/></label><br/>
	<label>Circle ID: <s:text name="circleId" size="20" value=""/></label><br/><br/>		
	<label>Circle Tag Name: <s:text name="circleTagName" size="20" value=""/></label><br/>
    <label>Circle Tag Group ID: <s:text name="circleTagGroupId" size="20" value=""/></label><br/>
	<s:submit name="submit" value="Submit"/>
</s:form>
</fieldset>
</body>
</html>