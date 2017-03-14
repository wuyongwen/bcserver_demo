<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>
<script src="<c:url value="/post/externalPost.js?v=${randVer}" />"></script>
<script src="<c:url value="/post/spinner/spin.js" />"></script>

<script src="<c:url value="/common/lib/cropper/cropper.js" />"></script>
<script src="<c:url value="/post/externalPostCrop.js?v=${randVer}" />"></script>
<link href="<c:url value="/common/lib/cropper/cropper.css" />" rel="stylesheet">
<link href="<c:url value="/common/lib/cropper/cropper.min.css" />" rel="stylesheet">
<link href="<c:url value="/post/externalPost.css" />" rel="stylesheet">

<h2 class=ico_mug>Post :: External Post</h2>
<div class=clearfix>
<div id="displayDiv" align="left" style="width:95%;">
<div align="left" style="width:80%; margin:0 0 0 10%;" id="nextStepDiv">
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
</div>


<iframe id="articleTable" src="./externalPost.action?getList&pageSize=${actionBean.pageSize}" style="width:100%; height:auto;" scrolling="no" seamless="seamless">
</iframe>
<div align="right" style="width:80%; margin:0 12% 0 10%;" id="nextStepDiv">
	<button style="font-size:14px;" class="button" id="autoPostConfig">Next Step</button>
</div>
<div id="imageSelection">
	</br>
	</br>
	<table style="vertical-align:middle" align="center">
	<tr align="center">
		<td class="selectedTd" style="width:450px; border-style:solid; border-width:0px; border-color:#FF99C1">
			<img id="autoDetectionImg1" src="" croppedZone="" style="max-width:400px; max-height:600px; width:auto; height:auto; ">
		</td>
		<td class="selectedTd" style="width:450px; border-style:solid; border-width:0px; border-color:#FF99C1">
			<img id="autoDetectionImg2" src="" croppedZone="" style="max-width:400px; max-height:600px; width:auto; height:auto; ">
		</td>
		<td class="selectedTd" style="width:450px; border-style:solid; border-width:0px; border-color:#FF99C1">
			<img id="autoDetectionImg3" src="" croppedZone="" style="max-width:400px; max-height:600px; width:auto; height:auto; ">
		</td>
	</tr>
	</table>
	<div align="center" style="padding: 15px 10px 5px 10px;">
		<input class="action-button" type="button" id="confirmSelectedImage" value="Confirm&Save" style="width: auto; font-size:14px;">
		<input class="action-button" type="button" id="doCroppedImage" value="Manual Crop" style="width: auto; font-size:14px;">
	</div>
	<div id="croppingArea" class="img-container">
		<input class="action-button" type="button" id="confirmCroppedImage" value="Confirm&Save" style="width: auto; font-size:14px;"><br>
		<img class="cropper" alt="Picture">
	</div>
	<div id="croppingSelection" class="img-container" style="visibility:hidden; width:0px; height:0px;" >
		<img class="cropper" alt="Picture">
	</div>
</div>
<div id="originalDiv" align="center">
	<div align="left">
		<input class="action-button" type="button" id="confirmOriginalImage" value="Save" style="width: auto; font-size:14px;"><br>
	</div>
	<img id="originalImage" src="" style="width:auto; height:auto;">
</div>
<div id="editProgress"></div>
<div id="rescueProgress"/></div>
