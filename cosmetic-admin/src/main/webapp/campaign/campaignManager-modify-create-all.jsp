<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>
<script src="<c:url value="/common/lib/general.js" />"></script>
<script src="<c:url value="/common/lib/json/json2.js" />"></script>
<script src="<c:url value="/campaign/campaignManager-create-all.js?v=${randVer} " />"></script>
<script src="<c:url value="/common/lib/timepicker/jquery-ui-timepicker-addon.js" />"></script>
<script src="<c:url value="/common/lib/timepicker/jquery-ui-sliderAccess.js" />"></script>
<link href="<c:url value="/common/lib/timepicker/jquery-ui-timepicker-addon.css" />" rel="stylesheet">

<h2 class=ico_mug>Campaign :: Campaign Management</h2>
<div>
	<s:form beanclass="${actionBean.class}">
	<input type="hidden" id="locales" value="${actionBean.locales}">
	<display:table id="row" name="actionBean.campaignGroupList" requestURI="campaignManager.action" pagesize="10" sort="page" partialList="true" size="${fn:length(actionBean.campaignGroupList)}" export="false" >
		<display:column title="Locale" style="width:60px;">
		 	<label id="${row.locale}" >${row.locale}</label>
		 	<input type="hidden" id="campaignGroupId-${row.locale}" value="${row.id}">
		</display:column>
		<display:column title="Campaigns" style="width:60px;">
			<table class="form" width="100%">
				<tr style="display:none;">
		          <td>File:
		          	<input id="campFileInput-${row.locale}" name="file" locale="${row.locale}" index="${(row_rowNum-1)*3}" type="file" accept="image/*">
		          	<s:text name="campaign.fileId" id="fileId-${row.locale}" value="${actionBean.campaign.fileId}"/>
		          </td>
		        </tr>
		        <tr style="display:none;">
		          <td>
		          	<div style=" height:200px; overflow:hidden;">
		          		<img id="imgfile-${row.locale}" style="max-height:98%;min-height:98%;border-color:#000000;border-width:3px;border-style:solid;" src="${actionBean.imgfileOriginalUrl}"/>
		       		</div>
		     	  </td>
		        </tr>
		        <tr>
		          <td>File 720P:
		          	<input id="campFile720Input-${row.locale}" name="file-${row.locale}" locale="${row.locale}" index="${(row_rowNum-1)*3 + 1}" type="file" accept="image/*">
		          	<s:text name="campaign.file720Id" id="file720Id-${row.locale}" value="${actionBean.campaign.file720Id}"/>
		          </td>
		        </tr>
		        <tr>
		          <td>
		          	<div style=" height:200px; overflow:hidden;">
		          		<img id="imgfile720-${row.locale}" style="max-height:98%;min-height:98%;border-color:#000000;border-width:3px;border-style:solid;" src="${actionBean.imgfile720OriginalUrl}"/>
		       		</div>
		     	  </td>
		        </tr>
		        <tr>
		          <td>File 1080P:
		          	<input id="campFile1080Input-${row.locale}" name="file" type="file" locale="${row.locale}" index="${(row_rowNum-1)*3 + 2}" accept="image/*">
		          	<s:text name="campaign.file1080Id" id="file1080Id-${row.locale}" value="${actionBean.campaign.file1080Id}"/>
		          </td>
		        </tr>
		        <tr>
		          <td>
		          	<div style="height:200px; overflow:hidden;">
		          		<img id="imgfile1080-${row.locale}" style="max-height:98%;min-height:98%;border-color:#000000;border-width:3px;border-style:solid;" src="${actionBean.imgfile1080OriginalUrl}"/>
		       		</div>
		     	  </td>
		        </tr>
		        <tr>
		        	<td>Link :
		          		<input type="text" name="campaign.link" id="link-${row.locale}" style="width:600px" placeholder="" value=""/>
					</td>
		        </tr>
		        <tr>
		        	<td>End Date :
		          		<input type="text" id="datetimepicker-${row.locale}" name="campaign.endDate" locale="${row.locale}" value="" style="width:200px;">
		          		<span id="hint-${row.locale}"></span>
					</td>
		        </tr>
		    </table>
		</display:column>
	</display:table>
	<div style="text-align:center;" >
		<s:submit id="saveFileEdit" name="createAll" class="button" value="Create" />
		&nbsp;&nbsp;&nbsp;&nbsp;
		<s:submit name="cancel" class="button" value="Cancle" />
	</div>
	<input type="hidden" name="campaignGroupName" value="${actionBean.campaignGroupName}"/>
	<input type="hidden" name="campaignListJsonString" id="campaignListJsonString" value="ss"/>
    <div id="editProgress"></div>
    </s:form>
</div>