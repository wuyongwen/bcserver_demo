<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%><%@ include
	file="/common/taglibs.jsp"%>

<h2 class=ico_mug>Feed :: Import Trending</h2>
<div class=clearfix>
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
	<label>Category :  </label>
	<select id="selCircleTypeId">
		<option value="0">All</option>
		<c:forEach items="${actionBean.availableCircleTypes}" var="circleType" varStatus="loop">
		<c:choose>
			<c:when test="${circleType.id eq actionBean.selCircleTypeId}">
				<option value="${circleType.id}" selected>${circleType.circleTypeName}</option>
			</c:when>
			<c:otherwise>
				<option value="${circleType.id}">${circleType.circleTypeName}</option>
			</c:otherwise>
		</c:choose>
		</c:forEach>
	</select>
	
	<input type="button" id="importBtn" value="Import" style="width: auto;">
</div>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>

<script type="text/javascript">
	$(document).ready(function() {
		$("#selRegion").change(function() {
			window.location.href = "./importTrending.action" + "?selRegion=" + $("#selRegion").val();
			
		});
		
		$("#importBtn").click(function() {
			var url = "importTrending.action?importTrend";
			var selRegion = $("#selRegion option:selected").val();
			var selCircleTypeId = $("#selCircleTypeId option:selected").val();
			var data = "selRegion=" + selRegion + "&selCircleTypeId=" + selCircleTypeId;
			$.post(url, data, function(responseJson) {
				alert("Task doing please check db");
			}).fail(function(e) {
				alert("Failed : " + e.status + " " + e.statusText + "\nData: " + data);
			});
		});
	});
</script>