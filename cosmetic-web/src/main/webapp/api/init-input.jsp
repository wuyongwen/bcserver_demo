<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Init</title>
</head>
<body>
<h3>Init Form</h3>
<hr/>
<fieldset>
<legend>Init</legend>
<s:form beanclass="${actionBean.class}">
	<label>ap: <s:text name="ap" value="U" size="20"/></label><br/>
	<label>version: <s:text name="version" value="1.0" size="20"/></label><br/>
	<label>versionType: <s:text name="versionType" value="Trial" size="20"/></label><br/>
	<label>buildNumber: <s:text name="buildNumber" size="20" value="1234"/></label><br/>	
	<label>locale: <s:text name="locale" size="20" value="en_US"/></label><br/>
	<label>model: <s:text name="model" size="20" value="HTC-M8"/></label><br/>
    <label>vender: <s:text name="vender" size="20" value="HTC"/></label><br/>
    <label>resolution: <s:text name="resolution" size="20" value="480x800"/></label><br/>
	<label>uuid: <s:text name="uuid" size="40" value="1b2e8b60-df5b-11e3-92c6-0002a5d5c51b"/></label><br/>  	
	<label>apiVersion: <s:text name="apiVersion" size="5" value="1.0"/></label><br/>
    <label>userAgent <s:text name="userAgent" size="50" value=""/></label><br/>
    <label>token: <s:text name="token" size="20" value=""/></label><br/>
    <label>apnsToken: <s:text name="apnsToken" size="50" value=""/></label/><br/>
	<s:submit name="submit" value="Submit"/>
</s:form>
</fieldset>
</body>
</html>