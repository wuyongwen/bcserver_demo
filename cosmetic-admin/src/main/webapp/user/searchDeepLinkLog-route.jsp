<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>

<h2 class=ico_mug>User :: User Management</h2>
<div class=clearfix>

<s:form beanclass="${actionBean.class}">
<td>
	Device OS
	<s:select name="searchDeviceOS" id="deviceBrowserName">
		<s:option value="">--ALL--</s:option>
		<s:options-collection collection="${actionBean.deviceOSList}" />
	</s:select>
	&nbsp;|&nbsp;Device Browser
	<s:select name="searchDeviceBrowser" id="deviceBrowserName">
		<s:option value="">--ALL--</s:option>
		<s:options-collection collection="${actionBean.deviceBrowserList}" />
	</s:select>
	&nbsp;|&nbsp;Device App
	<s:select name="searchDeviceApp" id="deviceAppName">
		<s:option value="">--ALL--</s:option>
		<s:options-collection collection="${actionBean.deviceAppList}" />
	</s:select>
	&nbsp;|&nbsp;Referrer
	<s:select name="searchReferrer" id="ReferrerId">
		<s:option value="">--ALL--</s:option>
		<s:options-collection collection="${actionBean.referrerList}" />
	</s:select>
	&nbsp;|&nbsp;Fry
	<s:select name="searchFry" id="FryId">
		<s:option value="">--ALL--</s:option>
		<s:options-collection collection="${actionBean.fryList}" />
	</s:select>
</td>
<s:param name="deviceOSList" value="${actionBean.deviceOSList}" />
<s:param name="deviceBrowserList" value="${actionBean.deviceBrowserList}" />
<s:param name="deviceAppList" value="${actionBean.deviceAppList}" />
<s:param name="referrerList" value="${actionBean.referrerList}" />
<s:param name="fryList" value="${actionBean.fryList}" />
<s:param name="isSearch" value="1" />
<s:submit name="route" value="Search"/>
</s:form>
<c:if test="${actionBean.isSearch eq true}">
	<display:table id="row" name="actionBean.pageResult.results" requestURI="searchDeepLinkLog.action" pagesize="20" sort="page" partialList="true" size="actionBean.pageResult.totalSize" export="false" >
	    <display:column title="userId" sortable="true">
	        <c:out value="${row.id}"/>
	    </display:column>
	    <display:column title="Platform" sortable="true">
	        <c:out value="${row.platform}"/>
	    </display:column>
	    <display:column title="Browser" sortable="true">
	        <c:out value="${row.browser}"/>
	    </display:column>
	    <display:column title="AppName" sortable="true">
	        <c:out value="${row.appName}"/>
	    </display:column>
	    <display:column title="AppUrl" sortable="false">
	        <c:out value="${row.appUrl}"/>
	    </display:column>
	    <display:column title="Referrer" sortable="false">
	        <c:out value="${row.referrer}"/>
	    </display:column>
		<display:column title="Fry" sortable="false">
	        <c:out value="${row.fry}"/>
	    </display:column>
	</display:table>
</c:if>
</div>
