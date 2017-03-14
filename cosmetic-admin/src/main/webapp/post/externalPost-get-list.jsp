<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.10.2.min.js" />"></script>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/jquery-ui.min.js" />"></script>
<link href="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/themes/hot-sneaks/jquery-ui.css" />" rel="stylesheet">
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>
<script src="<c:url value="/common/lib/lazyload/jquery.lazyload.js?v=${randVer}" />"></script>
<script src="<c:url value="/common/lib/lazyload/jquery.lazyload.min.js?v=${randVer}" />"></script>
<link rel="stylesheet" type="text/css" href='<c:url value="./../common/theme/backend/styles/style.css"/>' />
<link rel="stylesheet" type="text/css" href='<c:url value="./../common/theme/backend/styles/superfish.css"/>' />
<link rel="stylesheet" type="text/css" href='<c:url value="./../common/theme/backend/styles/screen.css"/>' />
<link rel="stylesheet" type="text/css" href='<c:url value="./../common/theme/backend/styles/jquery-ui-1.10.3.custom.css"/>' />
<script>
	function checkAll(box) {
		var checked = box.checked;
		var allRows = document.getElementsByTagName("input");
		for (var i = 0; i < allRows.length; i++) {
			if (allRows[i].id == 'checkIndexes')
				allRows[i].checked = checked;
		}
	}

	function checkLinkAll(box) {
		var checked = box.checked;
		var allRows = document.getElementsByTagName("input");
		var allLinks = document.getElementsByTagName("a");
		for (var i = 0; i < allRows.length; i++) {
			if (allRows[i].id == 'checkLinkIndexes')
				allRows[i].checked = checked;
		}

		for (var i = 0; i < allLinks.length; i++) {
			if (allLinks[i].id == 'externalLink') {
				if (checked) {
					allLinks[i].disabled = false;
					allLinks[i].setAttribute("href", allLinks[i]
							.getAttribute("link"));
					allLinks[i].style.color = "#477cae";
				} else {
					allLinks[i].disabled = true;
					allLinks[i].removeAttribute('href');
					allLinks[i].style.color = "black";
				}
			}
		}
	}
</script>

<style>
body {
	background: #FFFFFF;
}
</style>

<div align="center" style="with:100%; margin:0 10% 0 10%;">
<display:table style="width:100%;" id="row" name="actionBean.pageResult.results" requestURI="./externalPost.action?getList" requestURIcontext="disable" pagesize="${actionBean.pageSize}" sort="page" partialList="true" size="actionBean.pageResult.totalSize" export="false" >
	<display:setProperty name="basic.show.header" value="true" />
	<display:setProperty name="paging.banner.placement" value="both" />
	<display:setProperty name="paging.banner.group_size" value="10" />
	<display:setProperty name="paging.banner.all_items_found" value='<span class="pagebanner">  {0} {1} in the form to post, displaying all {2}. </span>'/>
	<display:setProperty name="paging.banner.some_items_found" value='<span class="pagebanner"> {0} {1} in the form to post, displaying {2} to {3}. </span>'/>
	<display:setProperty name="paging.banner.full" value='<span style="font-size:18px;" class="pagelinks"> <a href="{2}"><span style="font-size:22px;" class="icon">&#9668;</span></a> {0} <a href="{3}"><span <span style="font-size:22px;" class="icon">&#9658;</span></a></span>'/> 
	<display:setProperty name="paging.banner.first" value='<span style="font-size:18px;" class="pagelinks"> <span style="font-size:22px;" class="icon">&#9668;</span></a> {0} <a href="{3}"><span style="font-size:22px;" class="icon">&#9658;</span></a></span>'/>
	<display:setProperty name="paging.banner.last" value='<span style="font-size:18px;" class="pagelinks"> <a href="{2}"><span style="font-size:22px;" class="icon">&#9668;</span></a> {0} <span style="font-size:22px;" class="icon">&#9658;</span></span>'/>
	<display:column title="Cover" style="width:2%;">
		<div align="center" class="imgDiv">
			<c:if test="${row.cropped eq false}">
                <img src="./../common/theme/backend/images/ico_defualtImg.png" data-original="${row.image}" data-index="${row.index}" data-cropped="${row.cropped}" align="center" style="min-width:29px; min-height:21px; max-width:240px; max-height:240px; width:auto; height:auto;"/>
            </c:if>
			<c:if test="${row.cropped eq true}">
                <img src="${row.croppedImg}" data-original="${row.image}" data-index="${row.index}" data-cropped="${row.cropped}" align="center" style="min-width:29px; min-height:21px; max-width:240px; max-height:240px; width:auto; height:auto;"/>
            </c:if>
		</div>
		<div align="center">
			<input class="original-button" type="button" value="Original Image" style="width: auto;">
		</div>
	</display:column>
	<display:column title="Title" style="width:10%;">
		<div class="titleLink" style="word-break: break-all;">
			<a id="externalLink" style="font-size:20px; line-height: 30px;" href="${row.url}" link="${row.url}" target="_blank">${row.title}</a>
		</div>
		<div class="titleEdit">
			<textarea rows="3" style="resize:none; font-size:16px; width: 100%;">${row.title}</textarea>
		</div>
		<div class="contentDisplay">
			<textarea rows="7" style="resize:none; font-size:16px; width: 100%;" disabled='true' >${row.content}</textarea>
		</div>
		<div class="contentEdit">
			<textarea rows="7" style="resize:none; font-size:16px; width: 100%;">${row.content}</textarea>
		</div>
		<div class="editBar" align="left">
			<input class="save-button" id="saveBtn" type="button" value="Save" style="width: auto;" data-index="${row.index}" >
			<input class="cancel-button" id="cancelBtn" type="button" value="Cancel" style="width: auto;">
			<input class="edit-button" id="editBtn" type="button" value="Edit" style="width: auto;">
		</div>
		<label>&nbsp;</label>
	</display:column>
	<display:column title="<div align='center'><label><input type='checkbox' class='checkLink' name='checkLinkall' onClick='checkLinkAll(this);' style='transform: scale(1.8);' disabled='disabled' checked='checked'/> External Link</label></div>" style="width:0.5%;">
		<div id="checkboxLinkRegion" align="center" style="height:220px; padding: 30px 0px 0px 0px;">
			<input type="checkbox" class="checkLink" id="checkLinkIndexes" name="checkLinkIndexes" value="${row.index}" style="transform: scale(3.5);" disabled="disabled">
			<div id="region" align="center" style="height:100%;" />
		</div>
	</display:column>
	<display:column title="<div align='center'><label><input type='checkbox' name='checkall' onClick='checkAll(this);' style='transform: scale(1.8);' disabled='disabled'/> All</label></div>" style="width:0.5%;">
		<div id="checkboxRegion" align="center" style="height:220px; padding: 30px 0px 0px 0px;">
			<input type="checkbox" id="checkIndexes" name="checkIndexes" value="${row.index}" style="transform: scale(3.5);" disabled="disabled">
			<div id="region" align="center" style="height:100%;" />
		</div>
	</display:column>
</display:table>
</div>