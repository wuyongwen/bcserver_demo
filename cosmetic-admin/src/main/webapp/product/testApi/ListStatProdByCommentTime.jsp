<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>List statistic result by period</title>
</head>
<body>
	<fieldset>
	<legend>List statistic result by period</legend>
	<s:form beanclass="com.cyberlink.cosmetic.action.backend.product.ListStatProdByCommentTimeAction">
		<label>startTime <s:text name="startTime" size="20" value=""/></label><br/>
		<label>endTime <s:text name="endTime" size="20" value=""/></label><br/>
		<label>offset <s:text name="offset" size="20" value="0"/></label><br/>
		<label>limit <s:text name="limit" size="20" value="100"/></label><br/>
		<s:submit name="submit" value="Submit"/>
	</s:form>
	</fieldset>
</body>
</html>