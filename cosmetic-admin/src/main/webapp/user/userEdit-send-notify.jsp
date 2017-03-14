<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<h2 class=ico_mug>User :: Send Notify</h2>
<div class=clearfix>
<s:form beanclass="${actionBean.class}">
	<tr>
    	<td>User ID:</td>
    	<td>${actionBean.user.id}</td><!-- (2) -->
	</tr>
	<br/>
	<tr>
    	<td>Device Os:</td>
    	<td>${actionBean.device.deviceType}</td><!-- (2) -->
	</tr>
	<br/>
	<tr>
    	<td>Device Token:</td>
    	<td>${actionBean.device.apnsToken}</td><!-- (2) -->
	</tr>
	<br/>
	<tr>
    	<td>Device App:</td>
    	<td>${actionBean.device.app}</td><!-- (2) -->
	</tr>
	<br/>
	<tr>
    	<td>Title:</td>
    	<s:text name="notifyTitle"/><!-- (2) -->
	</tr>
	<br/>
	<tr>
    	<td>Text:</td>
    	<s:text name="notifyText" style="width:200px;"/><!-- (2) -->
	</tr>
	<br/>
    <s:submit name="send" value="Send">
    	Send
    	<s:param name="userId" value="${actionBean.userId}"/>
    </s:submit>
    <!-- (3) -->
    <s:submit name="cancel" value="Cancel"/>

</s:form>

</div>

