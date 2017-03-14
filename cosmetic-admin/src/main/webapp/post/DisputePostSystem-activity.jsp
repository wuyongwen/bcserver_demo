<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!--  <c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>
<script src="<c:url value="/post/spinner/spin.js" />"></script>-->
<style>
th {
	font-size: 18px;
	background-color: #f5f5f5;
}

#summaryTable td {
	align: center;
	padding: 5px;
}

#countTable td ,th{
	border: 1px solid black;
	font-size: 16px;
	text-align: center;
	padding: 5px;
}

h3 {
	font-size: 20px;
	text-align: center;
	padding-bottom: 20px;
}
</style>
<h2 class=ico_mug>Post :: Trending Post Summary</h2>
<div align="center" style="overflow:scroll">
	<table id="summaryTable">
	<tr>
		<c:forEach items="${actionBean.postScoreCountsMap}" var="locales">
		<td>
			<h3>${locales.key}</h3>
			<table id="countTable">
				<th style="min-width: 50px">Date</th>
				<th style="min-width: 35px">T</th>
				<th style="min-width: 35px">C</th>
				<c:forEach items="${locales.value}" var="dates">
					<tr>
					<td>
						<fmt:parseDate pattern="yyyy-MM-dd" value="${dates.key}" var="parsedDate" />
						<fmt:formatDate value="${parsedDate}" pattern="MM/dd" />
					</td>
					<c:forEach items="${dates.value}" var="resultType">
						<c:choose>
						<c:when test="${resultType.key eq 'CatAndTrend'}">
							<td style="font-weight: bold;">${resultType.value}</td>
						</c:when>
						<c:when test="${resultType.key eq 'CatOnly'}">
							<td style="font-weight: bold;">${resultType.value}</td>
						</c:when>
						</c:choose>
					</c:forEach>
					</tr>
				</c:forEach>
			</table>
		</td>
		</c:forEach>
	</tr>
	</table>
</div>
<!-- previous activity summary page -->
<!-- 
<div class=clearfix>
<div style="with:50%; margin:0 10% 0 12%;">
<div style="font-size: 16px;">
	<div style="border-radius: 5px;border: 2px solid #999;min-height:150px;min-width:100%;padding-top:10px;padding-bottom:10px;padding-left:10px;padding-right:10px;">
		<u style="font-weight: bold;">Last Modified :</u><br>
		<div id="lastRecord">
			<c:forEach items="${actionBean.lastActivityRecord}" var="lastRecordMap" varStatus="loop">
				<u>${lastRecordMap.key}</u><br>
				<c:forEach items="${lastRecordMap.value}" var="lastRecord" varStatus="loop">
					${lastRecord}<br>
				</c:forEach>
				<br>
			</c:forEach>
		</div><br>
		<u style="font-weight: bold;">Summary in 2 days :</u><br>
		<div id="summaryDiv">
			<c:forEach items="${actionBean.activitySummary}" var="activitySummaryMap" varStatus="loop">
				<u>${activitySummaryMap.key}</u><br>
				<c:forEach items="${activitySummaryMap.value}" var="summary" varStatus="loop">
					${summary}<br>
				</c:forEach>
				<br>
			</c:forEach>
		</div>
	</div>
</div>
 -->