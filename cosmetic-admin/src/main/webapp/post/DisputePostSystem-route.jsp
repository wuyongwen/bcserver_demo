<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000)%></c:set>
<script src="<c:url value="/post/disputePost.js?v=${randVer}" />"></script>
<script src="<c:url value="/post/smartTag.js?v=${randVer}" />"></script>
<script src="<c:url value="/post/spinner/spin.js" />"></script>
<style type="text/css">
a.not-active {
   pointer-events: none;
   cursor: default;
   color: #000;
   font-weight: bold;
}

.availableCircles img.cirSelected {
	box-shadow: 0px 0px 5px 3px #4780AE !important;
}

.availableCircles img {
	box-shadow:none !important;
}

.extCirDiv input.cirSelected {
	background: #375987 !important;
	color: #FFFFFF;
}

.extCirDiv input {
	background: #C9E7E7 !important;
	color: #000000;
}

.fPostTags img.selected {
	box-shadow: 0px 0px 5px 3px #4780AE !important;
}

.fPostTags img {
	box-shadow:none !important;
}

.rating div.rate {
	background-size:100% !important;
	box-shadow:none !important;
	cursor: pointer;
}

.rating div.rate.selected {
	box-shadow: 0px 0px 5px 3px #4780AE !important;
	cursor: pointer;
}

.rating .push {
  	cursor: pointer;
  	color:black;
	background: #FFFFFF;
	border: 1px;
	border-style: solid !important;
    border-color: #AEAEAE !important;
}

.rating .push.selected {
  	cursor: pointer;
  	color:white;
	background: #4780AE;
}

.push div.selected {
	box-shadow: 0px 0px 5px 3px #4780AE !important;
}

.postTags input.selected {
	background: #4780AE !important;
	color: #FFFFFF;
}

.postTags input {
	background: DEDEDE !important;
	color: #000000;
}

#btnMore {
 	border-radius: 7px;
 	border: 2px solid #2e5cb8;
 	font-size:14px;
 	padding: 7px 14px;
 	margin: 0px 0px 0px 5px;
 	float: right;
}

#unCuratedSummaryTable td{
	padding: 5px;
    text-align: left;
}

</style>
<h2 class=ico_mug>Post :: ${actionBean.poolType.description}</h2>
<div class=clearfix>
<div id="displayDiv" style="width:100%;">
<label>Locale :  </label>
<select id="selRegion">
<c:forEach items="${actionBean.availableRegion}" var="region" varStatus="loop">
<c:choose>
	<c:when test="${region eq actionBean.selRegion}">
		<option value="${region}" selected>${region}</option>
	</c:when>
	<c:otherwise>
		<option value="${region}">${region}</option>
	</c:otherwise>
</c:choose>
</c:forEach>
</select>
<label style="font-size:14px;">Page Size : </label>
<select id="pageSizeSel">
<c:forEach items="${actionBean.availablePageSize}" var="pageSize" varStatus="loop">
<c:choose>
	<c:when test="${pageSize eq actionBean.pageSize}">
		<option value="${pageSize}" selected>${pageSize}</option>
	</c:when>
	<c:otherwise>
		<option value="${pageSize}">${pageSize}</option>
	</c:otherwise>
</c:choose>
</c:forEach>
</select>
<c:if test="${actionBean.poolType.filterable}">
<label>Category :  </label>
<select id="selCircleTypeId">
<option value="0">All</option>
<c:forEach items="${actionBean.availableCircleTypes}" var="circleType" varStatus="loop">
<c:choose>
	<c:when test="${circleType.key eq actionBean.selCircleTypeId}">
		<option value="${circleType.key}" selected>${circleType.value}</option>
	</c:when>
	<c:otherwise>
		<option value="${circleType.key}">${circleType.value}</option>
	</c:otherwise>
</c:choose>
</c:forEach>
</select>
</c:if>

<c:if test="${actionBean.poolType.filterCreatorType}">
<label>Type :  </label>
<select id="selCreatorType">
<c:forEach items="${actionBean.creatorType}" var="creatorType" varStatus="loop">
<c:choose>
	<c:when test="${creatorType eq actionBean.selCreatorType}">
		<option value="${creatorType}" selected>${creatorType}</option>
	</c:when>
	<c:otherwise>
		<option value="${creatorType}">${creatorType}</option>
	</c:otherwise>
</c:choose>
</c:forEach>
</select>
</c:if>

<div style="overflow: auto;">
<div id="unCuratedSummaryDiv" style="float:left;font-weight:bold;font-size:16px;">
	<table id="unCuratedSummaryTable"></table>
</div>
<div id="handleCountingDiv" style="float:right;font-weight: bold;font-size: 16px;">
	<button class="button" id="btnMore" onclick="window.open('./DisputePostSystem.action?activity');" style="display: none;">more</button>
</div>
</div>
<c:set var="pagelinksSize" value="10"/>
<div id="disputePostTable" align="center">
<span id="disputePgBar1" class="pagelinks" style="font-size:18px;display:none;" minIdx="1" maxIdx="${pagelinksSize}" pgCount="${pagelinksSize}" > 
	<a class="previousBtn" href=""><span style="font-size:22px;" class="icon"><%="\u25c4"%></span></a> 
		<c:forEach var="idx" begin="1" end="${pagelinksSize}">
			<a class="pgNum" href="" pgIdx="${idx}" title="Go to page ${idx}">${idx}</a>
			<span>
				<c:if test="${idx < pagelinksSize}">,</c:if>
			</span>
		</c:forEach>
	<a class="nextBtn" href=""><span style="font-size:22px;" class="icon"><%="\u25ba"%></span></a>
	<input id="jumpToInput" type="number">
	<input id="jumpToBtn" type="button" value="Go"> 
</span>
<label id="messageLbl" style="font-weight:normal;color:#000000;letter-spacing:1pt;word-spacing:2pt;font-size:27px;text-align:left;font-family:arial, helvetica, sans-serif;line-height:1;"></label>
<table id="row" class="displaytag" style="display:none;border-collapse:collapse;min-width:100%;">
	<tbody>
		<c:choose>
			<c:when test="${actionBean.poolType.multiCategory}">
				<jsp:include page="/post/DisputePostSystem-multi-select.jsp" />
			</c:when>
			<c:otherwise>
				<jsp:include page="/post/DisputePostSystem-single-select.jsp" />
			</c:otherwise>
		</c:choose>
	</tbody>
</table>
<span id="disputePgBar2" class="pagelinks" style="font-size:18px;display:none;" minIdx="1" maxIdx="${pagelinksSize}" pgCount="${pagelinksSize}" > 
	<a class="previousBtn" href=""><span style="font-size:22px;" class="icon"><%="\u25c4"%></span></a> 
		<c:forEach var="idx" begin="1" end="${pagelinksSize}">
			<a class="pgNum" href="" pgIdx="${idx}" title="Go to page ${idx}">${idx}</a>
			<span>
				<c:if test="${idx < pagelinksSize}">,</c:if>
			</span>
		</c:forEach>
	<a class="nextBtn" href=""><span style="font-size:22px;" class="icon"><%="\u25ba"%></span></a>
</span>
</div>
<c:if test="${actionBean.poolType ne 'TrendingTest'}">
	<div align="right" style="with:80%; margin:0 12% 0 10%;" id="saveButtonDiv">
	<button style="font-size:14px;" class="button" id="rescue">Submit</button>
	</div>
</c:if>
</div>
<div id="authorDialog" title="Author property" style="display: none;">
	<div style="min-height:150px;">
		<div style="float:left;width:150px; height:150px; border-radius: 75px; overflow:hidden;">
			<img id="authorAvatar" style="width:100%;height:100%;"/>
		</div>
		<div style="padding-left: 180px;">
			<table style="border-collapse:collapse;border-spacing:0;">
				<tr>
					<td>User ID :</td>
					<td>
						<label id="authorId"/>
					</td>
				</tr>
				<tr>
					<td>Name :</td>
					<td>
						<label id="authorName"/>
					</td>
				</tr>
			</table>
		</div>
	</div>
	<div style="padding-top: 50px;">
		<input id="reportUser" type="button" class="button" value="REPORT USER">
		<br />
	</div>
</div>