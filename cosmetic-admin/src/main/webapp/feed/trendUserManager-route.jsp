<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%><%@ include
	file="/common/taglibs.jsp"%>

<h2 class=ico_mug>Feed :: Trend User Manager</h2>
<div class=clearfix>
	<s:form beanclass="${actionBean.class}" method="get">
		<label>Shard Id: </label>
		<s:select name="selShard" id="selShard">
			<s:option value="">--ALL--</s:option>
			<s:options-collection collection="${actionBean.shardList}" />
		</s:select>
		&nbsp;&nbsp;
		<input type="button" id="usersBtn" value="List User" style="width: auto;">
	</s:form>	
</div>

<div class=clearfix>
	<label>User Id : </label>
	<input type="text" id="categoryInput" size="30">
	<input type="button" id="categoryBtn" value="List Category" style="width: auto;">
</div>

<div class=clearfix>
	<label>User Id : </label>
	<input type="text" id="groupInput" size="30">
	<input type="button" id="groupBtn" value="Get Group" style="width: auto;">
</div>
<br>
<br>
<div class=clearfix id="resultDiv">
	
</div>

<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>
<script src="<c:url value="/feed/trendUser.js?v=${randVer}" />"></script>