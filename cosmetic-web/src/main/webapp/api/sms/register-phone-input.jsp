<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Register Phone</title>
</head>
<body>
<h3>Register Phone Form</h3>
<hr/>
<fieldset>
    <legend>Form</legend>
	<s:form beanclass="${actionBean.class}">
		<input type="hidden" name="uuid" value="${actionBean.uuid}" />
		<label>regionCode: <s:text name="regionCode" value="TW" size="5" /></label><br/>
        <label>countryCode: <s:text name="countryCode" value="886" size="5"/></label><br/>
	    <label>phoneNumber: <s:text name="phoneNumber" value="912345678" size="50"/></label><br/>
	    <s:submit name="submit" value="Submit"/>
	</s:form>
</fieldset>
</body>
</html>
